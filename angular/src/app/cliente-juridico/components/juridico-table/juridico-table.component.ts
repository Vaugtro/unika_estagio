import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {PageEvent} from '@angular/material/paginator';
import {MatDialog} from '@angular/material/dialog';
import {catchError, debounceTime, distinctUntilChanged, EMPTY, Subscription} from 'rxjs';
import {ToastService} from '../../../shared/services/toast.service';
import {Pageable} from '../../../api/model/pageable';
import {JuridicoCreateDialogComponent} from '../juridico-create-dialog/juridico-create-dialog.component';
import {ExportDialogComponent} from '../../../shared/components/export-dialog/export-dialog.component';
import {ImportDialogComponent} from '../../../shared/components/import-dialog/import-dialog.component';
import {ClienteJuridicoListResponse} from "../../../api/model/clienteJuridicoListResponse";
import {ClientesJuridicosService} from "../../../api";

@Component({
  selector: 'app-juridico-table',
  templateUrl: './juridico-table.component.html',
  styleUrls: ['./juridico-table.component.scss'],
})
export class JuridicoTableComponent implements OnInit, OnDestroy {
  displayedColumns = ['id', 'razaoSocial', 'cnpj', 'email', 'status', 'actions'];

  dataSource: ClienteJuridicoListResponse[] = [];
  totalElements = 0;
  page = 0;
  pageSize = 10;
  loading = false;

  searchControl = new FormControl('');
  editingId: number | null = null;
  private subscriptions: Subscription[] = [];
  private inlineValues: { razaoSocial: string; inscricaoEstadual: string; email: string } = {
    razaoSocial: '',
    inscricaoEstadual: '',
    email: ''
  };

  constructor(
    private clientesJuridicosService: ClientesJuridicosService,
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
    console.log('[JuridicoTable] loadData', {q, pageable});

    const obs$ = q
      ? this.clientesJuridicosService.clientesJuridicosSearch(q, pageable)
      : this.clientesJuridicosService.clientesJuridicosGetAll(pageable);

    this.subscriptions.push(
      obs$.pipe(
        catchError((err) => {
          console.error('[JuridicoTable] API error', err);
          this.toastService.show('error', 'Erro ao carregar clientes');
          this.loading = false;
          return EMPTY;
        })
      ).subscribe((page) => {
        console.log('[JuridicoTable] API response', page);
        this.dataSource = page.content!;
        console.log('[JuridicoTable] dataSource set', {content: page.content, length: page.content?.length});
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

  startEdit(row: ClienteJuridicoListResponse): void {
    this.editingId = row.id ?? null;
    this.inlineValues = {razaoSocial: row.razaoSocial ?? '', inscricaoEstadual: '', email: row.email ?? ''};
  }

  onInlineValueChange(values: { razaoSocial: string; inscricaoEstadual: string; email: string }): void {
    this.inlineValues = values;
  }

  saveInline(row: ClienteJuridicoListResponse): void {
    if (!this.editingId) return;

    this.subscriptions.push(
      this.clientesJuridicosService.clientesJuridicosUpdate(row.id!, {
        razaoSocial: this.inlineValues.razaoSocial,
        inscricaoEstadual: this.inlineValues.inscricaoEstadual,
        email: this.inlineValues.email || undefined,
        dataCriacaoEmpresa: '',
        estaAtivo: row.estaAtivo,
        enderecos: [],
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

  toggleStatus(row: ClienteJuridicoListResponse): void {
    const obs$ = row.estaAtivo
      ? this.clientesJuridicosService.clientesJuridicosInactivate(row.id!)
      : this.clientesJuridicosService.clientesJuridicosActivate(row.id!);

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
      data: {clienteType: 'juridico', searchQuery: this.searchControl.value?.trim() || undefined},
    });
  }

  openImport(): void {
    const dialogRef = this.dialog.open(ImportDialogComponent, {
      data: {clienteType: 'juridico'},
    });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe((result) => {
        if (result) this.loadData();
      })
    );
  }

  private makePageable(): Pageable {
    return {page: this.page, size: this.pageSize, sort: []};
  }
}
