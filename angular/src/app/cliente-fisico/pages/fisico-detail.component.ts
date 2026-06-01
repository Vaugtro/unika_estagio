import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {catchError, EMPTY, Subscription} from 'rxjs';
import {ToastService} from '../../shared/services/toast.service';
import {ClienteFisicoResponse} from '../../api/model/clienteFisicoResponse';
import {ClientesFisicosService} from "../../api";
import {ConfirmDialogComponent} from '../../shared/components/confirm-dialog/confirm-dialog.component';
import {FisicoEditDialogComponent} from '../components/fisico-edit-dialog/fisico-edit-dialog.component';

@Component({
  selector: 'app-fisico-detail',
  templateUrl: './fisico-detail.component.html',
  styleUrls: ['./fisico-detail.component.scss'],
})
export class FisicoDetailComponent implements OnInit, OnDestroy {
  cliente?: ClienteFisicoResponse;
  loading = true;
  private sub = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private clientesFisicosService: ClientesFisicosService,
    private toastService: ToastService,
  ) {
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.sub.add(
        this.clientesFisicosService.clientesFisicosGetById(id).pipe(
          catchError(() => {
            this.toastService.show('error', 'Erro ao carregar cliente');
            this.loading = false;
            return EMPTY;
          })
        ).subscribe((data) => {
          this.cliente = data;
          this.loading = false;
        })
      );
    }
  }

  openEdit(): void {
    if (!this.cliente) return;

    const dialogRef = this.dialog.open(FisicoEditDialogComponent, {
      width: '500px',
      data: {cliente: this.cliente},
    });

    this.sub.add(
      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.clientesFisicosService.clientesFisicosGetById(this.cliente!.id!).pipe(
            catchError(() => {
              this.toastService.show('error', 'Erro ao recarregar cliente');
              return EMPTY;
            })
          ).subscribe((data) => {
            this.cliente = data;
          });
        }
      })
    );
  }

  delete(): void {
    if (!this.cliente?.id) return;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Excluir Cliente Físico',
        message: `Confirma a exclusão permanente do cliente "${this.cliente.nome}"? Esta ação não pode ser desfeita.`,
        confirmText: 'Excluir',
      },
    });

    this.sub.add(
      dialogRef.afterClosed().subscribe((confirmed) => {
        if (!confirmed) return;

        this.sub.add(
          this.clientesFisicosService.clientesFisicosHardDelete(this.cliente!.id!).pipe(
            catchError((err) => {
              const ve = err.error?.validationErrors;
              const msg = ve ? Object.entries(ve).map(([k, v]) => `${k}: ${v}`).join('; ') : (err.error?.message || err.statusText || 'Erro ao excluir cliente');
              this.toastService.show('error', msg);
              return EMPTY;
            })
          ).subscribe(() => {
            this.toastService.show('success', 'Cliente físico excluído permanentemente');
            this.router.navigate(['/home']);
          })
        );
      })
    );
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
