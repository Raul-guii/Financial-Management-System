import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FinancialParameterService } from '../../../core/services/financial-parameter.service';

@Component({
  selector: 'app-financial-parameter-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './financial-parameter-form.component.html',
  styleUrls: ['./financial-parameter-form.component.scss']
})
export class FinancialParameterFormComponent implements OnInit {
  form!: FormGroup;
  isEditing = false;
  paramId?: number;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private paramService: FinancialParameterService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const paramId = this.route.snapshot.paramMap.get('id');
    this.isEditing = !!paramId;
    this.paramId = paramId ? Number(paramId) : undefined;

    this.form = this.fb.group({
      name:        ['', Validators.required],
      description: ['', Validators.required],
      type:        ['', Validators.required],
      category:    [''],
      value:       [null],
      active:      [true]
    });

    if (this.isEditing && this.paramId) {
      this.paramService.getById(this.paramId).subscribe({
        next: (param) => {
          this.form.patchValue({
            name:        param.name,
            description: param.description,
            type:        param.type,
            category:    param.category ?? '',
            value:       param.value,
            active:      param.active
          });
        },
        error: () => this.errorMessage = 'Erro ao carregar parâmetro'
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

    const payload: any = {
      name:        value.name,
      description: value.description,
      type:        value.type,
      active:      value.active
    };

    if (value.category) payload.category = value.category;
    if (value.value !== null && value.value !== '') payload.value = value.value;

    if (this.isEditing && this.paramId) {
      this.paramService.update(this.paramId, payload).subscribe({
        next: () => this.router.navigate(['/financial-parameters']),
        error: () => {
          this.loading = false;
          this.errorMessage = 'Erro ao atualizar parâmetro.';
        }
      });
    } else {
      this.paramService.create(payload).subscribe({
        next: () => this.router.navigate(['/financial-parameters']),
        error: () => {
          this.loading = false;
          this.errorMessage = 'Erro ao criar parâmetro.';
        }
      });
    }
  }
}