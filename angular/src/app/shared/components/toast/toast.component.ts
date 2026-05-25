import {Component} from '@angular/core';
import {ToastService} from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.scss'],
})
export class ToastComponent {
  toasts$ = this.toastService.toasts$;

  constructor(private toastService: ToastService) {
  }

  toastIcon(type: string): string {
    switch (type) {
      case 'success':
        return 'circle-check';
      case 'error':
        return 'circle-x';
      case 'warning':
        return 'triangle-alert';
      case 'info':
        return 'info';
      default:
        return 'bell';
    }
  }

  dismiss(id: string): void {
    this.toastService.dismiss(id);
  }
}
