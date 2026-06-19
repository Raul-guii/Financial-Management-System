import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { InvoiceService } from '../../../core/services/invoice.service';
import { InvoiceResponse } from '../../../models/invoices/invoice-response.model';
import { InvoiceStatus } from '../../../models/invoices/invoice-status.enum';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-invoice-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.scss']
})
export class InvoiceListComponent implements OnInit {
  invoices: InvoiceResponse[] = [];
  invoiceToDelete: InvoiceResponse | null = null;
  InvoiceStatus = InvoiceStatus;
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;
  searchTerm = '';
  statusFilter: string = '';
  amountFilter: string = '';  
  contractFilter: string = '';
  private searchSubject = new Subject<string>();

  constructor(
    private invoiceService: InvoiceService
  ) {}

  ngOnInit(): void {
    this.loadInvoices();
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(term => {
      this.searchTerm = term;
      this.currentPage = 0;
      this.loadInvoices();
    });
  }

  onSearch(term: string): void {
    this.searchSubject.next(term);
  }

  loadInvoices(): void {
    this.invoiceService.getAll(this.currentPage, this.pageSize, this.searchTerm).subscribe({
      next: (data) => {
        this.invoices = data.content;
        this.totalElements = data.totalElements;
        this.totalPages = data.totalPages;
      },
      error: (err) => console.error('Error loading invoices:', err)
    });
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadInvoices();
  }

  getFilteredInvoices(): InvoiceResponse[] {
    return this.invoices.filter(inv => {
      const matchStatus   = !this.statusFilter   || inv.status === this.statusFilter;
      const matchAmount   = this.matchesAmountFilter(inv.amount);
      const matchContract = !this.contractFilter || String(inv.contractId) === this.contractFilter;
      return matchStatus && matchAmount && matchContract;
    });
  }

  getUniqueContractIds(): number[] {
    return [...new Set(this.invoices.map(i => i.contractId))].sort((a, b) => a - b);
  }

  matchesAmountFilter(amount: number): boolean {
    switch (this.amountFilter) {
      case 'under100':    return amount < 100;
      case '100to500':    return amount >= 100 && amount < 500;
      case '500to1000':   return amount >= 500 && amount < 1000;
      case 'above1000':   return amount >= 1000;
      default:            return true;
    }
  }

  countByStatus(status: string): number {
    return this.invoices.filter(i => i.status === status).length;
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'Pending', PAID: 'Paid',
      OVERDUE: 'Overdue', CANCELLED: 'Cancelled',
      REFUNDED: 'Refunded', PARTIALLY_PAID: 'Partial'
    };
    return map[status] ?? status;
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
      },
      error: (err) => console.error('Error removing invoice:', err)
    });
  }
}
