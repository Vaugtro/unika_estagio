import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ExportService {
  private readonly base = '/v1/export';

  constructor(private http: HttpClient) {}

  pdfFisicos(): Observable<Blob> {
    return this.http.get(`${this.base}/clientes/fisicos/pdf`, { responseType: 'blob' });
  }

  pdfJuridicos(): Observable<Blob> {
    return this.http.get(`${this.base}/clientes/juridicos/pdf`, { responseType: 'blob' });
  }

  xlsxFisicos(): Observable<Blob> {
    throw new Error('Not implemented: GET /api/v1/export/clientes/fisicos/xlsx not yet available');
  }

  xlsxJuridicos(): Observable<Blob> {
    throw new Error('Not implemented: GET /api/v1/export/clientes/juridicos/xlsx not yet available');
  }
}
