import { Component, Input } from '@angular/core';
import { FisicoResponse } from '../../../shared/models/fisico.model';

@Component({
  selector: 'app-fisico-info-card',
  templateUrl: './fisico-info-card.component.html',
  styleUrls: ['./fisico-info-card.component.scss'],
})
export class FisicoInfoCardComponent {
  @Input() cliente!: FisicoResponse;
}
