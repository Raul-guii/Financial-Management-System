import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService } from '../../../core/services/client.service';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './client-form.component.html',
  styleUrls: ['./client-form.component.scss']
})
export class ClientFormComponent implements OnInit {
  form!: FormGroup;
  id?: number;

  constructor(
    private fb: FormBuilder,
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      type: ['PERSON', Validators.required],
      document: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      addressStreet: [''],
      addressNumber: [''],
      addressNeighborhood: [''],
      addressCity: [''],
      addressState: [''],
      addressPostalCode: [''],
      addressCountry: ['']
    });

    const paramId = this.route.snapshot.paramMap.get('id');
    this.id = paramId ? Number(paramId) : undefined;

    if (this.id) {
      this.clientService.getById(this.id).subscribe(client => {
        this.form.patchValue(client);
      });
    }
  }

  submit(): void {
    if (this.form.invalid) return;

    const payload: any = { ...this.form.value };

    if (this.id) {
      this.clientService.update(this.id, payload).subscribe(() => {
        this.router.navigate(['/clients']);
      });
    } else {
      this.clientService.create(payload).subscribe(() => {
        this.router.navigate(['/clients']);
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/clients']);
  }
}