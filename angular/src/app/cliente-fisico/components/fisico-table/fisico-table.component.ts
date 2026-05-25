import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {PageEvent} from '@angular/material/paginator';
import {MatDialog} from '@angular/material/dialog';
import {catchError, debounceTime, distinctUntilChanged, EMPTY, Subscription} from 'rxjs';
import {ToastService} from '../../../shared/services/toast.service';
import {ClienteFisicoListResponse} from '../../../api/model/clienteFisicoListResponse';
import {Pageable} from '../../../api/model/pageable';
import {FisicoCreateDialogComponent} from '../fisico-create-dialog/fisico-create-dialog.component';
import {ExportDialogComponent} from '../../../shared/components/export-dialog/export-dialog.component';
import {ImportDialogComponent} from '../../../shared/components/import-dialog/import-dialog.component';
import {ClientesFisicosService} from "../../../api";

@Component({
  selector: 'app-fisico-table',
  templateUrl: './fisico-table.component.html',
  styleUrls: ['./fisico-table.component.scss'],
})
export class FisicoTableComponent implements OnInit, OnDestroy {
  displayedColumns = ['id', 'nome', 'cpf', 'email', 'status', 'actions'];

  dataSource: ClienteFisicoListResponse[] = [];
  totalElements = 0;
  page = 0;
  pageSize = 10;
  loading = false;

  searchControl = new FormControl('');
  editingId: number | null = null;
  private subscriptions: Subscription[] = [];
  private inlineValues: { nome: string; email: string } = {nome: '', email: ''};

  constructor(
    private clientesFisicosService: ClientesFisicosService,
    private toastService: ToastService,
    private dialog: MatDialog,
  ) {
  }

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
    const pageable = this.makePageable();
    console.log('[FisicoTable] loadData', {q, pageable});

    const obs$ = q
      ? this.clientesFisicosService.clientesFisicosSearch(q, pageable)
      : this.clientesFisicosService.clientesFisicosGetAll(pageable);

    this.subscriptions.push(
      obs$.pipe(
        catchError((err) => {
          console.error('[FisicoTable] API error', err);
          this.toastService.show('error', 'Erro ao carregar clientes');
          this.loading = false;
          return EMPTY;
        })
      ).subscribe((page) => {
        console.log('[FisicoTable] API response', page);
        this.dataSource = page.content!;
        console.log('[FisicoTable] dataSource set', {content: page.content, length: page.content?.length});
        this.totalElements = page.totalElements ?? 0;
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

  startEdit(row: ClienteFisicoListResponse): void {
    this.editingId = row.id ?? null;
    this.inlineValues = {nome: row.nome ?? '', email: row.email ?? ''};
  }

  onInlineValueChange(values: { nome: string; email: string }): void {
    this.inlineValues = values;
  }

  saveInline(row: ClienteFisicoListResponse): void {
    if (!this.editingId) return;

    this.subscriptions.push(
      this.clientesFisicosService.clientesFisicosUpdate(row.id!, {
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

  toggleStatus(row: ClienteFisicoListResponse): void {
    const obs$ = row.estaAtivo
      ? this.clientesFisicosService.clientesFisicosInactivate(row.id!)
      : this.clientesFisicosService.clientesFisicosActivate(row.id!);

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
      data: {clienteType: 'fisico', searchQuery: this.searchControl.value?.trim() || undefined},
    });
  }

  openImport(): void {
    this.dialog.open(ImportDialogComponent, {
      data: {clienteType: 'fisico'},
    });
  }

  private makePageable(): Pageable {
    return {page: this.page, size: this.pageSize, sort: []};
  }
}
