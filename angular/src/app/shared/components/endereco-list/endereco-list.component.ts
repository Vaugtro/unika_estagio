import { Component, Input } from '@angular/core';
import { EnderecoResponse } from '../../../api/model/enderecoResponse';

@Component({
  selector: 'app-endereco-list',
  templateUrl: './endereco-list.component.html',
  styleUrls: ['./endereco-list.component.scss'],
})
export class EnderecoListComponent {
  @Input() enderecos: EnderecoResponse[] = [];
}
