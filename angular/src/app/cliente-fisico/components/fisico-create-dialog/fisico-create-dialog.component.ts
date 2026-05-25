import { Component, ViewChild, OnInit, OnDestroy } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Subscription, catchError, EMPTY } from 'rxjs';
import { EnderecoFormComponent } from '../../../shared/components/endereco-form/endereco-form.component';
import { ToastService } from '../../../shared/services/toast.service';
import { cpfValidator } from '../../../shared/validators/cpf.validator';
import { VALIDATION } from '../../../shared/validators/validation-constants';
import {ClientesFisicosService} from "../../../api";

@Component({
  selector: 'app-fisico-create-dialog',
  templateUrl: './fisico-create-dialog.component.html',
  styleUrls: ['./fisico-create-dialog.component.scss'],
})
export class FisicoCreateDialogComponent implements OnInit {
  @ViewChild('enderecoForm') enderecoForm!: EnderecoFormComponent;

  form!: FormGroup;
  submitting = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private fb: FormBuilder,
    private clientesFisicosService: ClientesFisicosService,
    private toastService: ToastService,
    private dialogRef: MatDialogRef<FisicoCreateDialogComponent>,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      cpf: ['', [Validators.required, cpfValidator()]],
      nome: ['', [Validators.required, Validators.minLength(VALIDATION.NOME_MIN), Validators.maxLength(VALIDATION.NOME_MAX)]],
      rg: ['', [Validators.required, Validators.minLength(VALIDATION.RG_LENGTH_MIN), Validators.maxLength(VALIDATION.RG_LENGTH_MAX)]],
      email: ['', [Validators.email, Validators.maxLength(VALIDATION.EMAIL_MAX)]],
      dataNascimento: ['', Validators.required],
    });
  }

  submit(): void {
    const mainInvalid = this.form.invalid;

    const enderecoArray = this.enderecoForm.getFormArray();
    const enderecoInvalid = enderecoArray.invalid || enderecoArray.length === 0;

    if (mainInvalid) {
      this.form.markAllAsTouched();
    }

    if (enderecoInvalid) {
      for (const control of enderecoArray.controls) {
        (control as FormGroup).markAllAsTouched();
      }
      this.toastService.show(
        'error',
        enderecoArray.length === 0
          ? 'Adicione pelo menos um endereço'
          : 'Verifique os campos obrigatórios de endereço',
      );
    }

    if (mainInvalid || enderecoInvalid) {
      return;
    }

    this.submitting = true;
    const rawCpf = this.form.value.cpf.replace(/\D/g, '');

    const dto = {
      cpf: rawCpf,
      nome: this.form.value.nome,
      rg: this.form.value.rg.replace(/\D/g, ''),
      email: this.form.value.email || undefined,
      dataNascimento: this.form.value.dataNascimento,
      enderecos: enderecoArray.value.map((e: any) => ({
        logradouro: e.logradouro,
        numero: e.numero,
        cep: e.cep.replace(/\D/g, ''),
        bairro: e.bairro,
        telefone: e.telefone.replace(/\D/g, ''),
        estado: e.estado,
        cidade: e.cidade,
        principal: e.principal,
        complemento: e.complemento || undefined,
      })),
    };

    this.subscriptions.push(
      this.clientesFisicosService.clientesFisicosCreate(dto).pipe(
        catchError((err) => {
          const msg = err.error?.message || err.statusText || 'Erro ao criar cliente';
          this.toastService.show('error', msg);
          this.submitting = false;
          return EMPTY;
        })
      ).subscribe(() => {
        this.toastService.show('success', 'Cliente físico criado com sucesso');
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
    if (control.errors['cpf']) return control.errors['cpf'];
    return 'Inválido';
  }
}
