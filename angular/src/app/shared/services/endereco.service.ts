import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from '../models/page.model';
import { EnderecoListResponse, EnderecoResponse, EnderecoCreateRequest, EnderecoUpdateRequest, EnderecoWithinClienteCreateRequest } from '../models/endereco.model';

@Injectable({ providedIn: 'root' })
export class EnderecoService {
  private readonly base = '/v1/enderecos';

  constructor(private http: HttpClient) {}

  findAllByClienteId(clienteId: number, page: number = 0, size: number = 50): Observable<Page<EnderecoListResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<EnderecoListResponse>>(`${this.base}/clientes/${clienteId}`, { params });
  }

  search(q: string, page: number = 0, size: number = 10): Observable<Page<EnderecoListResponse>> {
    let params = new HttpParams().set('q', q || '').set('page', page).set('size', size);
    return this.http.get<Page<EnderecoListResponse>>(`${this.base}/search`, { params });
  }

  findById(id: number): Observable<EnderecoResponse> {
    return this.http.get<EnderecoResponse>(`${this.base}/${id}`);
  }

  findPrincipalByClienteId(clienteId: number): Observable<EnderecoResponse> {
    return this.http.get<EnderecoResponse>(`${this.base}/clientes/${clienteId}/principal`);
  }

  hasPrincipal(clienteId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.base}/clientes/${clienteId}/has-principal`);
  }

  create(dto: EnderecoCreateRequest): Observable<EnderecoResponse> {
    return this.http.post<EnderecoResponse>(this.base, dto);
  }

  createForCliente(clienteId: number, dto: EnderecoWithinClienteCreateRequest): Observable<EnderecoResponse> {
    return this.http.post<EnderecoResponse>(`${this.base}/clientes/${clienteId}`, dto);
  }

  update(id: number, dto: EnderecoUpdateRequest): Observable<EnderecoResponse> {
    return this.http.put<EnderecoResponse>(`${this.base}/${id}`, dto);
  }

  setAsPrincipal(id: number): Observable<EnderecoResponse> {
    return this.http.patch<EnderecoResponse>(`${this.base}/${id}/principal`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  deleteAllByClienteId(clienteId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/clientes/${clienteId}`);
  }
}
