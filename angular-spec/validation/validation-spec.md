# Validation Spec — Mirrors `ValidationConstants.java`

## Constants Module

```typescript
// shared/validators/validation-constants.ts
export const VALIDATION = {
  // Cliente Fisico
  NOME_MIN: 3,
  NOME_MAX: 150,
  RG_LENGTH_MIN: 7,
  RG_LENGTH_MAX: 9,
  CPF_LENGTH_FORMATTED_MIN: 11,   // 000.000.000-00
  CPF_LENGTH_FORMATTED_MAX: 14,

  // Cliente Juridico
  RAZAO_SOCIAL_MIN: 3,
  RAZAO_SOCIAL_MAX: 150,
  CNPJ_LENGTH_FORMATTED_MIN: 14,  // 00.000.000/0000-00
  CNPJ_LENGTH_FORMATTED_MAX: 18,
  INSCRICAO_ESTADUAL_MAX: 20,

  // Endereco
  LOGRADOURO_MIN: 3,
  LOGRADOURO_MAX: 150,
  BAIRRO_MIN: 3,
  BAIRRO_MAX: 100,
  CIDADE_MIN: 3,
  CIDADE_MAX: 100,
  CEP_MAX: 9,                     // 00000-000
  TELEFONE_MAX: 16,               // (00) 00000-0000
  COMPLEMENTO_MAX: 150,
  ESTADO_LENGTH: 2,               // UF

  // Email
  EMAIL_MAX: 150,
} as const;
```

## Custom Validators

### CPF Validator

```typescript
// shared/validators/cpf.validator.ts
export function cpfValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value?.replace(/\D/g, '');
    if (!value || value.length !== 11) return { cpf: 'CPF deve ter 11 dígitos' };
    // CPF check-digit algorithm
    let sum = 0;
    for (let i = 0; i < 9; i++) sum += parseInt(value[i]) * (10 - i);
    let rem = (sum * 10) % 11;
    if (rem === 10) rem = 0;
    if (rem !== parseInt(value[9])) return { cpf: 'CPF inválido' };
    sum = 0;
    for (let i = 0; i < 10; i++) sum += parseInt(value[i]) * (11 - i);
    rem = (sum * 10) % 11;
    if (rem === 10) rem = 0;
    if (rem !== parseInt(value[10])) return { cpf: 'CPF inválido' };
    return null;
  };
}
```

### CNPJ Validator

```typescript
// shared/validators/cnpj.validator.ts
export function cnpjValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value?.replace(/\D/g, '');
    if (!value || value.length !== 14) return { cnpj: 'CNPJ deve ter 14 dígitos' };
    // CNPJ check-digit algorithm
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
```

### CEP Validator

```typescript
// shared/validators/cep.validator.ts
export function cepValidator(): ValidatorFn {
  return (control): ValidationErrors | null => {
    const clean = control.value?.replace(/\D/g, '');
    if (clean && clean.length !== 8) return { cep: 'CEP deve ter 8 dígitos' };
    return null;
  };
}
```

### Telefone Validator

```typescript
// shared/validators/telefone.validator.ts
export function telefoneValidator(): ValidatorFn {
  return (control): ValidationErrors | null => {
    const clean = control.value?.replace(/\D/g, '');
    if (clean && clean.length < 10 || clean.length > 11)
      return { telefone: 'Telefone deve ter 10 ou 11 dígitos' };
    return null;
  };
}
```

## Reactive Form Integration (Pattern)

```typescript
// Inside a component:
this.fisicoForm = fb.group({
  cpf: ['', [Validators.required, cpfValidator()]],
  nome: ['', [
    Validators.required,
    Validators.minLength(VALIDATION.NOME_MIN),
    Validators.maxLength(VALIDATION.NOME_MAX)
  ]],
  rg: ['', [
    Validators.required,
    Validators.minLength(VALIDATION.RG_LENGTH_MIN),
    Validators.maxLength(VALIDATION.RG_LENGTH_MAX)
  ]],
  email: ['', [Validators.email, Validators.maxLength(VALIDATION.EMAIL_MAX)]],
  dataNascimento: ['', Validators.required],
  enderecos: fb.array([])
});
```

## Wicket → Angular Validation Cross-Reference

| Wicket Validator | Angular Equivalent |
|---|---|
| `StringValidator.lengthBetween(3, 150)` | `Validators.minLength(3)`, `Validators.maxLength(150)` |
| `StringValidator.maximumLength(150)` | `Validators.maxLength(150)` |
| `StringValidator.exactLength(2)` | `Validators.minLength(2)`, `Validators.maxLength(2)` |
| `PatternValidator("^\\d{5}-?\\d{3}$")` | `cepValidator()` custom |
| `EmailAddressValidator.getInstance()` | `Validators.email` |
| `@CPF` / `@CNPJ` (Bean Validation) | `cpfValidator()` / `cnpjValidator()` custom |
| `setRequired(true)` | `Validators.required` |
| `AttributeModifier("data-mask", "...")` | `ngx-mask` directive: `mask="000.000.000-00"` |
