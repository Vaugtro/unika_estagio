import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatDialogModule} from '@angular/material/dialog';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatChipsModule} from '@angular/material/chips';
import {MatListModule} from '@angular/material/list';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatDividerModule} from '@angular/material/divider';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatSelectModule} from '@angular/material/select';
import {icons, LucideAngularModule} from 'lucide-angular';
import {NgxMaskModule} from 'ngx-mask';

// Shared components
import {ToastComponent} from './components/toast/toast.component';
import {ConfirmDialogComponent} from './components/confirm-dialog/confirm-dialog.component';
import {EnderecoFormComponent} from './components/endereco-form/endereco-form.component';
import {EnderecoListComponent} from './components/endereco-list/endereco-list.component';
import {ExportDialogComponent} from './components/export-dialog/export-dialog.component';
import {ImportDialogComponent} from './components/import-dialog/import-dialog.component';

// Fisico components
import {FisicoTableComponent} from '../cliente-fisico/components/fisico-table/fisico-table.component';
import {
  FisicoCreateDialogComponent
} from '../cliente-fisico/components/fisico-create-dialog/fisico-create-dialog.component';
import {
  FisicoEditDialogComponent
} from '../cliente-fisico/components/fisico-edit-dialog/fisico-edit-dialog.component';
import {FisicoRowFormComponent} from '../cliente-fisico/components/fisico-row-form/fisico-row-form.component';
import {FisicoInfoCardComponent} from '../cliente-fisico/components/fisico-info-card/fisico-info-card.component';
import {FisicoDetailComponent} from '../cliente-fisico/pages/fisico-detail.component';

// Juridico components
import {JuridicoTableComponent} from '../cliente-juridico/components/juridico-table/juridico-table.component';
import {
  JuridicoCreateDialogComponent
} from '../cliente-juridico/components/juridico-create-dialog/juridico-create-dialog.component';
import {
  JuridicoEditDialogComponent
} from '../cliente-juridico/components/juridico-edit-dialog/juridico-edit-dialog.component';
import {JuridicoRowFormComponent} from '../cliente-juridico/components/juridico-row-form/juridico-row-form.component';
import {
  JuridicoInfoCardComponent
} from '../cliente-juridico/components/juridico-info-card/juridico-info-card.component';
import {JuridicoDetailComponent} from '../cliente-juridico/pages/juridico-detail.component';

// Endereco components
import {EnderecoTableComponent} from '../endereco/components/endereco-table/endereco-table.component';
import {
  EnderecoCreateDialogComponent
} from '../endereco/components/endereco-create-dialog/endereco-create-dialog.component';

// Home component
import {HomeComponent} from '../home/home.component';

@NgModule({
  declarations: [
    // Shared components
    ToastComponent,
    ConfirmDialogComponent,
    EnderecoFormComponent,
    EnderecoListComponent,
    ExportDialogComponent,
    ImportDialogComponent,
    // Fisico components
    FisicoTableComponent,
    FisicoCreateDialogComponent,
    FisicoEditDialogComponent,
    FisicoRowFormComponent,
    FisicoInfoCardComponent,
    FisicoDetailComponent,
    // Juridico components
    JuridicoTableComponent,
    JuridicoCreateDialogComponent,
    JuridicoEditDialogComponent,
    JuridicoRowFormComponent,
    JuridicoInfoCardComponent,
    JuridicoDetailComponent,
    // Endereco components
    EnderecoTableComponent,
    EnderecoCreateDialogComponent,
    // Home
    HomeComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    LucideAngularModule.pick(icons),
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatToolbarModule,
    MatChipsModule,
    MatListModule,
    MatCheckboxModule,
    MatDividerModule,
    MatSnackBarModule,
    MatSelectModule,
    NgxMaskModule.forRoot(),
  ],
  exports: [
    // Re-export modules for app module
    ReactiveFormsModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatToolbarModule,
    MatChipsModule,
    MatListModule,
    MatCheckboxModule,
    MatDividerModule,
    MatSnackBarModule,
    // Shared components
    ToastComponent,
    ConfirmDialogComponent,
    EnderecoFormComponent,
    EnderecoListComponent,
    ExportDialogComponent,
    ImportDialogComponent,
    // Endereco components
    EnderecoTableComponent,
    EnderecoCreateDialogComponent,
  ],
})
export class SharedModule {
}
