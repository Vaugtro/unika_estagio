import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {debounceTime, distinctUntilChanged, Subscription} from 'rxjs';
import {VALIDATION} from '../../../shared/validators/validation-constants';

@Component({
  selector: 'app-juridico-row-form',
  templateUrl: './juridico-row-form.component.html',
  styleUrls: ['./juridico-row-form.component.scss'],
})
export class JuridicoRowFormComponent implements OnInit, OnDestroy {
  @Input() razaoSocial = '';
  @Input() email = '';
  @Output() save = new EventEmitter<void>();
  @Output() valueChange = new EventEmitter<{ razaoSocial: string; inscricaoEstadual: string; email: string }>();

  form!: FormGroup;
  private sub = new Subscription();

  constructor(private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      razaoSocial: [this.razaoSocial, [Validators.required, Validators.minLength(VALIDATION.RAZAO_SOCIAL_MIN), Validators.maxLength(VALIDATION.RAZAO_SOCIAL_MAX)]],
      inscricaoEstadual: ['', [Validators.maxLength(VALIDATION.INSCRICAO_ESTADUAL_MAX)]],
      email: [this.email, [Validators.email, Validators.maxLength(VALIDATION.EMAIL_MAX)]],
    });

    this.sub.add(
      this.form.valueChanges
        .pipe(debounceTime(200), distinctUntilChanged())
        .subscribe((v) => this.valueChange.emit(v))
    );
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  onSave(): void {
    if (this.form.valid) {
      this.save.emit();
    }
  }
}
