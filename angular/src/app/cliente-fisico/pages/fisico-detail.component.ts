import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription, catchError, EMPTY } from 'rxjs';
import { ToastService } from '../../shared/services/toast.service';
import { ClienteFisicoResponse } from '../../api/model/clienteFisicoResponse';
import {ClientesFisicosService} from "../../api";

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
    private clientesFisicosService: ClientesFisicosService,
    private toastService: ToastService,
  ) {}

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

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
