import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { ContractService } from '../../../core/services/contract.service';
import { ClientService } from '../../../core/services/client.service';
import { ContractStatus } from '../../../models/contracts/contract-status.enum';
import { ClientResponse } from '../../../models/clients/client-reponse.model';

@Component({
  selector: 'app-contract-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './contract-form.component.html',
  styleUrls: ['./contract-form.component.scss']
})
export class ContractFormComponent implements OnInit {
  form!: FormGroup;
  isEditing = false;
  contractId?: number;
  loading = false;
  errorMessage = '';
  clients: ClientResponse[] = [];
  currentStatus?: ContractStatus;

  constructor(
    private fb: FormBuilder,
    private contractService: ContractService,
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const paramId = this.route.snapshot.paramMap.get('id');
    this.isEditing = !!paramId;
    this.contractId = paramId ? Number(paramId) : undefined;

    this.form = this.fb.group({
      clientId:      [null, this.isEditing ? [] : Validators.required],
      billingPeriod: ['', Validators.required],
      startDate:     ['', Validators.required],
      endDate:       ['', Validators.required],
    });

    if (!this.isEditing) {
      this.clientService.getAll(0, 1000).subscribe({
        next: (data) => {
          this.clients = data.content;
        },
        error: (err) => console.error('Error loading clients:', err)
      });
    }

    if (this.isEditing && this.contractId) {
      this.contractService.getById(this.contractId).subscribe({
        next: (contract) => {
          this.currentStatus = contract.status;
          this.form.patchValue({
            billingPeriod: contract.billingPeriod,
            startDate:     contract.startDate,
            endDate:       contract.endDate,
          });
        },
        error: () => this.errorMessage = 'Error loading contract'
      });
    }
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

    if (this.isEditing && this.contractId) {
      const payload = {
        billingPeriod: value.billingPeriod,
        status:        this.currentStatus, 
        startDate:     value.startDate,
        endDate:       value.endDate,
      };

      this.contractService.update(this.contractId, payload).subscribe({
        next: () => this.router.navigate(['/contracts']),
        error: () => {
          this.loading = false;
          this.errorMessage = 'Error updating contract.';
        }
      });

    } else {
      const payload = {
        clientId:      Number(value.clientId),
        billingPeriod: value.billingPeriod,
        status:        ContractStatus.ACTIVE,
        startDate:     value.startDate,
        endDate:       value.endDate,
      };

      this.contractService.create(payload).subscribe({
        next: (contract) => this.router.navigate(['/contracts', contract.id, 'items']),
        error: () => {
          this.loading = false;
          this.errorMessage = 'Error creating contract.';
        }
      });
    }
  }
}
