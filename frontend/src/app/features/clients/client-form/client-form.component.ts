import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../../../core/services/client.service';
import { ClientType } from '../../../models/clients/client-type.enum';
import { CpfCnpjMaskDirective } from '../../../shared/directives/cpf-cnpj-mask.directive';
import { PhoneMaskDirective } from '../../../shared/directives/phone-mask.directive';
import { CepMaskDirective } from '../../../shared/directives/cep-mask.directive';
import { ClientCreateRequest } from '../../../models/clients/client-create.model';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    CpfCnpjMaskDirective,
    PhoneMaskDirective,
    CepMaskDirective,
  ],
  templateUrl: './client-form.component.html',
  styleUrls: ['./client-form.component.scss']
})
export class ClientFormComponent implements OnInit {
  form!: FormGroup;
  isEditing = false;
  clientId?: number;
  loading = false;
  errorMessage = '';

  ClientType = ClientType;

  constructor(
    private fb: FormBuilder,
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const paramId = this.route.snapshot.paramMap.get('id');
    this.isEditing = !!paramId;
    this.clientId = paramId ? Number(paramId) : undefined;

    this.form = this.fb.group({
      name:                ['', Validators.required],
      type:                ['', Validators.required],
      document:            ['', Validators.required],
      email:               ['', [Validators.required, Validators.email]],
      phone:               ['', Validators.required],
      addressStreet:       [''],
      addressNumber:       [''],
      addressNeighborhood: [''],
      addressCity:         [''],
      addressState:        [''],
      addressPostalCode:   [''],
      addressCountry:      ['Brasil'],
    });

    if (this.isEditing && this.clientId) {
      this.clientService.getById(this.clientId).subscribe({
        next: (client) => this.form.patchValue(client),
        error: () => this.errorMessage = 'Erro ao carregar cliente'
      });
    }
  }

  isInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && ctrl?.touched);
  }

  private stripMasks(value: ClientCreateRequest): ClientCreateRequest {
    return {
      ...value,
      document:          String(value['document']          ?? '').replace(/\D/g, ''),
      phone:             String(value['phone']             ?? '').replace(/\D/g, ''),
      addressPostalCode: String(value['addressPostalCode'] ?? '').replace(/\D/g, ''),
    };
  }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    this.loading = true;
    this.errorMessage = '';

    const value = this.stripMasks(this.form.value);

    if (this.isEditing && this.clientId) {
      this.clientService.update(this.clientId, value).subscribe({
        next: () => this.router.navigate(['/clients']),
        error: () => {
          this.loading = false;
          this.errorMessage = 'Erro ao atualizar cliente. Verifique os dados e tente novamente.';
        }
      });
    } else {
      this.clientService.create(value).subscribe({
        next: () => this.router.navigate(['/clients']),
        error: () => {
          this.loading = false;
          this.errorMessage = 'Erro ao cadastrar cliente. O documento ou e-mail pode já estar em uso.';
        }
      });
    }
  }
}