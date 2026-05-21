import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin, Subject } from 'rxjs';
import { ContractService } from '../../../core/services/contract.service';
import { ClientService } from '../../../core/services/client.service';
import { ContractResponse } from '../../../models/contracts/contract-response.model';
import { ContractStatus } from '../../../models/contracts/contract-status.enum';
import { BillingPeriod } from '../../../models/contracts/billing-period.enum';
import { ClientResponse } from '../../../models/clients/client-reponse.model';

@Component({
  selector: 'app-contract-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './contract-list.component.html',
  styleUrls: ['./contract-list.component.scss']
})
export class ContractListComponent implements OnInit {
  contracts: ContractResponse[] = [];
  clientMap: Record<number, string> = {};
  contractToDelete: ContractResponse | null = null;
  ContractStatus = ContractStatus;
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;
  searchTerm = '';
  private searchSubject = new Subject<string>();

  constructor(
    private contractService: ContractService,
    private clientService: ClientService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    forkJoin({
      contracts: this.contractService.getAll(this.currentPage, this.pageSize),
      clients:   this.clientService.getAll()
    }).subscribe({
        next: ({ contracts, clients }) => {
          this.contracts = contracts.content;
          this.totalPages = contracts.totalPages;
          this.totalElements = contracts.totalElements;

          this.clientMap = clients.content.reduce(
            (acc: Record<number, string>, c: ClientResponse) => {
              acc[c.id] = c.name;
              return acc;
            },
            {}
          );

          this.cdr.detectChanges();
        },
      error: (err) => console.error('Erro ao carregar dados:', err)
    });
  }

  getClientName(clientId: number): string {
    return this.clientMap[clientId] ?? `Cliente #${clientId}`;
  }

  getStatusBadgeClass(status: ContractStatus): string {
    const map: Record<ContractStatus, string> = {
      [ContractStatus.ACTIVE]:    'badge-active',
      [ContractStatus.SUSPENDED]: 'badge-suspended',
      [ContractStatus.CLOSED]:    'badge-closed',
      [ContractStatus.CANCELLED]: 'badge-cancelled',
    };
    return map[status] ?? '';
  }

  getStatusLabel(status: ContractStatus): string {
    const map: Record<ContractStatus, string> = {
      [ContractStatus.ACTIVE]:    'Ativo',
      [ContractStatus.SUSPENDED]: 'Suspenso',
      [ContractStatus.CLOSED]:    'Encerrado',
      [ContractStatus.CANCELLED]: 'Cancelado',
    };
    return map[status] ?? status;
  }

  getPeriodBadgeClass(period: BillingPeriod): string {
    const map: Record<BillingPeriod, string> = {
      [BillingPeriod.MONTHLY]:    'badge-monthly',
      [BillingPeriod.QUARTERLY]:  'badge-quarterly',
      [BillingPeriod.SEMIANNUAL]: 'badge-semiannual',
      [BillingPeriod.ANNUAL]:     'badge-annual',
    };
    return map[period] ?? '';
  }

  getPeriodLabel(period: BillingPeriod): string {
    const map: Record<BillingPeriod, string> = {
      [BillingPeriod.MONTHLY]:    'Mensal',
      [BillingPeriod.QUARTERLY]:  'Trimestral',
      [BillingPeriod.SEMIANNUAL]: 'Semestral',
      [BillingPeriod.ANNUAL]:     'Anual',
    };
    return map[period] ?? period;
  }

  openDeleteConfirm(contract: ContractResponse): void {
    this.contractToDelete = contract;
  }

  cancelDelete(): void {
    this.contractToDelete = null;
  }

  confirmDelete(): void {
    if (!this.contractToDelete) return;
    this.contractService.cancel(this.contractToDelete.id).subscribe({
      next: () => {
        const c = this.contracts.find(x => x.id === this.contractToDelete!.id);
        if (c) c.status = ContractStatus.CANCELLED;
        this.contractToDelete = null;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erro ao cancelar contrato:', err)
    });
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;

    this.currentPage = page;
    this.loadData();
  }
}