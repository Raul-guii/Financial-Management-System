import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { ContractItemService } from '../../../core/services/contract-item.service';
import { ContractItemResponse } from '../../../models/contract-items/contract-item-response.model';

@Component({
  selector: 'app-contract-items',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './contract-item.component.html',
  styleUrls: ['./contract-item.component.scss']
})
export class ContractItemsComponent implements OnInit {
  form!: FormGroup;
  contractId!: number;
  clientMap: Record<number, string> = {}; 
  items: ContractItemResponse[] = [];
  editingItem: ContractItemResponse | null = null;
  itemToDelete: ContractItemResponse | null = null;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private itemService: ContractItemService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.contractId = Number(this.route.snapshot.paramMap.get('id'));

    this.form = this.fb.group({
      name:        ['', Validators.required],
      description: [''],
      quantity:    [1, [Validators.required, Validators.min(1)]],
      unitPrice:   [null, [Validators.required, Validators.min(0)]],
      active:      [true]
    });

    this.loadItems();
  }

  loadItems(): void {
    this.itemService.getByContractId(this.contractId).subscribe({
      next: (data) => {
        this.items = data;
      },
      error: (err) => console.error('Erro ao carregar itens:', err)
    });
  }

  isInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && ctrl?.touched);
  }

  getTotal(): number {
    return this.items.reduce((acc, item) => acc + item.quantity * item.unitPrice, 0);
  }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    this.loading = true;
    this.errorMessage = '';

    const value = this.form.value;

    if (this.editingItem) {
      // EDITAR item existente
      this.itemService.update(this.editingItem.id, value).subscribe({
        next: (updated) => {
          const idx = this.items.findIndex(i => i.id === this.editingItem!.id);
          if (idx !== -1) this.items[idx] = updated;
          this.resetForm();
        },
        error: () => {
          this.loading = false;
          this.errorMessage = 'Erro ao atualizar item.';
        }
      });
    } else {
      // CRIAR novo item
      const payload = { ...value, contractId: this.contractId };
      this.itemService.create(payload).subscribe({
        next: (created) => {
          this.items = [...this.items, created];
          this.resetForm();
        },
        error: () => {
          this.loading = false;
          this.errorMessage = 'Erro ao adicionar item.';
        }
      });
    }
  }

  editItem(item: ContractItemResponse): void {
    this.editingItem = item;
    this.form.patchValue({
      name:        item.name,
      description: item.description ?? '',
      quantity:    item.quantity,
      unitPrice:   item.unitPrice,
      active:      item.active
    });
  }

  cancelEdit(): void {
    this.editingItem = null;
    this.resetForm();
  }

  resetForm(): void {
    this.loading = false;
    this.editingItem = null;
    this.form.reset({ quantity: 1, active: true });
  }

  openDeleteConfirm(item: ContractItemResponse): void {
    this.itemToDelete = item;
  }

  cancelDelete(): void {
    this.itemToDelete = null;
  }

  confirmDelete(): void {
    if (!this.itemToDelete) return;
    this.itemService.delete(this.itemToDelete.id).subscribe({
      next: () => {
        this.items = this.items.filter(i => i.id !== this.itemToDelete!.id);
        this.itemToDelete = null;
      },
      error: (err) => console.error('Erro ao remover item:', err)
    });
  }

  finish(): void {
    this.router.navigate(['/contracts']);
  }
}