import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { InvoiceService } from '../../../core/services/invoice.service';
import { InvoiceResponse } from '../../../models/invoices/invoice-response.model';
import { InvoiceStatus } from '../../../models/invoices/invoice-status.enum';

@Component({
  selector: 'app-invoice-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.scss']
})
export class InvoiceListComponent implements OnInit {
  invoices: InvoiceResponse[] = [];
  invoiceToDelete: InvoiceResponse | null = null;
  InvoiceStatus = InvoiceStatus;

  constructor(
    private invoiceService: InvoiceService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadInvoices();
  }

  loadInvoices(): void {
    this.invoiceService.getAll().subscribe({
      next: (data) => {
        this.invoices = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erro ao carregar faturas:', err)
    });
  }

  getStatusBadgeClass(status: InvoiceStatus): string {
    const map: Record<InvoiceStatus, string> = {
      [InvoiceStatus.PENDING]:       'badge-pending',
      [InvoiceStatus.PAID]:          'badge-paid',
      [InvoiceStatus.OVERDUE]:       'badge-overdue',
      [InvoiceStatus.CANCELLED]:     'badge-cancelled',
      [InvoiceStatus.REFUNDED]:      'badge-refunded',
      [InvoiceStatus.PARTIALLY_PAID]:'badge-partially',
    };
    return map[status] ?? '';
  }

  getStatusLabel(status: InvoiceStatus): string {
    const map: Record<InvoiceStatus, string> = {
      [InvoiceStatus.PENDING]:       'Pendente',
      [InvoiceStatus.PAID]:          'Paga',
      [InvoiceStatus.OVERDUE]:       'Vencida',
      [InvoiceStatus.CANCELLED]:     'Cancelada',
      [InvoiceStatus.REFUNDED]:      'Reembolsada',
      [InvoiceStatus.PARTIALLY_PAID]:'Parcialmente paga',
    };
    return map[status] ?? status;
  }

  openDeleteConfirm(invoice: InvoiceResponse): void {
    this.invoiceToDelete = invoice;
  }

  cancelDelete(): void {
    this.invoiceToDelete = null;
  }

  confirmDelete(): void {
    if (!this.invoiceToDelete) return;
    this.invoiceService.delete(this.invoiceToDelete.id).subscribe({
      next: () => {
        this.invoices = this.invoices.filter(i => i.id !== this.invoiceToDelete!.id);
        this.invoiceToDelete = null;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erro ao remover fatura:', err)
    });
  }
}