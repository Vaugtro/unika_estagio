import { AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';

export function cpfValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value?.replace(/\D/g, '');
    if (!value) return null;
    if (value.length !== 11) return { cpf: 'CPF deve ter 11 dígitos' };

    if (/^(\d)\1+$/.test(value)) return { cpf: 'CPF inválido' };

    let sum = 0;
    for (let i = 0; i < 9; i++) sum += parseInt(value[i]) * (10 - i);
    let rem = (sum * 10) % 11;
    if (rem === 10 || rem === 11) rem = 0;
    if (rem !== parseInt(value[9])) return { cpf: 'CPF inválido' };

    sum = 0;
    for (let i = 0; i < 10; i++) sum += parseInt(value[i]) * (11 - i);
    rem = (sum * 10) % 11;
    if (rem === 10 || rem === 11) rem = 0;
    if (rem !== parseInt(value[10])) return { cpf: 'CPF inválido' };

    return null;
  };
}
