import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ContractItemService } from '../../../core/services/contract-item.service';
import { ContractItem } from '../../../models/contract-item.model';

@Component({
  selector: 'app-contract-items-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './contract-items-list.component.html',
  styleUrls: ['./contract-items-list.component.scss']
})
export class ContractItemsListComponent implements OnInit {
  contractId!: number;
  items: ContractItem[] = [];

  constructor(
    private route: ActivatedRoute,
    private itemService: ContractItemService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.contractId = Number(params.get('id'));
      this.loadItems();
    });
  }

  loadItems(): void {
    console.log('Carregando itens do contrato:', this.contractId);

    this.itemService.getByContractId(this.contractId).subscribe({
      next: (data) => {
        console.log('Itens recebidos:', data);
        this.items = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao buscar itens:', err);
      }
    });
  }

  deactivate(id: number): void {
    this.itemService.deactivate(id).subscribe(() => {
      this.loadItems();
    });
  }
}