import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormControl } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { Subscription, debounceTime, distinctUntilChanged, catchError, EMPTY } from 'rxjs';
import { JuridicoService } from '../../../shared/services/juridico.service';
import { ToastService } from '../../../shared/services/toast.service';
import { JuridicoListResponse } from '../../../shared/models/juridico.model';
import { JuridicoCreateDialogComponent } from '../juridico-create-dialog/juridico-create-dialog.component';
import { ExportDialogComponent } from '../../../shared/components/export-dialog/export-dialog.component';
import { ImportDialogComponent } from '../../../shared/components/import-dialog/import-dialog.component';

@Component({
  selector: 'app-juridico-table',
  templateUrl: './juridico-table.component.html',
  styleUrls: ['./juridico-table.component.scss'],
})
export class JuridicoTableComponent implements OnInit, OnDestroy {
  displayedColumns = ['id', 'razaoSocial', 'cnpj', 'email', 'status', 'actions'];

  dataSource: JuridicoListResponse[] = [];
  totalElements = 0;
  page = 0;
  pageSize = 10;
  loading = false;

  searchControl = new FormControl('');
  private subscriptions: Subscription[] = [];

  editingId: number | null = null;
  private inlineValues: { razaoSocial: string; inscricaoEstadual: string; email: string } = { razaoSocial: '', inscricaoEstadual: '', email: '' };

  constructor(
    private juridicoService: JuridicoService,
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
  }

  loadData(): void {
    this.loading = true;
    const q = this.searchControl.value?.trim();

    const obs$ = q
      ? this.juridicoService.search(q, this.page, this.pageSize)
      : this.juridicoService.findAll(this.page, this.pageSize);

    this.subscriptions.push(
      obs$.pipe(
        catchError(() => {
          this.toastService.show('error', 'Erro ao carregar clientes');
          this.loading = false;
          return EMPTY;
        })
      ).subscribe((page) => {
        this.dataSource = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      })
    );
  }

  onPageChange(event: PageEvent): void {
    this.page = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadData();
  }

  clearSearch(): void {
    this.searchControl.setValue('');
  }

  startEdit(row: JuridicoListResponse): void {
    this.editingId = row.id;
    this.inlineValues = { razaoSocial: row.razaoSocial, inscricaoEstadual: '', email: row.email };
  }

  onInlineValueChange(values: { razaoSocial: string; inscricaoEstadual: string; email: string }): void {
    this.inlineValues = values;
  }

  saveInline(row: JuridicoListResponse): void {
    if (!this.editingId) return;

    this.subscriptions.push(
      this.juridicoService.update(row.id, {
        razaoSocial: this.inlineValues.razaoSocial,
        inscricaoEstadual: this.inlineValues.inscricaoEstadual,
        email: this.inlineValues.email || undefined,
        dataCriacaoEmpresa: '',
        estaAtivo: row.estaAtivo,
      }).pipe(
        catchError(() => {
          this.toastService.show('error', 'Erro ao atualizar cliente');
          return EMPTY;
        })
      ).subscribe(() => {
        this.toastService.show('success', 'Cliente atualizado');
        this.editingId = null;
        this.loadData();
      })
    );
  }

  cancelEdit(): void {
    this.editingId = null;
  }

  toggleStatus(row: JuridicoListResponse): void {
    const obs$ = row.estaAtivo
      ? this.juridicoService.inactivate(row.id)
      : this.juridicoService.activate(row.id);

    this.subscriptions.push(
      obs$.pipe(
        catchError(() => {
          this.toastService.show('error', `Erro ao ${row.estaAtivo ? 'inativar' : 'ativar'} cliente`);
          return EMPTY;
        })
      ).subscribe(() => {
        this.toastService.show('success', `Cliente ${row.estaAtivo ? 'inativado' : 'ativado'} com sucesso`);
        this.loadData();
      })
    );
  }

  openCreate(): void {
    const dialogRef = this.dialog.open(JuridicoCreateDialogComponent, {
      width: '800px',
    });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe((result) => {
        if (result) this.loadData();
      })
    );
  }

  openExport(): void {
    this.dialog.open(ExportDialogComponent, {
      data: { clienteType: 'juridico' },
    });
  }

  openImport(): void {
    this.dialog.open(ImportDialogComponent, {
      data: { clienteType: 'juridico' },
    });
  }
}
