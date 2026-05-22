export interface EnderecoResponse {
  id: number;
  logradouro: string;
  numero: number;
  cep: string;
  bairro: string;
  cidade: string;
  estado: string;
  telefone: string;
  complemento?: string;
  principal: boolean;
  clienteId: number;
  createdAt: string;
  updatedAt: string;
}

export interface EnderecoListResponse {
  id: number;
  logradouro: string;
  numero: number;
  cep: string;
  bairro: string;
  cidade: string;
  estado: string;
  clienteId: number;
  principal: boolean;
  complemento?: string;
}

export interface EnderecoCreateRequest {
  logradouro: string;
  numero: number;
  cep: string;
  bairro: string;
  telefone: string;
  estado: string;
  cidade: string;
  principal: boolean;
  complemento?: string;
  clienteId: number;
}

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
