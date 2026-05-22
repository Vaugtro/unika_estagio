import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription, catchError, EMPTY } from 'rxjs';
import { JuridicoService } from '../../shared/services/juridico.service';
import { ToastService } from '../../shared/services/toast.service';
import { JuridicoResponse } from '../../shared/models/juridico.model';

@Component({
  selector: 'app-juridico-detail',
  templateUrl: './juridico-detail.component.html',
  styleUrls: ['./juridico-detail.component.scss'],
})
export class JuridicoDetailComponent implements OnInit, OnDestroy {
  cliente?: JuridicoResponse;
  loading = true;
  private sub = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private juridicoService: JuridicoService,
    private toastService: ToastService,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.sub.add(
        this.juridicoService.findById(id).pipe(
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
