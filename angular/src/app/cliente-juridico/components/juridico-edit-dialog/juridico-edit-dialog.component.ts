import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {catchError, EMPTY, Subscription} from 'rxjs';
import {ToastService} from '../../../shared/services/toast.service';
import {VALIDATION} from '../../../shared/validators/validation-constants';
import {ClienteJuridicoResponse} from '../../../api/model/clienteJuridicoResponse';
import {ClientesJuridicosService} from "../../../api";

export interface JuridicoEditDialogData {
  cliente: ClienteJuridicoResponse;
}

@Component({
  selector: 'app-juridico-edit-dialog',
  templateUrl: './juridico-edit-dialog.component.html',
  styleUrls: ['./juridico-edit-dialog.component.scss'],
})
export class JuridicoEditDialogComponent implements OnInit {
  form!: FormGroup;
  submitting = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private fb: FormBuilder,
    private clientesJuridicosService: ClientesJuridicosService,
    private toastService: ToastService,
    private dialogRef: MatDialogRef<JuridicoEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: JuridicoEditDialogData,
  ) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      razaoSocial: [this.data.cliente.razaoSocial, [Validators.required, Validators.minLength(VALIDATION.RAZAO_SOCIAL_MIN), Validators.maxLength(VALIDATION.RAZAO_SOCIAL_MAX)]],
      inscricaoEstadual: [this.data.cliente.inscricaoEstadual, [Validators.maxLength(VALIDATION.INSCRICAO_ESTADUAL_MAX)]],
      email: [this.data.cliente.email, [Validators.email, Validators.maxLength(VALIDATION.EMAIL_MAX)]],
      dataCriacaoEmpresa: [this.data.cliente.dataCriacaoEmpresa || null],
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const dto = {
      razaoSocial: this.form.value.razaoSocial,
      inscricaoEstadual: this.form.value.inscricaoEstadual || undefined,
      email: this.form.value.email || undefined,
      dataCriacaoEmpresa: this.form.value.dataCriacaoEmpresa || undefined,
      enderecos: [],
    };

    this.subscriptions.push(
      this.clientesJuridicosService.clientesJuridicosUpdate(this.data.cliente.id!, dto).pipe(
        catchError((err) => {
          const ve = err.error?.validationErrors;
          const msg = ve ? Object.entries(ve).map(([k, v]) => `${k}: ${v}`).join('; ') : (err.error?.message || err.statusText || 'Erro ao atualizar cliente');
          this.toastService.show('error', msg);
          this.submitting = false;
          return EMPTY;
        })
      ).subscribe(() => {
        this.toastService.show('success', 'Cliente jurídico atualizado com sucesso');
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
    if (control.errors['email']) return 'E-mail inválido';
    return 'Inválido';
  }
}
