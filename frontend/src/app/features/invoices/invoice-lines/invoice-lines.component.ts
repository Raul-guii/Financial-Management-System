import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';
import { InvoiceService } from '../../../core/services/invoice.service';
import { InvoiceLineService } from '../../../core/services/invoice-line.service';
import { InvoiceResponse } from '../../../models/invoices/invoice-response.model';
import { InvoiceStatus } from '../../../models/invoices/invoice-status.enum';
import { InvoiceLineResponse } from '../../../models/invoice-line/invoice-line-response.model';

@Component({
  selector: 'app-invoice-lines',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './invoice-lines.component.html',
  styleUrls: ['./invoice-lines.component.scss']
})
export class InvoiceLinesComponent implements OnInit {
  invoiceId!: number;
  invoice?: InvoiceResponse;
  lines: InvoiceLineResponse[] = [];

  constructor(
    private invoiceService: InvoiceService,
    private invoiceLineService: InvoiceLineService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.invoiceId = Number(this.route.snapshot.paramMap.get('id'));

    forkJoin({
      invoice: this.invoiceService.getById(this.invoiceId),
      lines:   this.invoiceLineService.getByInvoiceId(this.invoiceId)
    }).subscribe({
      next: ({ invoice, lines }) => {
        this.invoice = invoice;
        this.lines = lines;
      },
      error: (err) => console.error('Error loading invoice:', err)
    });
  }

  getTotal(): number {
    return this.lines.reduce((acc, l) => acc + l.lineTotal, 0);
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
      [InvoiceStatus.PENDING]:       'Pending',
      [InvoiceStatus.PAID]:          'Paid',
      [InvoiceStatus.OVERDUE]:       'Overdue',
      [InvoiceStatus.CANCELLED]:     'Cancelled',
      [InvoiceStatus.REFUNDED]:      'Refunded',
      [InvoiceStatus.PARTIALLY_PAID]:'Partially paid',
    };
    return map[status] ?? status;
  }
}
