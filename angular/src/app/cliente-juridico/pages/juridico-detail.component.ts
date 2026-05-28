import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {catchError, EMPTY, Subscription} from 'rxjs';
import {ToastService} from '../../shared/services/toast.service';
import {ClienteJuridicoResponse} from '../../api/model/clienteJuridicoResponse';
import {ClientesJuridicosService} from "../../api";
import {ConfirmDialogComponent} from '../../shared/components/confirm-dialog/confirm-dialog.component';
import {JuridicoEditDialogComponent} from '../components/juridico-edit-dialog/juridico-edit-dialog.component';

@Component({
  selector: 'app-juridico-detail',
  templateUrl: './juridico-detail.component.html',
  styleUrls: ['./juridico-detail.component.scss'],
})
export class JuridicoDetailComponent implements OnInit, OnDestroy {
  cliente?: ClienteJuridicoResponse;
  loading = true;
  private sub = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private clientesJuridicosService: ClientesJuridicosService,
    private toastService: ToastService,
  ) {
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.sub.add(
        this.clientesJuridicosService.clientesJuridicosGetById(id).pipe(
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

    const dialogRef = this.dialog.open(JuridicoEditDialogComponent, {
      width: '500px',
      data: {cliente: this.cliente},
    });

    this.sub.add(
      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.clientesJuridicosService.clientesJuridicosGetById(this.cliente!.id!).pipe(
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
        title: 'Excluir Cliente Jurídico',
        message: `Confirma a exclusão permanente do cliente "${this.cliente.razaoSocial}"? Esta ação não pode ser desfeita.`,
        confirmText: 'Excluir',
      },
    });

    this.sub.add(
      dialogRef.afterClosed().subscribe((confirmed) => {
        if (!confirmed) return;

        this.sub.add(
          this.clientesJuridicosService.clientesJuridicosHardDelete(this.cliente!.id!).pipe(
            catchError((err) => {
              const msg = err.error?.message || err.statusText || 'Erro ao excluir cliente';
              this.toastService.show('error', msg);
              return EMPTY;
            })
          ).subscribe(() => {
            this.toastService.show('success', 'Cliente jurídico excluído permanentemente');
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
