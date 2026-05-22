import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './home/home.component';
import { FisicoDetailComponent } from './cliente-fisico/pages/fisico-detail.component';
import { JuridicoDetailComponent } from './cliente-juridico/pages/juridico-detail.component';
import { EnderecoTableComponent } from './endereco/components/endereco-table/endereco-table.component';

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'fisico/:id', component: FisicoDetailComponent },
  { path: 'juridico/:id', component: JuridicoDetailComponent },
  { path: 'enderecos', component: EnderecoTableComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
