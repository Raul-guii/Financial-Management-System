import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { InvoiceService } from '../../../core/services/invoice.service';
import { InvoiceLineService } from '../../../core/services/invoice-line.service';
import { PaymentService } from '../../../core/services/payment.service';
import { InvoiceResponse } from '../../../models/invoices/invoice-response.model';
import { PaymentResponse } from '../../../models/payments/payment-response.model';
import { InvoiceStatus } from '../../../models/invoices/invoice-status.enum';
import { PaymentStatus } from '../../../models/payments/payment-status.model.enum';
import { PaymentMethod } from '../../../models/payments/payment-method.model.enum';
import { InvoiceLineResponse } from '../../../models/invoice-line/invoice-line-response.model';
import { AuthService } from '../../../core/services/auth.service';
import { RefundRequestService } from '../../../core/services/refund-request.service';

@Component({
  selector: 'app-invoice-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './invoice-detail.component.html',
  styleUrls: ['./invoice-detail.component.scss']
})
export class InvoiceDetailComponent implements OnInit {
  invoiceId!: number;
  invoice?: InvoiceResponse;
  lines: InvoiceLineResponse[] = [];
  payments: PaymentResponse[] = [];

  simulating = false;
  copied = false;
  showSimulation = true;
  toastMessage = '';
  toastType = 'toast-success';

  showPaymentModal = false;
  paymentForm!: FormGroup;
  paymentLoading = false;
  paymentError = '';

  showRefundModal = false;
  refundForm!: FormGroup;
  refundLoading = false;
  refundError = '';

  InvoiceStatus = InvoiceStatus;
  PaymentStatus = PaymentStatus;

  constructor(
    private invoiceService: InvoiceService,
    private invoiceLineService: InvoiceLineService,
    private paymentService: PaymentService,
    private authService: AuthService,
    private refundRequestService: RefundRequestService,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.invoiceId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadData();
  }

  loadData(): void {
    forkJoin({
      invoice:  this.invoiceService.getById(this.invoiceId),
      lines:    this.invoiceLineService.getByInvoiceId(this.invoiceId),
      payments: this.paymentService.getByInvoiceId(this.invoiceId)
    }).subscribe({
      next: ({ invoice, lines, payments }) => {
        this.invoice  = invoice;
        this.lines    = lines;
        this.payments = payments;
      },
      error: (err) => console.error('Erro ao carregar detalhe da fatura:', err)
    });
  }

  getTotal(): number {
    return this.lines.reduce((acc, l) => acc + l.lineTotal, 0);
  }

  openPaymentModal(): void {
    this.paymentError = '';
    this.paymentForm = this.fb.group({
      amount:              [this.invoice?.remainingAmount ?? 0, [Validators.required, Validators.min(0.01)]],
      payerEmail:          ['', [Validators.required, Validators.email]],
      paymentDate:         [this.getTodayStr(), Validators.required],
      date_of_expiration:  ['']
    });
    this.showPaymentModal = true;
  }

  closePaymentModal(): void {
    this.showPaymentModal = false;
    this.paymentError = '';
  }

  isPaymentInvalid(field: string): boolean {
    const ctrl = this.paymentForm?.get(field);
    return !!(ctrl?.invalid && ctrl?.touched);
  }

  submitPayment(): void {
    
    this.paymentForm.markAllAsTouched();
    if (this.paymentForm.invalid) return;

    this.paymentLoading = true;
    this.paymentError = '';

    const value = this.paymentForm.value;

    const paymentDateTime = value.paymentDate + 'T00:00:00';

    const payload: any = {
      amount:         value.amount,
      payerEmail:     value.payerEmail,
      paymentDate:    paymentDateTime, 
      paymentMethod:  PaymentMethod.PIX,
      invoiceId:      this.invoiceId,
      payerFirstName: 'Pagador',
      payerLastName:  'SGF',
      payerDocument:  '00000000000'
    };

    if (value.date_of_expiration) {
        payload.date_of_expiration = value.date_of_expiration + ':00Z';
    }

    this.paymentService.create(payload).subscribe({
      next: () => {
        this.paymentLoading = false;
        this.showPaymentModal = false;
        this.showToast('Pagamento gerado! QR Code PIX disponível.', 'toast-success');
        setTimeout(() => this.loadData(), 500);
      },
      error: (err) => {
        this.paymentLoading = false;
        const msg = err?.error?.message || err?.error || '';
        this.paymentError = msg || 'Erro ao gerar pagamento. Tente novamente.';
      }
    });
  }

  simulateApproval(paymentId: number): void {
    this.simulating = true;
    this.paymentService.simulateApproval(paymentId).subscribe({
      next: () => {
        this.simulating = false;
        this.showToast('Pagamento aprovado com sucesso!', 'toast-success');
        setTimeout(() => this.loadData(), 1000);
      },
      error: () => {
        this.simulating = false;
        this.showToast('Erro ao simular aprovação.', 'toast-error');
      }
    });
  }

  simulateRefund(paymentId: number): void {
    this.simulating = true;
    this.paymentService.simulateRefund(paymentId).subscribe({
      next: () => {
        this.simulating = false;
        this.showToast('Reembolso simulado com sucesso!', 'toast-success');
        setTimeout(() => this.loadData(), 1000);
      },
      error: () => {
        this.simulating = false;
        this.showToast('Erro ao simular reembolso.', 'toast-error');
      }
    });
  }

  copyQrCode(code: string): void {
    navigator.clipboard.writeText(code).then(() => {
      this.copied = true;
      setTimeout(() => { this.copied = false; }, 2000);
    });
  }

  showToast(message: string, type: string): void {
    this.toastMessage = message;
    this.toastType = type;
    setTimeout(() => {
       this.toastMessage = '';
    }, 3000);
  }

  getTodayStr(): string {
    return new Date().toISOString().split('T')[0];
  }

  getStatusBadgeClass(status: InvoiceStatus): string {
    const map: Record<InvoiceStatus, string> = {
      [InvoiceStatus.PENDING]:        'badge-pending',
      [InvoiceStatus.PAID]:           'badge-paid',
      [InvoiceStatus.OVERDUE]:        'badge-overdue',
      [InvoiceStatus.CANCELLED]:      'badge-cancelled',
      [InvoiceStatus.REFUNDED]:       'badge-refunded',
      [InvoiceStatus.PARTIALLY_PAID]: 'badge-partially',
    };
    return map[status] ?? '';
  }

  getStatusLabel(status: InvoiceStatus): string {
    const map: Record<InvoiceStatus, string> = {
      [InvoiceStatus.PENDING]:        'Pendente',
      [InvoiceStatus.PAID]:           'Paga',
      [InvoiceStatus.OVERDUE]:        'Vencida',
      [InvoiceStatus.CANCELLED]:      'Cancelada',
      [InvoiceStatus.REFUNDED]:       'Reembolsada',
      [InvoiceStatus.PARTIALLY_PAID]: 'Parcialmente paga',
    };
    return map[status] ?? status;
  }

  getPaymentStatusBadgeClass(status: PaymentStatus): string {
    const map: Record<PaymentStatus, string> = {
      [PaymentStatus.PENDING]:  'badge-pending',
      [PaymentStatus.APPROVED]: 'badge-approved',
      [PaymentStatus.REJECTED]: 'badge-rejected',
      [PaymentStatus.REFUNDED]: 'badge-refunded',
      [PaymentStatus.CANCELLED]: 'badge-cancelled',
      [PaymentStatus.ERROR]:    'badge-error',
    };
    return map[status] ?? '';
  }

  getPaymentStatusLabel(status: PaymentStatus): string {
    const map: Record<PaymentStatus, string> = {
      [PaymentStatus.PENDING]:  'Pendente',
      [PaymentStatus.APPROVED]: 'Aprovado',
      [PaymentStatus.REJECTED]: 'Rejeitado',
      [PaymentStatus.REFUNDED]: 'Reembolsado',
      [PaymentStatus.CANCELLED]: 'badge-cancelled',
      [PaymentStatus.ERROR]:    'Erro',
    };
    return map[status] ?? status;
  }

  get canApproveRefund(): boolean {
    const role = this.authService.getUser()?.role ?? '';
    return ['ADMIN', 'FINANCIAL_MANAGER'].includes(role);
  }

  get canPay(): boolean {
    return !!(
      this.invoice &&
      this.invoice.remainingAmount > 0 &&
      this.invoice.status !== InvoiceStatus.CANCELLED &&
      this.invoice.status !== InvoiceStatus.PAID
    );
  }

  openRefundModal(paymentId: number): void {
    this.refundError = '';
    this.refundForm = this.fb.group({
      paymentId: [paymentId],
      reason: ['', [Validators.required, Validators.minLength(10)]]
    });
    this.showRefundModal = true;
  }

  closeRefundModal(): void {
    this.showRefundModal = false;
    this.refundError = '';
  }

  isRefundInvalid(field: string): boolean {
    const ctrl = this.refundForm?.get(field);
    return !!(ctrl?.invalid && ctrl?.touched);
  }

  submitRefund(): void {
    this.refundForm.markAllAsTouched();
    if (this.refundForm.invalid) return;

    this.refundLoading = true;
    this.refundError = '';

    this.refundRequestService.create(this.refundForm.value).subscribe({
      next: () => {
        this.refundLoading = false;
        this.showRefundModal = false;
        this.showToast('Solicitação de reembolso enviada!', 'toast-success');
      },
      error: (err) => {
        this.refundLoading = false;
        this.refundError = err?.error?.message || 'Erro ao solicitar reembolso.';
      }
    });
  }

  get canRequestRefund(): boolean {
    return !!(this.invoice &&
      this.invoice.status !== InvoiceStatus.CANCELLED);
  }
}