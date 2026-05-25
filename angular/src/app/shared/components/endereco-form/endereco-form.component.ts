import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {of, Subscription} from 'rxjs';
import {catchError, debounceTime, distinctUntilChanged, filter, switchMap} from 'rxjs/operators';
import {cepValidator} from '../../validators/cep.validator';
import {telefoneValidator} from '../../validators/telefone.validator';
import {VALIDATION} from '../../validators/validation-constants';
import {ViaCepService} from '../../services/via-cep.service';
import {principalArrayValidator} from "../../validators/principal.validator";

@Component({
  selector: 'app-endereco-form',
  templateUrl: './endereco-form.component.html',
  styleUrls: ['./endereco-form.component.scss'],
})
export class EnderecoFormComponent implements OnInit, OnDestroy {
  form!: FormGroup;
  private sub = new Subscription();

  constructor(
    private fb: FormBuilder,
    private viaCepService: ViaCepService,
  ) {
  }

  get enderecos(): FormArray {
    return this.form.get('enderecos') as FormArray;
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      enderecos: this.fb.array([], [principalArrayValidator()]),
    });
    this.addEndereco(true);
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
      cidade: ['', [Validators.required, Validators.minLength(VALIDATION.CIDADE_MIN), Validators.maxLength(VALIDATION.CIDADE_MAX)]],
      estado: ['', [Validators.required, Validators.minLength(VALIDATION.ESTADO_LENGTH), Validators.maxLength(VALIDATION.ESTADO_LENGTH)]],
      telefone: ['', telefoneValidator()],
      principal: [principal],
      complemento: ['', [Validators.maxLength(VALIDATION.COMPLEMENTO_MAX)]],
    });

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
          const patch: Record<string, string> = {};
          if (result.logradouro) patch['logradouro'] = result.logradouro;
          if (result.bairro) patch['bairro'] = result.bairro;
          if (result.localidade) patch['cidade'] = result.localidade;
          if (result.uf) patch['estado'] = result.uf;
          if (result.complemento) patch['complemento'] = result.complemento;
          group.patchValue(patch);
        };

        this.sub
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
    if (control.errors['required']) return 'Campo obrigatório';
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
        // emitEvent: false evita loops infinitos de eventos
        control.get('principal')?.setValue(false, {emitEvent: false});
      }
    });
  }
}
