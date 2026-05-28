import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {EMPTY, of, Subscription} from 'rxjs';
import {catchError, debounceTime, distinctUntilChanged, filter, switchMap, tap} from 'rxjs/operators';
import {ToastService} from '../../../shared/services/toast.service';
import {cepValidator} from '../../../shared/validators/cep.validator';
import {telefoneValidator} from '../../../shared/validators/telefone.validator';
import {VALIDATION} from '../../../shared/validators/validation-constants';
import {EnderecoResponse} from '../../../api/model/enderecoResponse';
import {EnderecosService} from '../../../api';
import {ViaCepService} from '../../../shared/services/via-cep.service';
import {UnidadeFederativaDTO} from '../../../api/model/unidadeFederativaDTO';
import {MunicipioDTO} from '../../../api/model/municipioDTO';
import {UnidadesFederativasService} from '../../../api/api/unidadesFederativas.service';
import {MunicipiosService} from '../../../api/api/municipios.service';

export interface EnderecoCreateDialogData {
  clienteId?: number;
  clienteType?: 'fisico' | 'juridico';
  endereco?: EnderecoResponse;
}

@Component({
  selector: 'app-endereco-create-dialog',
  templateUrl: './endereco-create-dialog.component.html',
  styleUrls: ['./endereco-create-dialog.component.scss'],
})
export class EnderecoCreateDialogComponent implements OnInit, OnDestroy {
  form!: FormGroup;
  submitting = false;
  hasClienteContext = false;
  isEditing = false;
  ufs: UnidadeFederativaDTO[] = [];
  municipiosCache = new Map<string, MunicipioDTO[]>();
  private subscriptions: Subscription[] = [];

  constructor(
    private fb: FormBuilder,
    private enderecosService: EnderecosService,
    private toastService: ToastService,
    private viaCepService: ViaCepService,
    private ufService: UnidadesFederativasService,
    private municipioService: MunicipiosService,
    public dialogRef: MatDialogRef<EnderecoCreateDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EnderecoCreateDialogData,
  ) {
    this.hasClienteContext = !!data?.clienteId && !data?.endereco;
    this.isEditing = !!data?.endereco;
  }

  getMunicipios(ufSigla: string): MunicipioDTO[] {
    return this.municipiosCache.get(ufSigla) || [];
  }

  private loadMunicipios(ufSigla: string): void {
    if (!ufSigla || this.municipiosCache.has(ufSigla)) return;
    this.subscriptions.push(
      this.municipioService.municipiosFindByUf(ufSigla).pipe(
        catchError(() => of([]))
      ).subscribe((municipios) => {
        this.municipiosCache.set(ufSigla, municipios);
      })
    );
  }

  ngOnInit(): void {
    const e = this.data.endereco;
    const group: Record<string, unknown> = {
      logradouro: [e?.logradouro || '', [Validators.required, Validators.minLength(VALIDATION.LOGRADOURO_MIN), Validators.maxLength(VALIDATION.LOGRADOURO_MAX)]],
      numero: [e?.numero || null, [Validators.required, Validators.min(1)]],
      bairro: [e?.bairro || '', [Validators.required, Validators.minLength(VALIDATION.BAIRRO_MIN), Validators.maxLength(VALIDATION.BAIRRO_MAX)]],
      cep: [e?.cep || '', [Validators.required, cepValidator()]],
      ufSigla: ['', Validators.required],
      municipioId: [null, Validators.required],
      telefone: ['', telefoneValidator()],
      complemento: ['', Validators.maxLength(VALIDATION.COMPLEMENTO_MAX)],
      principal: [e?.principal || false],
    };

    if (!this.hasClienteContext) {
      group['clienteTipo'] = ['FISICA', Validators.required];
      group['clienteId'] = [null, [Validators.required, Validators.min(1)]];
    }

    this.form = this.fb.group(group);

    if (this.isEditing && e) {
      this.loadMunicipios(e.estado || '');
      setTimeout(() => {
        this.form.patchValue({
          ufSigla: e.estado || '',
          municipioId: e.municipioId,
        });
      });
    }

    this.subscriptions.push(
      this.ufService.unidadesFederativasFindAll().pipe(
        catchError(() => of([]))
      ).subscribe((ufs) => {
        this.ufs = ufs;
      })
    );

    this.subscriptions.push(
      this.form.get('ufSigla')!.valueChanges.pipe(
        distinctUntilChanged(),
        tap((sigla: string) => this.loadMunicipios(sigla))
      ).subscribe()
    );

    this.subscriptions.push(
      this.form.get('cep')!.valueChanges.pipe(
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
          this.form.patchValue(patch);

          if (result.uf && result.ibge) {
            this.loadMunicipios(result.uf);
            const ibge = Number(result.ibge);
            setTimeout(() => this.form.patchValue({municipioId: ibge}));
          }
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const v = this.form.value;

    const addressDto = {
      logradouro: v.logradouro,
      numero: v.numero,
      cep: (v.cep || '').replace(/\D/g, ''),
      bairro: v.bairro,
      telefone: (v.telefone || '').replace(/\D/g, ''),
      municipioId: v.municipioId,
      principal: v.principal,
      complemento: v.complemento || undefined,
    };

    let obs$;
    if (this.isEditing) {
      obs$ = this.enderecosService.enderecosUpdate(this.data.endereco!.id!, addressDto);
    } else if (this.hasClienteContext) {
      obs$ = this.enderecosService.enderecosCreateForCliente(this.data.clienteId!, addressDto);
    } else {
      obs$ = this.enderecosService.enderecosCreate({...addressDto, clienteId: v.clienteId});
    }

    const successMsg = this.isEditing ? 'Endereço atualizado com sucesso' : 'Endereço criado com sucesso';

    this.subscriptions.push(
      obs$.pipe(
        catchError((err) => {
          const msg = err.error?.message || err.statusText || 'Erro ao salvar endereço';
          this.toastService.show('error', msg);
          this.submitting = false;
          return EMPTY;
        })
      ).subscribe(() => {
        this.toastService.show('success', successMsg);
        this.dialogRef.close(true);
      })
    );
  }

  getFieldError(field: string): string {
    const control = this.form.get(field);
    if (!control?.errors) return '';
    if (control.errors['required']) return 'Campo é necessário.';
    if (control.errors['minlength']) return `Mín. ${control.errors['minlength'].requiredLength} caracteres`;
    if (control.errors['maxlength']) return `Máx. ${control.errors['maxlength'].requiredLength} caracteres`;
    if (control.errors['min']) return 'Valor inválido';
    if (control.errors['cep']) return control.errors['cep'];
    if (control.errors['telefone']) return control.errors['telefone'];
    return 'Inválido';
  }
}
