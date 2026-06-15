import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { RefundRequestResponse } from '../../models/refund-request.model';
import { RefundRequestService } from '../../core/services/refund-request.service';

@Component({
  selector: 'app-refund-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './refund-list.component.html',
  styleUrls: ['./refund-list.component.scss']
})
export class RefundListComponent implements OnInit {
  refunds: RefundRequestResponse[] = [];
  loading = false;
  actionLoadingId: number | null = null;
  toastMessage = '';
  toastType = 'toast-success';

  constructor(
    private refundService: RefundRequestService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.refundService.getAll().pipe(finalize(() => this.loading = false)).subscribe({
      next: (data) => {
        this.refunds = data;
        console.log('REFUNDS:', data); 
      },
      error: (err) => console.error(err)
    });
  }

  approve(id: number): void {
    this.actionLoadingId = id;
    this.refundService.approve(id).pipe(finalize(() => this.actionLoadingId = null)).subscribe({
      next: () => {
        this.showToast('Reembolso aprovado!', 'toast-success');
        this.load();
      },
      error: (err) => this.showToast(err?.error?.message || 'Erro ao aprovar.', 'toast-error')
    });
  }

  reject(id: number): void {
    this.actionLoadingId = id;
    this.refundService.reject(id).pipe(finalize(() => this.actionLoadingId = null)).subscribe({
      next: () => {
        this.showToast('Reembolso rejeitado.', 'toast-success');
        this.load();
      },
      error: (err) => this.showToast(err?.error?.message || 'Erro ao rejeitar.', 'toast-error')
    });
  }

  get canApprove(): boolean {
    const role = this.authService.getUser()?.role ?? '';
    console.log('ROLE:', role); 
    return ['ADMIN', 'FINANCIAL_MANAGER'].includes(role);
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'Pendente', APPROVED: 'Aprovado', REJECTED: 'Rejeitado'
    };
    return map[status] ?? status;
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'badge-pending', APPROVED: 'badge-approved', REJECTED: 'badge-danger'
    };
    return map[status] ?? '';
  }

  showToast(message: string, type: string): void {
    this.toastMessage = message;
    this.toastType = type;
    setTimeout(() => this.toastMessage = '', 3000);
  }
}