import { EnderecoResponse, EnderecoWithinClienteCreateRequest } from './endereco.model';

export interface JuridicoListResponse {
  id: number;
  razaoSocial: string;
  cnpj: string;
  email: string;
  estaAtivo: boolean;
}

export interface JuridicoResponse {
  id: number;
  tipo: 'FISICA' | 'JURIDICA';
  cnpj: string;
  razaoSocial: string;
  inscricaoEstadual: string;
  email?: string;
  dataCriacaoEmpresa: string;
  estaAtivo: boolean;
  enderecos: EnderecoResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface JuridicoCreateRequest {
  cnpj: string;
  razaoSocial: string;
  inscricaoEstadual: string;
  email?: string;
  dataCriacaoEmpresa: string;
  enderecos: EnderecoWithinClienteCreateRequest[];
}

export interface JuridicoUpdateRequest {
  razaoSocial: string;
  inscricaoEstadual: string;
  email?: string;
  dataCriacaoEmpresa: string;
  estaAtivo: boolean;
}
