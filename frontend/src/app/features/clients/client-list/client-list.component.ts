import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ClientService } from '../../../core/services/client.service';
import { ClientType } from '../../../models/clients/client-type.enum';
import { ClientResponse } from '../../../models/clients/client-reponse.model';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.scss']
})
export class ClientListComponent implements OnInit {
  clients: ClientResponse[] = [];
  clientToDelete: ClientResponse | null = null;
  filterMode: 'all' | 'defaulters' = 'all';
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;
  searchTerm = '';
  private searchSubject = new Subject<string>();

  constructor(
    private clientService: ClientService,
    private cdr: ChangeDetectorRef
  ) {}

ngOnInit(): void {
  this.loadClients();

  this.searchSubject.pipe(
    debounceTime(400),
    distinctUntilChanged()
  ).subscribe(term => {
    this.searchTerm = term;
    this.currentPage = 0;
    this.loadClients();
  });
}

onSearch(term: string): void {
  this.searchSubject.next(term);
}

loadClients(): void {
  this.clientService.getAll(this.currentPage, this.pageSize, this.searchTerm).subscribe({
    next: (data) => {
      this.clients = data.content;      
      this.totalElements = data.totalElements;
      this.totalPages = data.totalPages;
      this.cdr.detectChanges();
    },
    error: (err) => console.error('Erro ao carregar clientes:', err)
  });
}

  getInitials(name: string): string {
    return name
      .split(' ')
      .slice(0, 2)
      .map(n => n[0])
      .join('')
      .toUpperCase();
  }

  getAvatarClass(type: ClientType): string {
    return type === ClientType.COMPANY ? 'av-company' : 'av-person';
  }

  getTypeBadgeClass(type: ClientType): string {
    return type === ClientType.COMPANY ? 'badge-company' : 'badge-person';
  }

  getTypeLabel(type: ClientType): string {
    return type === ClientType.COMPANY ? 'Empresa' : 'Pessoa Física';
  }

  toggleActive(client: ClientResponse): void {
    this.clientService.deactivate(client.id).subscribe({
      next: () => {
        client.active = !client.active;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erro ao alterar status:', err)
    });
  }

  openDeleteConfirm(client: ClientResponse): void {
    this.clientToDelete = client;
  }

  cancelDelete(): void {
    this.clientToDelete = null;
  }

  confirmDelete(): void {
    if (!this.clientToDelete) return;
    this.clientService.delete(this.clientToDelete.id).subscribe({
      next: () => {
        this.clients = this.clients.filter(c => c.id !== this.clientToDelete!.id);
        this.clientToDelete = null;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erro ao deletar cliente:', err)
    });
  }

  setFilter(mode: 'all' | 'defaulters'): void {
    this.filterMode = mode;
  }

  getFilteredClients(): ClientResponse[] {
    if (this.filterMode === 'defaulters') {
      return this.clients.filter(c => c.defaulter);
    }
    return this.clients;
  }

  getDefaultersCount(): number {
    return this.clients.filter(c => c.defaulter).length;
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadClients();
  }
}