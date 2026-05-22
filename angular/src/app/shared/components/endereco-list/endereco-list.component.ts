import { Component, Input } from '@angular/core';
import { EnderecoListResponse } from '../../models/endereco.model';

@Component({
  selector: 'app-endereco-list',
  templateUrl: './endereco-list.component.html',
  styleUrls: ['./endereco-list.component.scss'],
})
export class EnderecoListComponent {
  @Input() enderecos: EnderecoListResponse[] = [];
}
