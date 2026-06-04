import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FinancialParameterService } from '../../../core/services/financial-parameter.service';
import { FinancialParameterResponse } from '../../../models/financial-parameters/financial-parameter-response.model';
import { FinancialParameterType } from '../../../models/financial-parameters/financial-parameter-type.enum.model';
import { FinancialParameterCategory } from '../../../models/financial-parameters/financial-parameter-category.enum.model';

@Component({
  selector: 'app-financial-parameter-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './financial-parameter-list.component.html',
  styleUrls: ['./financial-parameter-list.component.scss']
})
export class FinancialParameterListComponent implements OnInit {
  parameters: FinancialParameterResponse[] = [];
  paramToDeactivate: FinancialParameterResponse | null = null;

  constructor(
    private paramService: FinancialParameterService
  ) {}

  ngOnInit(): void {
    this.loadParameters();
  }

  loadParameters(): void {
    this.paramService.getAll().subscribe({
      next: (data) => {
        this.parameters = data;
      },
      error: (err) => console.error('Erro ao carregar parâmetros:', err)
    });
  }

  formatValue(param: FinancialParameterResponse): string {
    if (param.value === null || param.value === undefined) return '—';

    switch (param.category) {
      case FinancialParameterCategory.PERCENTAGE:
        return `${param.value}%`;
      case FinancialParameterCategory.MONETARY:
        return `R$ ${param.value.toFixed(2)}`;
      case FinancialParameterCategory.DAYS:
        return `${param.value} dias`;
      case FinancialParameterCategory.FLAG:
        return param.value === 1 ? 'Sim' : 'Não';
      default:
        return String(param.value);
    }
  }

  getTypeBadgeClass(type: FinancialParameterType): string {
    const map: Record<FinancialParameterType, string> = {
      [FinancialParameterType.DECIMAL]: 'badge-decimal',
      [FinancialParameterType.INTEGER]: 'badge-integer',
      [FinancialParameterType.STRING]:  'badge-string',
      [FinancialParameterType.BOOLEAN]: 'badge-boolean',
    };
    return map[type] ?? '';
  }

  getTypeLabel(type: FinancialParameterType): string {
    const map: Record<FinancialParameterType, string> = {
      [FinancialParameterType.DECIMAL]: 'Decimal',
      [FinancialParameterType.INTEGER]: 'Inteiro',
      [FinancialParameterType.STRING]:  'Texto',
      [FinancialParameterType.BOOLEAN]: 'Booleano',
    };
    return map[type] ?? type;
  }

  getCategoryBadgeClass(category?: FinancialParameterCategory): string {
    if (!category) return '';
    const map: Record<FinancialParameterCategory, string> = {
      [FinancialParameterCategory.MONETARY]:   'badge-monetary',
      [FinancialParameterCategory.PERCENTAGE]: 'badge-percentage',
      [FinancialParameterCategory.DAYS]:       'badge-days',
      [FinancialParameterCategory.FLAG]:       'badge-flag',
    };
    return map[category] ?? '';
  }

  getCategoryLabel(category?: FinancialParameterCategory): string {
    if (!category) return '';
    const map: Record<FinancialParameterCategory, string> = {
      [FinancialParameterCategory.MONETARY]:   'Monetário',
      [FinancialParameterCategory.PERCENTAGE]: 'Percentual',
      [FinancialParameterCategory.DAYS]:       'Dias',
      [FinancialParameterCategory.FLAG]:       'Flag',
    };
    return map[category] ?? category;
  }

  openDeactivateConfirm(param: FinancialParameterResponse): void {
    this.paramToDeactivate = param;
  }

  cancelDeactivate(): void {
    this.paramToDeactivate = null;
  }

  confirmDeactivate(): void {
    if (!this.paramToDeactivate) return;
    this.paramService.deactivate(this.paramToDeactivate.id).subscribe({
      next: () => {
        const p = this.parameters.find(x => x.id === this.paramToDeactivate!.id);
        if (p) p.active = false;
        this.paramToDeactivate = null;
      },
      error: (err) => console.error('Erro ao desativar parâmetro:', err)
    });
  }
}