import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription, debounceTime, distinctUntilChanged } from 'rxjs';
import { VALIDATION } from '../../../shared/validators/validation-constants';

@Component({
  selector: 'app-fisico-row-form',
  templateUrl: './fisico-row-form.component.html',
  styleUrls: ['./fisico-row-form.component.scss'],
})
export class FisicoRowFormComponent implements OnInit, OnDestroy {
  @Input() nome = '';
  @Input() email = '';
  @Output() save = new EventEmitter<void>();
  @Output() valueChange = new EventEmitter<{ nome: string; email: string }>();

  form!: FormGroup;
  private sub = new Subscription();

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nome: [this.nome, [Validators.required, Validators.minLength(VALIDATION.NOME_MIN), Validators.maxLength(VALIDATION.NOME_MAX)]],
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
