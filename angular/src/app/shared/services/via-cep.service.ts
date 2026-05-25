import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';

export interface ViaCepResponse {
  cep: string;
  logradouro: string;
  complemento: string;
  bairro: string;
  localidade: string;
  uf: string;
  ibge: string;
  gia: string;
  ddd: string;
  siafi: string;
  erro?: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class ViaCepService {
  private readonly baseUrl = 'https://viacep.com.br/ws';

  constructor(private http: HttpClient) {
  }

  lookup(cep: string): Observable<ViaCepResponse> {
    const clean = cep.replace(/\D/g, '');
    if (clean.length !== 8) {
      return of({erro: true} as ViaCepResponse);
    }

    return this.http.get<ViaCepResponse>(`${this.baseUrl}/${clean}/json/`).pipe(
      catchError(() => of({erro: true} as ViaCepResponse)),
    );
  }
}
