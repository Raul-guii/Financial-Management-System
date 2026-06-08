import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReconciliationService } from '../../../core/services/reconciliation.service';
import { ReconciliationResponse } from '../../../models/reconciliations/reconciliation-response.model';
import { ReconciliationItemResponse } from '../../../models/reconciliations/reconciliation-item-response.model';

@Component({
  selector: 'app-reconciliation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reconciliation.component.html',
  styleUrls: ['./reconciliation.component.scss']
})
export class ReconciliationComponent implements OnInit {
  form!: FormGroup;
  result?: ReconciliationResponse;
  loading = false;
  exporting = false;
  errorMessage = '';
  statusFilter = '';

  constructor(
    private fb: FormBuilder,
    private reconciliationService: ReconciliationService
  ) {}

  ngOnInit(): void {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1)
      .toISOString().split('T')[0];
    const todayStr = today.toISOString().split('T')[0];

    this.form = this.fb.group({
      periodStart: [firstDay, Validators.required],
      periodEnd:   [todayStr, Validators.required]
    });
  }

  isInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && ctrl?.touched);
  }

  execute(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    this.loading = true;
    this.errorMessage = '';
    this.result = undefined;

    const value = this.form.value;

    this.reconciliationService.execute({
      periodStart: value.periodStart,
      periodEnd:   value.periodEnd,
      executedAt:  new Date().toISOString()
    }).subscribe({
      next: (data) => {
        this.result  = data;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        const msg = err?.error?.message || err?.error || '';
        this.errorMessage = msg || 'Erro ao executar reconciliação.';
      }
    });
  }

  exportCsv(): void {
    this.exporting = true;
    this.reconciliationService.exportCsv().subscribe({
      next: (blob) => {
        const url  = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href  = url;
        link.download = `reconciliation_${new Date().toISOString().split('T')[0]}.csv`;
        link.click();
        window.URL.revokeObjectURL(url);
        this.exporting = false;
      },
      error: () => {
        this.exporting = false;
        this.errorMessage = 'Erro ao exportar CSV.';
      }
    });
  }

  getFilteredItems(): ReconciliationItemResponse[] {
    if (!this.result) return [];
    if (!this.statusFilter) return this.result.items;
    return this.result.items.filter(i => i.status === this.statusFilter);
  }

  getDivergenceCount(): number {
    return this.result?.items.filter(i => i.status === 'DIVERGENT').length ?? 0;
  }

  getMatchedCount(): number {
    return this.result?.items.filter(i => i.status === 'MATCHED').length ?? 0;
  }

  getDiff(item: ReconciliationItemResponse): number {
    return item.systemAmount - item.gatewayAmount;
  }

  getRefundedCount(): number {
    return this.result?.items.filter(i => i.status === 'REFUNDED').length ?? 0;
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      MATCHED:            'Confirmado',
      DIVERGENT:          'Divergente',
      PENDING:            'Pendente',
      MISSING_IN_GATEWAY: 'Sem gateway',
      REFUNDED:           'Reembolsado',
    };
    return map[status] ?? status;
  }

  getStatusBadgeClass(status: string): string {
    const map: Record<string, string> = {
      MATCHED:            'badge-matched',
      DIVERGENT:          'badge-divergent',
      PENDING:            'badge-pending',
      MISSING_IN_GATEWAY: 'badge-warn',
      REFUNDED:           'badge-refunded',
    };
    return map[status] ?? 'badge-unknown';
  }
  
}