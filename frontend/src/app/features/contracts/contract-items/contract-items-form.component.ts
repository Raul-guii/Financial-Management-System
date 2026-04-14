import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ContractItemService } from '../../../core/services/contract-item.service';

@Component({
  selector: 'app-contract-items-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './contract-items-form.component.html',
  styleUrls: ['./contract-items-form.component.scss']
})
export class ContractItemsFormComponent implements OnInit {
  form!: FormGroup;
  contractId!: number;
  items: any[] = [];
  error?: string;

  constructor(
    private fb: FormBuilder,
    private itemService: ContractItemService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      quantity: ['', Validators.required],
      unitPrice: ['', Validators.required],
      active: [true]
    });

    this.contractId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadItems();
  }

  loadItems(): void {
    this.itemService.getByContractId(this.contractId).subscribe(data => {
      this.items = data;
    });
  }

  submit(): void {
    this.saveItem();
  }

 finish(): void {
    if (this.form.valid) {
      this.saveItem(() => this.goToInvoice());
    } else {
      this.goToInvoice();
    }
  } 
  
  goToInvoice(): void {
    this.router.navigate([
      '/contracts',
      this.contractId,
      'invoice',
      'new'
    ]);
  }

  saveItem(callback?: () => void): void {
    if (this.form.invalid) return;

    const payload = {
      ...this.form.value,
      contractId: this.contractId
    };

    this.itemService.create(payload).subscribe({
      next: () => {
        this.form.reset({ active: true });
        this.loadItems();

        if (callback) callback();
      },
      error: (err) => {
        this.error = 'Erro ao salvar item';
        console.error(err);
      }
    });
  }
}