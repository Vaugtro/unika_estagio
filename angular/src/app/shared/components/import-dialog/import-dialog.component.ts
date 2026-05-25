import { Component, Inject, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subscription, catchError, EMPTY } from 'rxjs';

import { ArquivoService } from '../../../api/api/arquivo.service';
import { ToastService } from '../../services/toast.service';
import { downloadBlob } from '../../services/download.util';

export interface ImportDialogData {
  clienteType: 'fisico' | 'juridico' | 'endereco';
}

@Component({
  selector: 'app-import-dialog',
  templateUrl: './import-dialog.component.html',
  styleUrls: ['./import-dialog.component.scss'],
})
export class ImportDialogComponent implements OnDestroy {
  selectedFile: File | null = null;
  downloadingTemplate = false;
  private subscriptions: Subscription[] = [];

  constructor(
    public dialogRef: MatDialogRef<ImportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ImportDialogData,
    private arquivoService: ArquivoService,
    private toastService: ToastService,
  ) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.selectedFile = input.files[0];
    }
  }

  downloadTemplate(): void {
    if (this.data.clienteType === 'endereco') {
      this.toastService.show('info', 'Download de template não disponível para endereços');
      return;
    }

    this.downloadingTemplate = true;

    const templateCall = this.data.clienteType === 'fisico'
      ? this.arquivoService.arquivoTemplateClientesFisicos('response')
      : this.arquivoService.arquivoTemplateClientesJuridicos('response');

    this.subscriptions.push(
      templateCall.pipe(
        catchError(() => {
          this.toastService.show('error', 'Erro ao baixar template');
          this.downloadingTemplate = false;
          return EMPTY;
        })
      ).subscribe((response: HttpResponse<string>) => {
        const blob = response.body as unknown as Blob;
        downloadBlob(blob, `template-clientes-${this.data.clienteType}.xlsx`);
        this.toastService.show('success', 'Template baixado com sucesso');
        this.downloadingTemplate = false;
      })
    );
  }

  import(): void {
    // TODO: implement upload when backend endpoint is ready
    this.dialogRef.close();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
