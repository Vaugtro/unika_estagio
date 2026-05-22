import { EnderecoResponse, EnderecoWithinClienteCreateRequest } from './endereco.model';

export interface FisicoListResponse {
  id: number;
  nome: string;
  cpf: string;
  email: string;
  estaAtivo: boolean;
}

export interface FisicoResponse {
  id: number;
  tipo: 'FISICA' | 'JURIDICA';
  cpf: string;
  nome: string;
  rg: string;
  email?: string;
  dataNascimento: string;
  estaAtivo: boolean;
  enderecos: EnderecoResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface FisicoCreateRequest {
  cpf: string;
  nome: string;
  rg: string;
  email?: string;
  dataNascimento: string;
  enderecos: EnderecoWithinClienteCreateRequest[];
}

export interface FisicoUpdateRequest {
  nome: string;
  email?: string;
  estaAtivo: boolean;
}
