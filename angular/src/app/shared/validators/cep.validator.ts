import { AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';

export function cepValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const clean = control.value?.replace(/\D/g, '');
    if (clean && clean.length !== 8) return { cep: 'CEP deve ter 8 dígitos' };
    return null;
  };
}
