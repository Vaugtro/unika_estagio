# TypeScript Interfaces — Mapped from Java DTOs

## Common

```typescript
// Page<T> — Spring Page wrapper
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;       // current page (0-indexed)
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface Cliente {
  id?: number;
  tipo?: 'FISICA' | 'JURIDICA';
  email?: string;
  estaAtivo?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
```

---

## Cliente Fisico

```typescript
// Wicket: ClienteFisicoListResponse
export interface FisicoListResponse {
  id: number;
  nome: string;
  cpf: string;          // formatted: 000.000.000-00
  email: string;
  estaAtivo: boolean;
}

// Wicket: ClienteFisicoResponse
export interface FisicoResponse extends Cliente {
  cpf: string;
  nome: string;
  rg: string;
  dataNascimento: string;  // yyyy-MM-dd
  enderecos: EnderecoResponse[];
}

// Wicket: ClienteFisicoCreateRequest
export interface FisicoCreateRequest {
  cpf: string;           // cleaned: 11 digits
  nome: string;          // 3-150 chars
  rg: string;            // cleaned: 7-9 digits
  email?: string;        // max 150 chars
  dataNascimento: string; // yyyy-MM-dd
  enderecos: EnderecoWithinClienteCreateRequest[];
}

// Wicket: ClienteFisicoUpdateRequest
export interface FisicoUpdateRequest {
  nome: string;
  email: string;
  estaAtivo: boolean;
}
```

---

## Cliente Juridico

```typescript
// Wicket: ClienteJuridicoListResponse
export interface JuridicoListResponse {
  id: number;
  razaoSocial: string;
  cnpj: string;           // formatted: 00.000.000/0000-00
  email: string;
  estaAtivo: boolean;
}

// Wicket: ClienteJuridicoResponse
export interface JuridicoResponse extends Cliente {
  cnpj: string;
  razaoSocial: string;
  inscricaoEstadual: string;
  dataCriacaoEmpresa: string;  // yyyy-MM-dd
  enderecos: EnderecoResponse[];
}

// Wicket: ClienteJuridicoCreateRequest
export interface JuridicoCreateRequest {
  cnpj: string;                  // cleaned: 14 digits
  razaoSocial: string;           // 3-150 chars
  inscricaoEstadual: string;     // cleaned: 8-14 alphanum
  email?: string;                // max 150 chars
  dataCriacaoEmpresa: string;    // yyyy-MM-dd
  enderecos: EnderecoWithinClienteCreateRequest[];
}

// Wicket: ClienteJuridicoUpdateRequest
export interface JuridicoUpdateRequest {
  razaoSocial: string;
  inscricaoEstadual: string;
  email: string;
  dataCriacaoEmpresa: string;
  estaAtivo: boolean;
}
```

---

## Endereco

```typescript
// Wicket: EnderecoResponse
export interface EnderecoResponse {
  id: number;
  logradouro: string;
  numero: number;
  bairro: string;
  cep: string;              // formatted: 00000-000
  cidade: string;
  estado: string;            // UF: 2 chars
  telefone: string;          // formatted: (00) 00000-0000
  complemento?: string;
  principal: boolean;
  clienteId: number;
  createdAt: string;
  updatedAt: string;
}

// Wicket: EnderecoListResponse (if different from Response)
export interface EnderecoListResponse {
  id: number;
  logradouro: string;
  numero: number;
  bairro: string;
  cep: string;
  cidade: string;
  estado: string;
  telefone: string;
  principal: boolean;
}

// Wicket: EnderecoCreateRequest
export interface EnderecoCreateRequest {
  logradouro: string;
  numero: number;
  cep: string;               // cleaned: 8 digits
  bairro: string;
  telefone: string;          // cleaned: 10-11 digits
  estado: string;            // 2 chars
  cidade: string;
  principal: boolean;
  complemento?: string;
  clienteId?: number;        // nullable for EnderecoWithinClienteCreateRequest
}

// Wicket: EnderecoWithinClienteCreateRequest (used inside create cliente request)
export interface EnderecoWithinClienteCreateRequest {
  logradouro: string;
  numero: number;
  cep: string;
  bairro: string;
  telefone: string;
  estado: string;
  cidade: string;
  principal: boolean;
  complemento?: string;
}

// Wicket: EnderecoUpdateRequest
export interface EnderecoUpdateRequest {
  logradouro: string;
  numero: number;
  cep: string;
  bairro: string;
  telefone: string;
  estado: string;
  cidade: string;
  principal: boolean;
  complemento?: string;
}
```

---

## Form Models (Wicket `*FormModel` → Angular Reactive Form shape)

```typescript
// Wicket: EnderecoCreateFormModel → Angular FormGroup shape
export interface EnderecoFormModel {
  id?: number;
  logradouro: string;
  numero: number | null;
  bairro: string;
  cep: string;
  cidade: string;
  estado: string;
  telefone: string;
  principal: boolean;
  complemento?: string;
}

// Angular creates FormGroups matching these shapes:
// fisicoCreateForm = fb.group({
//   cpf: ['', [Validators.required, CpfValidator()]],
//   nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(150)]],
//   rg: ['', [Validators.required, Validators.minLength(7), Validators.maxLength(9)]],
//   email: ['', [Validators.email, Validators.maxLength(150)]],
//   dataNascimento: ['', Validators.required],
//   enderecos: fb.array([...])
// });
```
