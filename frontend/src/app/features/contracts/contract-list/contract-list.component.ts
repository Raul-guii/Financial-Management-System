import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ContractService } from '../../../core/services/contract.service';

@Component({
  selector: 'app-contract-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './contract-list.component.html',
  styleUrls: ['./contract-list.component.scss']
})
export class ContractListComponent implements OnInit {
  contracts: any[] = [];

  constructor(
    private contractService: ContractService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadContracts();
  }

  loadContracts(): void {
    this.contractService.getAll().subscribe({
      next: (data) => {
        this.contracts = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.log(err)
    });
  }

  cancel(id: number): void {
    this.contractService.delete(id).subscribe(() => {
      this.loadContracts();
    });
  }
}