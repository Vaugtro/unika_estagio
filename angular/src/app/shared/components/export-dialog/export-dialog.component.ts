import { Component, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ToastService } from '../../services/toast.service';
import { downloadBlob } from '../../services/download.util';
import { Subscription, catchError, EMPTY } from 'rxjs';

export interface ExportDialogData {
  clienteType: 'fisico' | 'juridico' | 'endereco';
}

@Component({
  selector: 'app-export-dialog',
  templateUrl: './export-dialog.component.html',
  styleUrls: ['./export-dialog.component.scss'],
})
export class ExportDialogComponent {
  exportingPdf = false;
  exportingXlsx = false;
  private subscriptions: Subscription[] = [];

  constructor(
    public dialogRef: MatDialogRef<ExportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ExportDialogData,
    private httpClient: HttpClient,
    private toastService: ToastService,
  ) {}

  exportPdf(): void {
    if (this.data.clienteType === 'endereco') {
      this.toastService.show('info', 'Exportação de PDF não disponível para endereços');
      return;
    }

    this.exportingPdf = true;
    const url = this.data.clienteType === 'fisico'
      ? '/v1/export/clientes/fisicos/pdf'
      : '/v1/export/clientes/juridicos/pdf';

    this.subscriptions.push(
      this.httpClient.get(url, { responseType: 'blob' }).pipe(
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
      })
    );
  }

  exportXlsx(): void {
    if (this.data.clienteType === 'endereco') {
      this.toastService.show('info', 'Exportação de planilha não disponível para endereços');
      return;
    }

    this.exportingXlsx = true;
    const url = this.data.clienteType === 'fisico'
      ? '/v1/export/clientes/fisicos/xlsx'
      : '/v1/export/clientes/juridicos/xlsx';

    this.subscriptions.push(
      this.httpClient.get(url, { responseType: 'blob' }).pipe(
        catchError((err) => {
          this.toastService.show('error', err.message || 'Exportação XLSX não disponível');
          this.exportingXlsx = false;
          return EMPTY;
        })
      ).subscribe((blob) => {
        downloadBlob(blob, `clientes-${this.data.clienteType}.xlsx`);
        this.toastService.show('success', 'Planilha exportada com sucesso');
        this.exportingXlsx = false;
        this.dialogRef.close();
      })
    );
  }
}
