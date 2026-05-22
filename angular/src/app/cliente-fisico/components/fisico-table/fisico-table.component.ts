import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormControl } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { Subscription, debounceTime, distinctUntilChanged, catchError, EMPTY } from 'rxjs';
import { FisicoService } from '../../../shared/services/fisico.service';
import { ToastService } from '../../../shared/services/toast.service';
import { FisicoListResponse } from '../../../shared/models/fisico.model';
import { FisicoCreateDialogComponent } from '../fisico-create-dialog/fisico-create-dialog.component';
import { ExportDialogComponent } from '../../../shared/components/export-dialog/export-dialog.component';
import { ImportDialogComponent } from '../../../shared/components/import-dialog/import-dialog.component';

@Component({
  selector: 'app-fisico-table',
  templateUrl: './fisico-table.component.html',
  styleUrls: ['./fisico-table.component.scss'],
})
export class FisicoTableComponent implements OnInit, OnDestroy {
  displayedColumns = ['id', 'nome', 'cpf', 'email', 'status', 'actions'];

  dataSource: FisicoListResponse[] = [];
  totalElements = 0;
  page = 0;
  pageSize = 10;
  loading = false;

  searchControl = new FormControl('');
  private subscriptions: Subscription[] = [];

  editingId: number | null = null;
  private inlineValues: { nome: string; email: string } = { nome: '', email: '' };

  constructor(
    private fisicoService: FisicoService,
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
      ? this.fisicoService.search(q, this.page, this.pageSize)
      : this.fisicoService.findAll(this.page, this.pageSize);

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

  startEdit(row: FisicoListResponse): void {
    this.editingId = row.id;
    this.inlineValues = { nome: row.nome, email: row.email };
  }

  onInlineValueChange(values: { nome: string; email: string }): void {
    this.inlineValues = values;
  }

  saveInline(row: FisicoListResponse): void {
    if (!this.editingId) return;

    this.subscriptions.push(
      this.fisicoService.update(row.id, {
        nome: this.inlineValues.nome,
        email: this.inlineValues.email || undefined,
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

  toggleStatus(row: FisicoListResponse): void {
    const obs$ = row.estaAtivo
      ? this.fisicoService.inactivate(row.id)
      : this.fisicoService.activate(row.id);

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
    const dialogRef = this.dialog.open(FisicoCreateDialogComponent, {
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
      data: { clienteType: 'fisico' },
    });
  }

  openImport(): void {
    this.dialog.open(ImportDialogComponent, {
      data: { clienteType: 'fisico' },
    });
  }
}
