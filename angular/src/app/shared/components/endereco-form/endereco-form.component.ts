import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {of, Subscription} from 'rxjs';
import {catchError, debounceTime, distinctUntilChanged, filter, switchMap, tap} from 'rxjs/operators';
import {cepValidator} from '../../validators/cep.validator';
import {telefoneValidator} from '../../validators/telefone.validator';
import {VALIDATION} from '../../validators/validation-constants';
import {ViaCepService} from '../../services/via-cep.service';
import {principalArrayValidator} from "../../validators/principal.validator";
import {UnidadesFederativasService} from '../../../api/api/unidadesFederativas.service';
import {MunicipiosService} from '../../../api/api/municipios.service';
import {UnidadeFederativaDTO} from '../../../api/model/unidadeFederativaDTO';
import {MunicipioDTO} from '../../../api/model/municipioDTO';

@Component({
  selector: 'app-endereco-form',
  templateUrl: './endereco-form.component.html',
  styleUrls: ['./endereco-form.component.scss'],
})
export class EnderecoFormComponent implements OnInit, OnDestroy {
  form!: FormGroup;
  ufs: UnidadeFederativaDTO[] = [];
  municipiosCache = new Map<string, MunicipioDTO[]>();
  loadingMunicipios = false;
  private sub = new Subscription();

  constructor(
    private fb: FormBuilder,
    private viaCepService: ViaCepService,
    private ufService: UnidadesFederativasService,
    private municipioService: MunicipiosService,
  ) {
  }

  get enderecos(): FormArray {
    return this.form.get('enderecos') as FormArray;
  }

  getMunicipios(ufSigla: string): MunicipioDTO[] {
    return this.municipiosCache.get(ufSigla) || [];
  }

  private loadMunicipios(ufSigla: string): void {
    if (!ufSigla || this.municipiosCache.has(ufSigla)) return;
    this.loadingMunicipios = true;
    this.sub.add(
      this.municipioService.municipiosFindByUf(ufSigla).pipe(
        catchError(() => of([]))
      ).subscribe((municipios: MunicipioDTO[]) => {
        this.municipiosCache.set(ufSigla, municipios);
        this.loadingMunicipios = false;
      })
    );
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      enderecos: this.fb.array([], [principalArrayValidator()]),
    });
    this.addEndereco(true);

    this.sub.add(
      this.ufService.unidadesFederativasFindAll().pipe(
        catchError(() => of([]))
      ).subscribe((ufs) => {
        this.ufs = ufs;
      })
    );
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  getFormArray(): FormArray {
    return this.enderecos;
  }

  createEnderecoGroup(principal: boolean): FormGroup {
    const group = this.fb.group({
      logradouro: ['', [Validators.required, Validators.minLength(VALIDATION.LOGRADOURO_MIN), Validators.maxLength(VALIDATION.LOGRADOURO_MAX)]],
      numero: [null, [Validators.required, Validators.min(1)]],
      bairro: ['', [Validators.required, Validators.minLength(VALIDATION.BAIRRO_MIN), Validators.maxLength(VALIDATION.BAIRRO_MAX)]],
      cep: ['', [Validators.required, cepValidator()]],
      ufSigla: ['', Validators.required],
      municipioId: [null as number | null, Validators.required],
      telefone: ['', telefoneValidator()],
      principal: [principal],
      complemento: ['', [Validators.maxLength(VALIDATION.COMPLEMENTO_MAX)]],
    });

    this.sub.add(
      group.get('ufSigla')!.valueChanges.pipe(
        filter((sigla): sigla is string => !!sigla || sigla === ''),
        distinctUntilChanged(),
        tap((sigla: string) => this.loadMunicipios(sigla))
      ).subscribe()
    );

    this.sub.add(
      group.get('cep')!.valueChanges.pipe(
        debounceTime(500),
        filter((v: string | null): v is string => !!v && v.replace(/\D/g, '').length === 8),
        distinctUntilChanged(),
        switchMap((value: string) =>
          this.viaCepService.lookup(value).pipe(catchError(() => of({erro: true} as any)))
        ),
      ).subscribe((result) => {
        if (result && !result.erro) {
          const patch: Record<string, any> = {};
          if (result.logradouro) patch['logradouro'] = result.logradouro;
          if (result.bairro) patch['bairro'] = result.bairro;
          if (result.uf) patch['ufSigla'] = result.uf;
          if (result.complemento) patch['complemento'] = result.complemento;
          group.patchValue(patch);

          if (result.uf && result.ibge) {
            this.loadMunicipios(result.uf);
            const ibge = Number(result.ibge);
            setTimeout(() => group.patchValue({municipioId: ibge as number | null}));
          }
        }
      })
    );

    this.sub.add(
      group.get('principal')?.valueChanges?.subscribe(isPrincipal => {
        if (isPrincipal) {
          this.unsetOtherPrincipals(group);
        }
      })
    )

    return group;
  }

  addEndereco(principal: boolean = false): void {
    this.enderecos.push(this.createEnderecoGroup(principal));
  }

  removeEndereco(index: number): void {
    if (this.enderecos.length > 1) {
      this.enderecos.removeAt(index);
    }
  }

  getFieldError(groupIndex: number, field: string): string {
    const control = this.enderecos.at(groupIndex).get(field);
    if (!control?.errors) return '';
    if (control.errors['required']) return 'Campo é necessário.';
    if (control.errors['minlength']) return `Mín. ${control.errors['minlength'].requiredLength} caracteres`;
    if (control.errors['maxlength']) return `Máx. ${control.errors['maxlength'].requiredLength} caracteres`;
    if (control.errors['cep']) return control.errors['cep'];
    if (control.errors['telefone']) return control.errors['telefone'];
    if (control.errors['min']) return 'Valor inválido';
    return 'Inválido';
  }

  private unsetOtherPrincipals(currentGroup: FormGroup) {
    const enderecosArray = this.form.get('enderecos') as FormArray;

    enderecosArray.controls.forEach(control => {
      if (control !== currentGroup) {
        control.get('principal')?.setValue(false, {emitEvent: false});
      }
    });
  }
}
