import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { Subscription, catchError, EMPTY } from 'rxjs';
import { ToastService } from '../../../shared/services/toast.service';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { EnderecoListResponse } from '../../../api/model/enderecoListResponse';
import { Pageable } from '../../../api/model/pageable';
import {
  EnderecoCreateDialogComponent,
  EnderecoCreateDialogData,
} from '../endereco-create-dialog/endereco-create-dialog.component';
import { ExportDialogComponent } from '../../../shared/components/export-dialog/export-dialog.component';
import { ImportDialogComponent } from '../../../shared/components/import-dialog/import-dialog.component';
import {EnderecosService} from "../../../api";

@Component({
  selector: 'app-endereco-table',
  templateUrl: './endereco-table.component.html',
  styleUrls: ['./endereco-table.component.scss'],
})
export class EnderecoTableComponent implements OnInit, OnDestroy {
  @Input() clienteId!: number;
  @Input() clienteType!: 'fisico' | 'juridico';

  displayedColumns = ['logradouro', 'numero', 'bairro', 'cidade', 'estado', 'cep', 'principal', 'actions'];

  dataSource: EnderecoListResponse[] = [];
  totalElements = 0;
  page = 0;
  pageSize = 10;
  loading = false;

  private subscriptions: Subscription[] = [];
  private dataSubscription?: Subscription;

  constructor(
    private enderecosService: EnderecosService,
    private toastService: ToastService,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
    this.dataSubscription?.unsubscribe();
  }

  loadData(): void {
    this.loading = true;
    const pageable = this.makePageable();
    console.log('[EnderecoTable] loadData', { clienteId: this.clienteId, pageable });

    this.dataSubscription?.unsubscribe();
    this.dataSubscription = this.enderecosService.enderecosFindAllByClienteId(this.clienteId, this.makePageable()).pipe(
      catchError((err) => {
        console.error('[EnderecoTable] API error', err);
        this.toastService.show('error', 'Erro ao carregar endereços');
        this.loading = false;
        return EMPTY;
      })
    ).subscribe((page) => {
      console.log('[EnderecoTable] API response', page);
      this.dataSource = page.content!;
      console.log('[EnderecoTable] dataSource set', { content: page.content, length: page.content?.length });
      this.totalElements = page.totalElements ?? 0;
      this.loading = false;
    });
  }

  private makePageable(): Pageable {
    return { page: this.page, size: this.pageSize, sort: [] };
  }

  onPageChange(event: PageEvent): void {
    this.page = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }

  setAsPrincipal(row: EnderecoListResponse): void {
    if (row.principal) {
      this.toastService.show('info', 'Este endereço já é o principal');
      return;
    }

    this.subscriptions.push(
      this.enderecosService.enderecosSetAsPrincipal(row.id!).pipe(
        catchError(() => {
          this.toastService.show('error', 'Erro ao definir endereço como principal');
          return EMPTY;
        })
      ).subscribe(() => {
        this.toastService.show('success', 'Endereço definido como principal');
        this.loadData();
      })
    );
  }

  delete(row: EnderecoListResponse): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Excluir endereço',
        message: `Confirma a exclusão do endereço "${row.logradouro}, ${row.numero}"?`,
      },
    });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe((confirmed) => {
        if (!confirmed) return;

        this.subscriptions.push(
          this.enderecosService.enderecosDelete(row.id!).pipe(
            catchError(() => {
              this.toastService.show('error', 'Erro ao excluir endereço');
              return EMPTY;
            })
          ).subscribe(() => {
            this.toastService.show('success', 'Endereço excluído');
            this.loadData();
          })
        );
      })
    );
  }

  openCreate(): void {
    const dialogRef = this.dialog.open<EnderecoCreateDialogComponent, EnderecoCreateDialogData, boolean>(
      EnderecoCreateDialogComponent,
      {
        width: '600px',
        data: { clienteId: this.clienteId, clienteType: this.clienteType },
      }
    );

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe((result) => {
        if (result) this.loadData();
      })
    );
  }

  openExport(): void {
    this.dialog.open(ExportDialogComponent, {
      data: { clienteType: this.clienteType },
    });
  }

  openImport(): void {
    this.dialog.open(ImportDialogComponent, {
      data: { clienteType: this.clienteType },
    });
  }
}
