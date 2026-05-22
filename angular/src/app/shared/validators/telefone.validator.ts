import { AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';

export function telefoneValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const clean = control.value?.replace(/\D/g, '');
    if (clean && (clean.length < 10 || clean.length > 11)) {
      return { telefone: 'Telefone deve ter 10 ou 11 dígitos' };
    }
    return null;
  };
}
