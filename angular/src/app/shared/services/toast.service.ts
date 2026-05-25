import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

export interface Toast {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
}

@Injectable({providedIn: 'root'})
export class ToastService {
  private toastsSubject = new BehaviorSubject<Toast[]>([]);
  toasts$ = this.toastsSubject.asObservable();

  show(type: 'success' | 'error' | 'warning' | 'info', message: string): void {
    const id = Math.random().toString(36).slice(2);
    const toast: Toast = {id, type, message};
    this.toastsSubject.next([...this.toastsSubject.value, toast]);
    setTimeout(() => this.dismiss(id), 5000);
  }

  dismiss(id: string): void {
    this.toastsSubject.next(this.toastsSubject.value.filter(t => t.id !== id));
  }
}
