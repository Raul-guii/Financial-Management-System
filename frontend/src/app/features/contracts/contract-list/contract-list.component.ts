import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { debounceTime, distinctUntilChanged, forkJoin, Subject } from 'rxjs';
import { ContractService } from '../../../core/services/contract.service';
import { ClientService } from '../../../core/services/client.service';
import { ContractResponse } from '../../../models/contracts/contract-response.model';
import { ContractStatus } from '../../../models/contracts/contract-status.enum';
import { BillingPeriod } from '../../../models/contracts/billing-period.enum';
import { ClientResponse } from '../../../models/clients/client-reponse.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contract-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
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
  statusFilter: string = '';
  billingFilter: string = '';
  private searchSubject = new Subject<string>();

  constructor(
    private contractService: ContractService,
    private clientService: ClientService,
  ) {}

 ngOnInit(): void {
    this.loadContracts();
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(term => {
      this.searchTerm = term;
      this.currentPage = 0;
      this.loadContracts();
    });
  }

  onSearch(term: string): void {
    this.searchSubject.next(term);
  }

  loadContracts(): void {
    forkJoin({
      contracts: this.contractService.getAll(
        this.currentPage,
        this.pageSize,
        this.searchTerm
      ),
      clients: this.clientService.getAll()
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
      },

      error: (err) => console.error('Erro ao carregar contratos:', err)
    });
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;

    this.currentPage = page;
    this.loadContracts();
  }

  getClientName(clientId: number): string {
    return this.clientMap[clientId] ?? `Cliente #${clientId}`;
  }

  getFilteredContracts(): ContractResponse[] {
    return this.contracts.filter(c => {
      const matchStatus = !this.statusFilter || c.status === this.statusFilter;
      const matchBilling = !this.billingFilter || c.billingPeriod === this.billingFilter;
      return matchStatus && matchBilling;
    });
  }

  countByStatus(status: string): number {
    return this.contracts.filter(c => c.status === status).length;
  }

  countByBilling(period: string): number {
    return this.contracts.filter(c => c.billingPeriod === period).length;
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      ACTIVE: 'Ativo', SUSPENDED: 'Suspenso',
      CLOSED: 'Encerrado', CANCELLED: 'Cancelado'
    };
    return map[status] ?? status;
  }

  getBillingLabel(period: string): string {
    const map: Record<string, string> = {
      MONTHLY: 'Mensal', QUARTERLY: 'Trimestral',
      SEMIANNUAL: 'Semestral', ANNUAL: 'Anual'
    };
    return map[period] ?? period;
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
      },
      error: (err) => console.error('Erro ao cancelar contrato:', err)
    });
  }
}