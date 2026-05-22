import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormControl } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { Subscription, debounceTime, distinctUntilChanged, catchError, EMPTY } from 'rxjs';
import { EnderecoService } from '../../../shared/services/endereco.service';
import { ToastService } from '../../../shared/services/toast.service';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { EnderecoListResponse } from '../../../shared/models/endereco.model';
import { EnderecoCreateDialogComponent } from '../endereco-create-dialog/endereco-create-dialog.component';
import { ExportDialogComponent } from '../../../shared/components/export-dialog/export-dialog.component';
import { ImportDialogComponent } from '../../../shared/components/import-dialog/import-dialog.component';

@Component({
  selector: 'app-endereco-table',
  templateUrl: './endereco-table.component.html',
  styleUrls: ['./endereco-table.component.scss'],
})
export class EnderecoTableComponent implements OnInit, OnDestroy {
  displayedColumns = ['id', 'logradouro', 'numero', 'bairro', 'cidade', 'estado', 'cep', 'principal', 'actions'];

  dataSource: EnderecoListResponse[] = [];
  totalElements = 0;
  page = 0;
  pageSize = 10;
  loading = false;

  searchControl = new FormControl('');
  private subscriptions: Subscription[] = [];
  private dataSubscription?: Subscription;

  constructor(
    private enderecoService: EnderecoService,
    private toastService: ToastService,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.loadData();

    this.subscriptions.push(
      this.searchControl.valueChanges
        .pipe(debounceTime(300), distinctUntilChanged())
        .subscribe(() => {
          this.page = 0;
          this.loadData();
        })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
    this.dataSubscription?.unsubscribe();
  }

  loadData(): void {
    this.loading = true;
    const q = this.searchControl.value?.trim() || '';

    this.dataSubscription?.unsubscribe();
    this.dataSubscription = this.enderecoService.search(q, this.page, this.pageSize).pipe(
      catchError(() => {
        this.toastService.show('error', 'Erro ao carregar endereços');
        this.loading = false;
        return EMPTY;
      })
    ).subscribe((page) => {
      this.dataSource = page.content;
      this.totalElements = page.totalElements;
      this.loading = false;
    });
  }

  onPageChange(event: PageEvent): void {
    this.page = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }

  clearSearch(): void {
    this.searchControl.setValue('');
  }

  setAsPrincipal(row: EnderecoListResponse): void {
    if (row.principal) {
      this.toastService.show('info', 'Este endereço já é o principal');
      return;
    }

    this.subscriptions.push(
      this.enderecoService.setAsPrincipal(row.id).pipe(
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
          this.enderecoService.delete(row.id).pipe(
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
    const dialogRef = this.dialog.open(EnderecoCreateDialogComponent, {
      width: '600px',
    });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe((result) => {
        if (result) this.loadData();
      })
    );
  }

  openExport(): void {
    this.dialog.open(ExportDialogComponent, {
      data: { clienteType: 'endereco' },
    });
  }

  openImport(): void {
    this.dialog.open(ImportDialogComponent, {
      data: { clienteType: 'endereco' },
    });
  }
}
