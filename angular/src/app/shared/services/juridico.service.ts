import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page } from '../models/page.model';
import { JuridicoListResponse, JuridicoResponse, JuridicoCreateRequest, JuridicoUpdateRequest } from '../models/juridico.model';

@Injectable({ providedIn: 'root' })
export class JuridicoService {
  private readonly base = '/v1/clientes/juridicos';

  constructor(private http: HttpClient) {}

  findAll(page: number = 0, size: number = 10): Observable<Page<JuridicoListResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<JuridicoListResponse>>(this.base, { params });
  }

  findAllActive(page: number = 0, size: number = 10): Observable<Page<JuridicoListResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<JuridicoListResponse>>(`${this.base}/ativos`, { params });
  }

  search(q: string, page: number = 0, size: number = 10): Observable<Page<JuridicoListResponse>> {
    const params = new HttpParams().set('q', q).set('page', page).set('size', size);
    return this.http.get<Page<JuridicoListResponse>>(`${this.base}/search`, { params });
  }

  findById(id: number): Observable<JuridicoResponse> {
    return this.http.get<JuridicoResponse>(`${this.base}/${id}`);
  }

  existsByCnpj(cnpj: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.base}/cnpj/${cnpj}/exists`);
  }

  create(dto: JuridicoCreateRequest): Observable<JuridicoResponse> {
    return this.http.post<JuridicoResponse>(this.base, dto);
  }

  update(id: number, dto: JuridicoUpdateRequest): Observable<JuridicoResponse> {
    return this.http.put<JuridicoResponse>(`${this.base}/${id}`, dto);
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
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
