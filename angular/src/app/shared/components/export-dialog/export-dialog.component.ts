import {Component, Inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {ToastService} from '../../services/toast.service';
import {downloadBlob} from '../../services/download.util';
import {catchError, EMPTY} from 'rxjs';

export interface ExportDialogData {
  clienteType: 'fisico' | 'juridico' | 'endereco';
  clienteId?: number;
  searchQuery?: string;
}

@Component({
  selector: 'app-export-dialog',
  templateUrl: './export-dialog.component.html',
  styleUrls: ['./export-dialog.component.scss'],
})
export class ExportDialogComponent {
  exportingPdf = false;
  exportingXlsx = false;

  constructor(
    public dialogRef: MatDialogRef<ExportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ExportDialogData,
    private httpClient: HttpClient,
    private toastService: ToastService,
  ) {
  }

  private getApiBaseUrl(): string {
    return (window as any).__env?.apiUrl || 'http://localhost:8080';
  }

  exportPdf(): void {
    this.exportingPdf = true;

    const url = this.data.clienteType === 'endereco'
      ? `${this.getApiBaseUrl()}/v1/export/clientes/${this.data.clienteId}/enderecos/pdf`
      : this.buildUrl(
          this.data.clienteType === 'fisico'
            ? '/v1/export/clientes/fisicos/pdf'
            : '/v1/export/clientes/juridicos/pdf'
        );

    this.httpClient.get(url, {responseType: 'blob'}).pipe(
      catchError(() => {
        this.toastService.show('error', 'Erro ao exportar PDF');
        this.exportingPdf = false;
        return EMPTY;
      })
    ).subscribe((blob) => {
      downloadBlob(blob, `clientes-${this.data.clienteType}.pdf`);
      this.toastService.show('success', 'PDF exportado com sucesso');
      this.exportingPdf = false;
      this.dialogRef.close();
    });
  }

  exportXlsx(): void {
    this.exportingXlsx = true;

    const url = this.data.clienteType === 'endereco'
      ? `${this.getApiBaseUrl()}/v1/export/clientes/${this.data.clienteId}/enderecos/xlsx`
      : this.buildUrl(
          this.data.clienteType === 'fisico'
            ? '/v1/export/clientes/fisicos/xlsx'
            : '/v1/export/clientes/juridicos/xlsx'
        );

    this.httpClient.get(url, {responseType: 'blob'}).pipe(
      catchError(() => {
        this.toastService.show('error', 'Erro ao exportar planilha');
        this.exportingXlsx = false;
        return EMPTY;
      })
    ).subscribe((blob) => {
      downloadBlob(blob, `clientes-${this.data.clienteType}.xlsx`);
      this.toastService.show('success', 'Planilha exportada com sucesso');
      this.exportingXlsx = false;
      this.dialogRef.close();
    });
  }

  private buildUrl(base: string): string {
    const q = this.data.searchQuery?.trim();
    return q ? `${this.getApiBaseUrl()}${base}?q=${encodeURIComponent(q)}` : `${this.getApiBaseUrl()}${base}`;
  }
}
