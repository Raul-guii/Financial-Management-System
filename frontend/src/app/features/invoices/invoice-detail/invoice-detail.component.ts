import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { InvoiceService } from '../../../core/services/invoice.service';
import { InvoiceLineService } from '../../../core/services/invoice-line.service';
import { InvoiceLine } from '../../../models/invoices/invoice-line.model';
import { Invoice } from '../../../models/invoices/invoice.model';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-invoice-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './invoice-detail.component.html',
  styleUrls: ['./invoice-detail.component.scss']
})
export class InvoiceDetailComponent implements OnInit {

  invoice?: Invoice;
  lines: InvoiceLine[] = [];

  constructor(
    private route: ActivatedRoute,
    private invoiceService: InvoiceService,
    private lineService: InvoiceLineService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.invoiceService.findById(id).subscribe(inv => {
      this.invoice = inv;
    });

    console.log('ID DA ROTA:', id); 

    this.invoiceService.findById(id).subscribe(inv => {
      console.log('INVOICE CHEGOU:', inv); 
      this.invoice = inv;

      this.cdr.detectChanges();
    });

    this.lineService.findByInvoice(id).subscribe(lines => {
      this.lines = lines;
    });

    this.lineService.findByInvoice(id).subscribe(lines => {
      console.log('LINES:', lines); 
      this.lines = lines;
    });
  }

  isOverdue(): boolean {
    if (!this.invoice) return false;
    return new Date() > new Date(this.invoice.dueDay);
  }

  getDaysLate(): number {
    if (!this.invoice) return 0;

    const today = new Date();
    const due = new Date(this.invoice.dueDay);

    if (today <= due) return 0;

    const diff = today.getTime() - due.getTime();
    return Math.floor(diff / (1000 * 60 * 60 * 24));
  }

  markAsPaid(): void {
    if (!this.invoice) return;

    this.invoiceService.pay(this.invoice.id).subscribe(inv => {
      this.invoice = inv;
    });
  }

  refund(): void {
    if (!this.invoice) return;

    this.invoiceService.refund(this.invoice.id).subscribe(inv => {
      this.invoice = inv;
    });
  }
}