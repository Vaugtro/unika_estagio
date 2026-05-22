import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from '../models/page.model';
import { FisicoListResponse, FisicoResponse, FisicoCreateRequest, FisicoUpdateRequest } from '../models/fisico.model';

@Injectable({ providedIn: 'root' })
export class FisicoService {
  private readonly base = '/v1/clientes/fisicos';

  constructor(private http: HttpClient) {}

  findAll(page: number = 0, size: number = 10): Observable<Page<FisicoListResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<FisicoListResponse>>(this.base, { params });
  }

  findAllActive(page: number = 0, size: number = 10): Observable<Page<FisicoListResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<FisicoListResponse>>(`${this.base}/ativos`, { params });
  }

  search(q: string, page: number = 0, size: number = 10): Observable<Page<FisicoListResponse>> {
    const params = new HttpParams().set('q', q).set('page', page).set('size', size);
    return this.http.get<Page<FisicoListResponse>>(`${this.base}/search`, { params });
  }

  findById(id: number): Observable<FisicoResponse> {
    return this.http.get<FisicoResponse>(`${this.base}/${id}`);
  }

  existsByCpf(cpf: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.base}/cpf/${cpf}/exists`);
  }

  create(dto: FisicoCreateRequest): Observable<FisicoResponse> {
    return this.http.post<FisicoResponse>(this.base, dto);
  }

  update(id: number, dto: FisicoUpdateRequest): Observable<FisicoResponse> {
    return this.http.put<FisicoResponse>(`${this.base}/${id}`, dto);
  }

  activate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.base}/${id}/ativar`, {});
  }

  inactivate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.base}/${id}/inativar`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  hardDelete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}/permanent`);
  }
}
