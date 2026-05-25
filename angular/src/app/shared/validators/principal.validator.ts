import {AbstractControl, ValidatorFn, ValidationErrors, FormArray} from '@angular/forms';

export function principalArrayValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const formArray = control as FormArray;
    const principais = formArray.controls.filter(
      group => group.get('principal')?.value === true
    );

    // Erro se houver mais de um ou nenhum (ajuste conforme sua regra)
    if (principais.length > 1) {
      return { multiplePrincipals: true };
    }
    if (principais.length === 0) {
      return { noPrincipal: true };
    }

    return null;
  };
}
