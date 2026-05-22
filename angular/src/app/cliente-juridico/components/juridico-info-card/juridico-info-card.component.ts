import { Component, Input } from '@angular/core';
import { JuridicoResponse } from '../../../shared/models/juridico.model';

@Component({
  selector: 'app-juridico-info-card',
  templateUrl: './juridico-info-card.component.html',
  styleUrls: ['./juridico-info-card.component.scss'],
})
export class JuridicoInfoCardComponent {
  @Input() cliente!: JuridicoResponse;
}
