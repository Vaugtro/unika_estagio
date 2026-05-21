# Service API Contracts

## REST API Endpoints (assumed Spring Boot REST)

These endpoints mirror the existing service layer. The Spring app already has
REST controllers (or they would need to be added). Adjust base URL in
`environment.ts`.

```
BASE_URL = /api/v1
```

---

## FisicoService

| Method | HTTP | Endpoint | Wicket Ref |
|--------|------|----------|------------|
| `findById(id)` | `GET /clientes/fisicos/{id}` | `ClienteFisicoQueryService.findById()` |
| `findByIdList(id)` | `GET /clientes/fisicos/{id}/list` | `ClienteFisicoQueryService.findByIdList()` |
| `findAll(page, size)` | `GET /clientes/fisicos?page=0&size=10` | `ClienteFisicoQueryService.findAll(pageable)` |
| `findAllActive(page, size)` | `GET /clientes/fisicos/active?page=0&size=10` | `ClienteFisicoQueryService.findAllActive(pageable)` |
| `create(dto)` | `POST /clientes/fisicos` | `ClienteFisicoLifecycleService.create()` |
| `update(id, dto)` | `PUT /clientes/fisicos/{id}` | `ClienteFisicoLifecycleService.update()` |
| `delete(id)` | `DELETE /clientes/fisicos/{id}` | `ClienteFisicoLifecycleService.delete()` |
| `hardDelete(id)` | `DELETE /clientes/fisicos/{id}/hard` | `ClienteFisicoLifecycleService.hardDelete()` |
| `activate(id)` | `PATCH /clientes/fisicos/{id}/activate` | `ClienteFisicoLifecycleService.activate()` |
| `inactivate(id)` | `PATCH /clientes/fisicos/{id}/inactivate` | `ClienteFisicoLifecycleService.inactivate()` |
| `existsByCpf(cpf)` | `GET /clientes/fisicos/exists?cpf={cpf}` | `ClienteFisicoQueryService.existsByCpf()` |
| `count()` | `GET /clientes/fisicos/count` | `ClienteFisicoQueryService.count()` |

```typescript
// fisico.service.ts
@Injectable({ providedIn: 'root' })
export class FisicoService {
  constructor(private http: HttpClient) {}

  findAll(page: number, size: number): Observable<Page<FisicoListResponse>> {
    return this.http.get<Page<FisicoListResponse>>(`/api/v1/clientes/fisicos`, {
      params: { page, size }
    });
  }

  findById(id: number): Observable<FisicoResponse> {
    return this.http.get<FisicoResponse>(`/api/v1/clientes/fisicos/${id}`);
  }

  create(dto: FisicoCreateRequest): Observable<FisicoResponse> {
    return this.http.post<FisicoResponse>(`/api/v1/clientes/fisicos`, dto);
  }

  update(id: number, dto: FisicoUpdateRequest): Observable<FisicoResponse> {
    return this.http.put<FisicoResponse>(`/api/v1/clientes/fisicos/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`/api/v1/clientes/fisicos/${id}`);
  }

  activate(id: number): Observable<void> {
    return this.http.patch<void>(`/api/v1/clientes/fisicos/${id}/activate`, {});
  }

  inactivate(id: number): Observable<void> {
    return this.http.patch<void>(`/api/v1/clientes/fisicos/${id}/inactivate`, {});
  }
}
```

---

## JuridicoService

Mirrors FisicoService exactly, with `cnpj` replacing `cpf` and additional
`inscricaoEstadual` / `dataCriacaoEmpresa` fields.

| Method | HTTP | Endpoint |
|--------|------|----------|
| `findById(id)` | `GET /clientes/juridicos/{id}` |
| `findAll(page, size)` | `GET /clientes/juridicos?page=0&size=10` |
| `create(dto)` | `POST /clientes/juridicos` |
| `update(id, dto)` | `PUT /clientes/juridicos/{id}` |
| `delete(id)` | `DELETE /clientes/juridicos/{id}` |
| `hardDelete(id)` | `DELETE /clientes/juridicos/{id}/hard` |
| `activate(id)` | `PATCH /clientes/juridicos/{id}/activate` |
| `inactivate(id)` | `PATCH /clientes/juridicos/{id}/inactivate` |
| `existsByCnpj(cnpj)` | `GET /clientes/juridicos/exists?cnpj={cnpj}` |

---

## EnderecoService

| Method | HTTP | Endpoint | Wicket Ref |
|--------|------|----------|------------|
| `findAllByClienteId(id)` | `GET /enderecos?clienteId={id}` | `EnderecoService.findAllByClienteId()` |
| `findById(id)` | `GET /enderecos/{id}` | `EnderecoService.findById()` |
| `create(dto)` | `POST /enderecos` | `EnderecoService.create()` |
| `createForCliente(clienteId, dto)` | `POST /enderecos?clienteId={id}` | `EnderecoService.createForCliente()` |
| `update(id, dto)` | `PUT /enderecos/{id}` | `EnderecoService.update()` |
| `setAsPrincipal(id)` | `PATCH /enderecos/{id}/principal` | `EnderecoService.setAsPrincipal()` |
| `delete(id)` | `DELETE /enderecos/{id}` | `EnderecoService.delete()` |
| `hasPrincipal(clienteId)` | `GET /enderecos/has-principal?clienteId={id}` | `EnderecoService.hasPrincipalAddress()` |

---

## ExportService

| Method | HTTP | Wicket Ref |
|--------|------|------------|
| `pdfFisicos()` | `GET /export/fisicos/pdf` | `ExportService.pdfFisicos()` |
| `xlsxFisicos()` | `GET /export/fisicos/xlsx` | `ExportService.xlsxFisicos()` |
| `pdfJuridicos()` | `GET /export/juridicos/pdf` | `ExportService.pdfJuridicos()` |
| `xlsxJuridicos()` | `GET /export/juridicos/xlsx` | `ExportService.xlsxJuridicos()` |
| `pdfEnderecos(clienteId)` | `GET /export/enderecos/pdf?clienteId={id}` | `ExportService.pdfEnderecos()` |
| `xlsxEnderecos(clienteId)` | `GET /export/enderecos/xlsx?clienteId={id}` | `ExportService.xlsxEnderecos()` |

Implementation: `HttpClient.get(url, { responseType: 'blob' })` + trigger browser download.

```typescript
downloadBlob(blob: Blob, filename: string) {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  window.URL.revokeObjectURL(url);
}
```
