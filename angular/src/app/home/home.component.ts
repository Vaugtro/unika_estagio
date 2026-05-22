import { Component } from '@angular/core';
import { FisicoTableComponent } from '../cliente-fisico/components/fisico-table/fisico-table.component';
import { JuridicoTableComponent } from '../cliente-juridico/components/juridico-table/juridico-table.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  activeTab: 'fisico' | 'juridico' = 'fisico';
}
