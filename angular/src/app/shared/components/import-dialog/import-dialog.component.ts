import {Component, Inject, OnDestroy} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {catchError, EMPTY, Subscription} from 'rxjs';

import {ArquivoService} from '../../../api/api/arquivo.service';
import {ToastService} from '../../services/toast.service';
import {downloadBlob} from '../../services/download.util';
import {ImportResult} from '../../models/import-result';

export interface ImportDialogData {
  clienteType: 'fisico' | 'juridico' | 'endereco';
  clienteId?: number;
}

@Component({
  selector: 'app-import-dialog',
  templateUrl: './import-dialog.component.html',
  styleUrls: ['./import-dialog.component.scss'],
})
export class ImportDialogComponent implements OnDestroy {
  selectedFile: File | null = null;
  downloadingTemplate = false;
  importing = false;
  private subscriptions: Subscription[] = [];

  constructor(
    public dialogRef: MatDialogRef<ImportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ImportDialogData,
    private arquivoService: ArquivoService,
    private httpClient: HttpClient,
    private toastService: ToastService,
  ) {
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.selectedFile = input.files[0];
    }
  }

  downloadTemplate(): void {
    this.downloadingTemplate = true;

    let templateCall;
    if (this.data.clienteType === 'fisico') {
      templateCall = this.arquivoService.arquivoTemplateClientesFisicos('response');
    } else if (this.data.clienteType === 'juridico') {
      templateCall = this.arquivoService.arquivoTemplateClientesJuridicos('response');
    } else {
      templateCall = this.arquivoService.arquivoTemplateEnderecos('response');
    }

    this.subscriptions.push(
      templateCall.pipe(
        catchError(() => {
          this.toastService.show('error', 'Erro ao baixar template');
          this.downloadingTemplate = false;
          return EMPTY;
        })
      ).subscribe((response: any) => {
        const blob = response.body as Blob;
        const filename = this.data.clienteType === 'endereco'
          ? 'template-enderecos.xlsx'
          : `template-clientes-${this.data.clienteType}.xlsx`;
        downloadBlob(blob, filename);
        this.toastService.show('success', 'Template baixado com sucesso');
        this.downloadingTemplate = false;
      })
    );
  }

  import(): void {
    if (!this.selectedFile) return;

    this.importing = true;
    const formData = new FormData();
    formData.append('file', this.selectedFile);

    let url: string;
    if (this.data.clienteType === 'fisico') {
      url = '/v1/export/clientes/fisicos/import';
    } else if (this.data.clienteType === 'juridico') {
      url = '/v1/export/clientes/juridicos/import';
    } else {
      url = `/v1/export/enderecos/import?clienteId=${this.data.clienteId}`;
    }

    this.subscriptions.push(
      this.httpClient.post<ImportResult>(url, formData).pipe(
        catchError(() => {
          this.toastService.show('error', 'Erro inesperado. Tente novamente.');
          this.importing = false;
          return EMPTY;
        })
      ).subscribe((result: ImportResult) => {
        this.importing = false;

        if (result.successCount > 0 && result.errors.length === 0) {
          this.toastService.show('success', `${result.successCount} registros importados com sucesso!`);
          this.dialogRef.close(true);
        } else if (result.successCount > 0) {
          const firstError = result.errors[0];
          this.toastService.show('warning', `${result.successCount} registros | Linhas com erro: ${firstError}`);
        } else {
          this.toastService.show('warning', result.errors[0]);
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
