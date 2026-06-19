import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditLogResponse } from '../../models/audit-log/audit-log.response.model';
import { AuditLogService } from '../../core/services/audit-log.service';

@Component({
  selector: 'app-audit-log-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './audit-log-list.component.html',
  styleUrls: ['./audit-log-list.component.scss']
})
export class AuditLogListComponent implements OnInit {
  logs: AuditLogResponse[] = [];
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;
  entityTypeFilter = '';
  actionFilter = '';

  entityTypes = ['CONTRACT', 'INVOICE', 'PAYMENT', 'REFUND', 'CLIENT', 'USER'];
  actions = [
    'CONTRACT_CREATED', 'CONTRACT_UPDATED', 'CONTRACT_CANCELLED',
    'INVOICE_CREATED', 'INVOICE_CANCELLED',
    'PAYMENT_CREATED', 'PAYMENT_DELETED',
    'REFUND_REQUESTED', 'REFUND_APPROVED', 'REFUND_REJECTED',
    'CLIENT_CREATED', 'CLIENT_UPDATED', 'CLIENT_DEACTIVATED',
    'USER_CREATED', 'USER_UPDATED'
  ];

  constructor(
    private auditLogService: AuditLogService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.auditLogService.getAll(
      this.currentPage, this.pageSize,
      this.entityTypeFilter || undefined,
      this.actionFilter || undefined
    ).subscribe({
      next: (data) => {
        this.logs = data.content;
        this.totalElements = data.totalElements;
        this.totalPages = data.totalPages;
      },
      error: (err) => console.error('Error loading audit logs:', err)
    });
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.load();
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.load();
  }

  getActionLabel(action: string): string {
    const map: Record<string, string> = {
      CONTRACT_CREATED: 'Contract created',
      CONTRACT_UPDATED: 'Contract updated',
      CONTRACT_CANCELLED: 'Contract cancelled',
      INVOICE_CREATED: 'Invoice created',
      INVOICE_CANCELLED: 'Invoice cancelled',
      PAYMENT_CREATED: 'Payment created',
      PAYMENT_DELETED: 'Payment removed',
      REFUND_REQUESTED: 'Refund requested',
      REFUND_APPROVED: 'Refund approved',
      REFUND_REJECTED: 'Refund rejected',
      CLIENT_CREATED: 'Client created',
      CLIENT_UPDATED: 'Client updated',
      CLIENT_DEACTIVATED: 'Client deactivated',
      USER_CREATED: 'User created',
      USER_UPDATED: 'User updated',
    };
    return map[action] ?? action;
  }

  getEntityLabel(entity: string): string {
    const map: Record<string, string> = {
      CONTRACT: 'Contract',
      INVOICE: 'Invoice',
      PAYMENT: 'Payment',
      REFUND: 'Refund',
      CLIENT: 'Client',
      USER: 'User',
    };
    return map[entity] ?? entity;
  }

  getActionBadgeClass(action: string): string {
    if (action.includes('CREATED')) return 'badge-created';
    if (action.includes('UPDATED')) return 'badge-updated';
    if (action.includes('CANCELLED') || action.includes('DELETED') ||
        action.includes('DEACTIVATED') || action.includes('REJECTED')) return 'badge-danger';
    if (action.includes('APPROVED')) return 'badge-approved';
    if (action.includes('REQUESTED')) return 'badge-pending';
    return '';
  }
}
