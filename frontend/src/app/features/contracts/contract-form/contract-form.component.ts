import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ContractService } from '../../../core/services/contract.service';
import { ClientService } from '../../../core/services/client.service';

@Component({
  selector: 'app-contract-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './contract-form.component.html',
  styleUrls: ['./contract-form.component.scss']
})
export class ContractFormComponent implements OnInit {
  form!: FormGroup;
  id?: number;
  clients: any[] = [];

  constructor(
    private fb: FormBuilder,
    private contractService: ContractService,
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      clientId: ['', Validators.required],
      billingPeriod: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });

    this.clientService.getAll().subscribe({
      next: (data) => {
        console.log('clients:', data);
        this.clients = data;
      },
      error: (err) => console.error(err)
    });

    const paramId = this.route.snapshot.paramMap.get('id');
    this.id = paramId ? Number(paramId) : undefined;

    if (this.id) {
      this.contractService.getById(this.id).subscribe(contract => {
        this.form.patchValue({
          clientId: contract.client,
          billingPeriod: contract.billingPeriod,
          startDate: contract.startDate,
          endDate: contract.endDate
        });
      });
    }
  }

  submit(): void {
    if (this.form.invalid) return;

    const payload = this.form.value;

    if (this.id) {
      this.contractService.update(this.id, payload).subscribe(() => {
        this.router.navigate(['/contracts']);
      });
    } else {
      this.contractService.create(payload).subscribe((contract) => {
        this.router.navigate(['/contracts', contract.id, 'items']);
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/contracts']);
  }
}