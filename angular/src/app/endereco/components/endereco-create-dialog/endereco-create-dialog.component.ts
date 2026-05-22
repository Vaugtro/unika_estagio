import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Subscription, catchError, EMPTY } from 'rxjs';
import { EnderecoService } from '../../../shared/services/endereco.service';
import { ToastService } from '../../../shared/services/toast.service';
import { cepValidator } from '../../../shared/validators/cep.validator';
import { telefoneValidator } from '../../../shared/validators/telefone.validator';
import { VALIDATION } from '../../../shared/validators/validation-constants';

@Component({
  selector: 'app-endereco-create-dialog',
  templateUrl: './endereco-create-dialog.component.html',
  styleUrls: ['./endereco-create-dialog.component.scss'],
})
export class EnderecoCreateDialogComponent implements OnInit {
  form!: FormGroup;
  submitting = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private fb: FormBuilder,
    private enderecoService: EnderecoService,
    private toastService: ToastService,
    public dialogRef: MatDialogRef<EnderecoCreateDialogComponent>,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      clienteTipo: ['FISICA', Validators.required],
      clienteId: [null, [Validators.required, Validators.min(1)]],
      logradouro: ['', [Validators.required, Validators.minLength(VALIDATION.LOGRADOURO_MIN), Validators.maxLength(VALIDATION.LOGRADOURO_MAX)]],
      numero: [null, [Validators.required, Validators.min(1)]],
      bairro: ['', [Validators.required, Validators.minLength(VALIDATION.BAIRRO_MIN), Validators.maxLength(VALIDATION.BAIRRO_MAX)]],
      cep: ['', [Validators.required, cepValidator()]],
      cidade: ['', [Validators.required, Validators.minLength(VALIDATION.CIDADE_MIN), Validators.maxLength(VALIDATION.CIDADE_MAX)]],
      estado: ['', [Validators.required, Validators.minLength(VALIDATION.ESTADO_LENGTH), Validators.maxLength(VALIDATION.ESTADO_LENGTH)]],
      telefone: ['', telefoneValidator()],
      complemento: ['', Validators.maxLength(VALIDATION.COMPLEMENTO_MAX)],
      principal: [false],
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const v = this.form.value;

    const dto = {
      logradouro: v.logradouro,
      numero: v.numero,
      cep: v.cep.replace(/\D/g, ''),
      bairro: v.bairro,
      telefone: v.telefone.replace(/\D/g, ''),
      estado: v.estado,
      cidade: v.cidade,
      principal: v.principal,
      complemento: v.complemento || undefined,
      clienteId: v.clienteId,
    };

    this.subscriptions.push(
      this.enderecoService.create(dto).pipe(
        catchError((err) => {
          const msg = err.error?.message || err.statusText || 'Erro ao criar endereço';
          this.toastService.show('error', msg);
          this.submitting = false;
          return EMPTY;
        })
      ).subscribe(() => {
        this.toastService.show('success', 'Endereço criado com sucesso');
        this.dialogRef.close(true);
      })
    );
  }

  getFieldError(field: string): string {
    const control = this.form.get(field);
    if (!control?.errors) return '';
    if (control.errors['required']) return 'Campo obrigatório';
    if (control.errors['minlength']) return `Mín. ${control.errors['minlength'].requiredLength} caracteres`;
    if (control.errors['maxlength']) return `Máx. ${control.errors['maxlength'].requiredLength} caracteres`;
    if (control.errors['min']) return 'Valor inválido';
    if (control.errors['cep']) return control.errors['cep'];
    if (control.errors['telefone']) return control.errors['telefone'];
    return 'Inválido';
  }
}
