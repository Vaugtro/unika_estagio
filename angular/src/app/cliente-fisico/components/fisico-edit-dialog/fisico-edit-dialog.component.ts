import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {catchError, EMPTY, Subscription} from 'rxjs';
import {ToastService} from '../../../shared/services/toast.service';
import {VALIDATION} from '../../../shared/validators/validation-constants';
import {ClienteFisicoResponse} from '../../../api/model/clienteFisicoResponse';
import {ClientesFisicosService} from "../../../api";

export interface FisicoEditDialogData {
  cliente: ClienteFisicoResponse;
}

@Component({
  selector: 'app-fisico-edit-dialog',
  templateUrl: './fisico-edit-dialog.component.html',
  styleUrls: ['./fisico-edit-dialog.component.scss'],
})
export class FisicoEditDialogComponent implements OnInit {
  form!: FormGroup;
  submitting = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private fb: FormBuilder,
    private clientesFisicosService: ClientesFisicosService,
    private toastService: ToastService,
    private dialogRef: MatDialogRef<FisicoEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: FisicoEditDialogData,
  ) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      nome: [this.data.cliente.nome, [Validators.required, Validators.minLength(VALIDATION.NOME_MIN), Validators.maxLength(VALIDATION.NOME_MAX)]],
      email: [this.data.cliente.email, [Validators.email, Validators.maxLength(VALIDATION.EMAIL_MAX)]],
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const dto = {
      nome: this.form.value.nome,
      email: this.form.value.email || undefined,
    };

    this.subscriptions.push(
      this.clientesFisicosService.clientesFisicosUpdate(this.data.cliente.id!, dto).pipe(
        catchError((err) => {
          const msg = err.error?.message || err.statusText || 'Erro ao atualizar cliente';
          this.toastService.show('error', msg);
          this.submitting = false;
          return EMPTY;
        })
      ).subscribe(() => {
        this.toastService.show('success', 'Cliente físico atualizado com sucesso');
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
