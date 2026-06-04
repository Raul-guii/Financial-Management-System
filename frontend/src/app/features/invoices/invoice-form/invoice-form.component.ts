import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { InvoiceService } from '../../../core/services/invoice.service';
import { ContractService } from '../../../core/services/contract.service';
import { ClientService } from '../../../core/services/client.service';
import { ContractResponse } from '../../../models/contracts/contract-response.model';

@Component({
  selector: 'app-invoice-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './invoice-form.component.html',
  styleUrls: ['./invoice-form.component.scss']
})
export class InvoiceFormComponent implements OnInit {
  form!: FormGroup;
  isEditing = false;
  invoiceId?: number;
  contractIdFromRoute?: number;
  loading = false;
  errorMessage = '';
  contracts: ContractResponse[] = [];
  clientMap: Record<number, string> = {};

  constructor(
    private fb: FormBuilder,
    private invoiceService: InvoiceService,
    private contractService: ContractService,
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const paramId = this.route.snapshot.paramMap.get('id');
    this.isEditing = !!paramId;
    this.invoiceId = paramId ? Number(paramId) : undefined;

    // pega contractId vindo do queryParam (fluxo contrato → itens → fatura)
    const qContractId = this.route.snapshot.queryParamMap.get('contractId');
    this.contractIdFromRoute = qContractId ? Number(qContractId) : undefined;

    this.form = this.fb.group({
      contractId: [
        this.contractIdFromRoute ?? null,
        this.isEditing || this.contractIdFromRoute ? [] : Validators.required
      ],
      issueDate: [this.getTodayStr(), Validators.required],
      dueDate:   ['']
    });

    if (!this.isEditing && !this.contractIdFromRoute) {
      // carrega contratos e clientes para o select
      forkJoin({
        contracts: this.contractService.getAll(),
        clients:   this.clientService.getAll()
      }).subscribe({
          next: ({ contracts, clients }) => {
            this.contracts = contracts.content;

            this.clientMap = clients.content.reduce(
              (acc: Record<number, string>, c) => {
                acc[c.id] = c.name;
                return acc;
              },
              {}
            );

          },
        error: (err) => console.error(err)
      });
    }

    if (this.isEditing && this.invoiceId) {
      this.invoiceService.getById(this.invoiceId).subscribe({
        next: (invoice) => {
          this.form.patchValue({
            issueDate: invoice.issueDate,
            dueDate:   invoice.dueDate ?? ''
          });
        },
        error: () => this.errorMessage = 'Erro ao carregar fatura'
      });
    }
  }

  getClientName(clientId: number): string {
    return this.clientMap[clientId] ?? `Cliente #${clientId}`;
  }

  getTodayStr(): string {
    return new Date().toISOString().split('T')[0];
  }

  isInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && ctrl?.touched);
  }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    this.loading = true;
    this.errorMessage = '';

    const value = this.form.value;

    if (this.isEditing && this.invoiceId) {
      const payload: any = {
        issueDate: value.issueDate,
      };
      if (value.dueDate) payload.dueDate = value.dueDate;

      this.invoiceService.update(this.invoiceId, payload).subscribe({
        next: () => this.router.navigate(['/invoices']),
        error: () => {
          this.loading = false;
          this.errorMessage = 'Erro ao atualizar fatura.';
        }
      });

    } else {
      const payload: any = {
        contractId: this.contractIdFromRoute ?? Number(value.contractId),
        issueDate:  value.issueDate,
      };
      if (value.dueDate) payload.dueDate = value.dueDate;

      this.invoiceService.create(payload).subscribe({
        next: (invoice) => this.router.navigate(['/invoices', invoice.id, 'lines']),
        error: () => {
          this.loading = false;
          this.errorMessage = 'Erro ao gerar fatura.';
        }
      });
    }
  }
}