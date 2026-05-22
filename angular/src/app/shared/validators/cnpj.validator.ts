import { AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';

export function cnpjValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value?.replace(/\D/g, '');
    if (!value) return null;
    if (value.length !== 14) return { cnpj: 'CNPJ deve ter 14 dígitos' };

    if (/^(\d)\1+$/.test(value)) return { cnpj: 'CNPJ inválido' };

    let size = value.length - 2;
    let numbers = value.substring(0, size);
    const digits = value.substring(size);
    let sum = 0;
    let pos = size - 7;

    for (let i = size; i >= 1; i--) {
      sum += parseInt(numbers.charAt(size - i)) * pos--;
      if (pos < 2) pos = 9;
    }

    let result = sum % 11 < 2 ? 0 : 11 - (sum % 11);
    if (result !== parseInt(digits.charAt(0))) return { cnpj: 'CNPJ inválido' };

    size += 1;
    numbers = value.substring(0, size);
    sum = 0;
    pos = size - 7;

    for (let i = size; i >= 1; i--) {
      sum += parseInt(numbers.charAt(size - i)) * pos--;
      if (pos < 2) pos = 9;
    }

    result = sum % 11 < 2 ? 0 : 11 - (sum % 11);
    if (result !== parseInt(digits.charAt(1))) return { cnpj: 'CNPJ inválido' };

    return null;
  };
}
