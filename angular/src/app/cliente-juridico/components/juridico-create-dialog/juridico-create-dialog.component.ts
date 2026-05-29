import {Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {catchError, EMPTY, Subscription} from 'rxjs';
import {EnderecoFormComponent} from '../../../shared/components/endereco-form/endereco-form.component';
import {ToastService} from '../../../shared/services/toast.service';
import {cnpjValidator} from '../../../shared/validators/cnpj.validator';
import {VALIDATION} from '../../../shared/validators/validation-constants';
import {ClientesJuridicosService} from "../../../api";

@Component({
  selector: 'app-juridico-create-dialog',
  templateUrl: './juridico-create-dialog.component.html',
  styleUrls: ['./juridico-create-dialog.component.scss'],
})
export class JuridicoCreateDialogComponent implements OnInit {
  @ViewChild('enderecoForm') enderecoForm!: EnderecoFormComponent;

  form!: FormGroup;
  submitting = false;
  private subscriptions: Subscription[] = [];
  public mask = {
    guide: true,
    showMask : true,
    mask: [/\d/, /\d/, '/', /\d/, /\d/, '/',/\d/, /\d/,/\d/, /\d/]
  };

  constructor(
    private fb: FormBuilder,
    private clientesJuridicosService: ClientesJuridicosService,
    private toastService: ToastService,
    private dialogRef: MatDialogRef<JuridicoCreateDialogComponent>,
  ) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      cnpj: ['', [Validators.required, cnpjValidator()]],
      razaoSocial: ['', [Validators.required, Validators.minLength(VALIDATION.RAZAO_SOCIAL_MIN), Validators.maxLength(VALIDATION.RAZAO_SOCIAL_MAX)]],
      inscricaoEstadual: ['', [Validators.maxLength(VALIDATION.INSCRICAO_ESTADUAL_MAX)]],
      email: ['', [Validators.email, Validators.maxLength(VALIDATION.EMAIL_MAX)]],
      dataCriacaoEmpresa: ['', Validators.required],
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
    const rawCnpj = this.form.value.cnpj.replace(/\D/g, '');

    const dto = {
      cnpj: rawCnpj,
      razaoSocial: this.form.value.razaoSocial,
      inscricaoEstadual: this.form.value.inscricaoEstadual,
      email: this.form.value.email || undefined,
      dataCriacaoEmpresa: this.form.value.dataCriacaoEmpresa || undefined,
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
      this.clientesJuridicosService.clientesJuridicosCreate(dto).pipe(
        catchError((err) => {
          const msg = err.error?.message || err.statusText || 'Erro ao criar cliente';
          this.toastService.show('error', msg);
          this.submitting = false;
          return EMPTY;
        })
      ).subscribe(() => {
        this.toastService.show('success', 'Cliente jurídico criado com sucesso');
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
    if (control.errors['cnpj']) return control.errors['cnpj'];
    return 'Inválido';
  }
}
