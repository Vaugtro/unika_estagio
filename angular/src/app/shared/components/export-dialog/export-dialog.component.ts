import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ExportService } from '../../services/export.service';
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
    private exportService: ExportService,
    private toastService: ToastService,
  ) {}

  exportPdf(): void {
    if (this.data.clienteType === 'endereco') {
      this.toastService.show('info', 'Exportação de PDF não disponível para endereços');
      return;
    }

    this.exportingPdf = true;
    const obs$ = this.data.clienteType === 'fisico'
      ? this.exportService.pdfFisicos()
      : this.exportService.pdfJuridicos();

    this.subscriptions.push(
      obs$.pipe(
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
    const obs$ = this.data.clienteType === 'fisico'
      ? this.exportService.xlsxFisicos()
      : this.exportService.xlsxJuridicos();

    this.subscriptions.push(
      obs$.pipe(
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
