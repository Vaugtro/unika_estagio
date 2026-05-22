import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { cepValidator } from '../../validators/cep.validator';
import { telefoneValidator } from '../../validators/telefone.validator';
import { VALIDATION } from '../../validators/validation-constants';

@Component({
  selector: 'app-endereco-form',
  templateUrl: './endereco-form.component.html',
  styleUrls: ['./endereco-form.component.scss'],
})
export class EnderecoFormComponent implements OnInit, OnDestroy {
  form!: FormGroup;
  private sub = new Subscription();

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      enderecos: this.fb.array([]),
    });
    this.addEndereco();
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  get enderecos(): FormArray {
    return this.form.get('enderecos') as FormArray;
  }

  getFormArray(): FormArray {
    return this.enderecos;
  }

  createEnderecoGroup(): FormGroup {
    return this.fb.group({
      logradouro: ['', [Validators.required, Validators.minLength(VALIDATION.LOGRADOURO_MIN), Validators.maxLength(VALIDATION.LOGRADOURO_MAX)]],
      numero: [null, [Validators.required, Validators.min(1)]],
      bairro: ['', [Validators.required, Validators.minLength(VALIDATION.BAIRRO_MIN), Validators.maxLength(VALIDATION.BAIRRO_MAX)]],
      cep: ['', [Validators.required, cepValidator()]],
      cidade: ['', [Validators.required, Validators.minLength(VALIDATION.CIDADE_MIN), Validators.maxLength(VALIDATION.CIDADE_MAX)]],
      estado: ['', [Validators.required, Validators.minLength(VALIDATION.ESTADO_LENGTH), Validators.maxLength(VALIDATION.ESTADO_LENGTH)]],
      telefone: ['', telefoneValidator()],
      principal: [false],
      complemento: ['', [Validators.maxLength(VALIDATION.COMPLEMENTO_MAX)]],
    });
  }

  addEndereco(): void {
    this.enderecos.push(this.createEnderecoGroup());
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
}
