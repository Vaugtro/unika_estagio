import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {PageEvent} from '@angular/material/paginator';
import {MatDialog} from '@angular/material/dialog';
import {catchError, debounceTime, distinctUntilChanged, EMPTY, Subscription} from 'rxjs';
import {ToastService} from '../../../shared/services/toast.service';
import {ClienteFisicoListResponse} from '../../../api/model/clienteFisicoListResponse';
import {Pageable} from '../../../api/model/pageable';
import {FisicoCreateDialogComponent} from '../fisico-create-dialog/fisico-create-dialog.component';
import {FisicoEditDialogComponent} from '../fisico-edit-dialog/fisico-edit-dialog.component';
import {ExportDialogComponent} from '../../../shared/components/export-dialog/export-dialog.component';
import {ImportDialogComponent} from '../../../shared/components/import-dialog/import-dialog.component';
import {ClienteFisicoResponse} from '../../../api/model/clienteFisicoResponse';
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
  private subscriptions: Subscription[] = [];

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

  openEdit(row: ClienteFisicoListResponse): void {
    const dialogRef = this.dialog.open(FisicoEditDialogComponent, {
      width: '500px',
      data: {cliente: row as ClienteFisicoResponse},
    });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe((result) => {
        if (result) this.loadData();
      })
    );
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
    const dialogRef = this.dialog.open(ImportDialogComponent, {
      data: {clienteType: 'fisico'},
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
