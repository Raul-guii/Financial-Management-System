import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { Role } from '../../../models/users/role.enum';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss']
})
export class UserFormComponent implements OnInit {
  form!: FormGroup;
  isEditing = false;
  userId?: number;
  loading = false;
  errorMessage = '';
  showPassword = false;
  roleDescription = '';

  Role = Role; // Exposes the enum to the template.

  private roleDescriptions: Record<Role, string> = {
    [Role.ADMIN]: 'Full system access. Can manage users, contracts, invoices, and settings.',
    [Role.FINANCIAL_MANAGER]: 'Can create and edit contracts and clients, approve refunds, and view reports.',
    [Role.FINANCIAL_ANALYST]: 'Can view invoices, record payments, and request refunds. No access to users.'
  };

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const paramId = this.route.snapshot.paramMap.get('id');
    this.isEditing = !!paramId;
    this.userId = paramId ? Number(paramId) : undefined;

    this.form = this.fb.group({
      name:     ['', Validators.required],
      email:    ['', [Validators.required, Validators.email]],
      password: ['', this.isEditing ? [] : [Validators.required, Validators.minLength(8)]],
      role:     ['', Validators.required]
    });

    if (this.isEditing && this.userId) {
      this.userService.getById(this.userId).subscribe({
        next: (user) => {
          this.form.patchValue({
            name: user.name,
            email: user.email,
            role: user.role
          });
          this.onRoleChange();
        },
        error: () => this.errorMessage = 'Error loading user'
      });
    }
  }

  onRoleChange(): void {
    const role = this.form.get('role')?.value as Role;
    this.roleDescription = this.roleDescriptions[role] ?? '';
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

    if (this.isEditing && this.userId) {
      const payload: any = {
        name:  value.name,
        email: value.email,
        role:  value.role
      };
      // Includes password only when the user entered one.
      if (value.password) payload.password = value.password;

      this.userService.update(this.userId, payload).subscribe({
        next: () => this.router.navigate(['/users']),
        error: () => {
          this.loading = false;
          this.errorMessage = 'Error updating user. Check the data and try again.';
        }
      });

    } else {
      this.userService.create(value).subscribe({
        next: () => this.router.navigate(['/users']),
        error: () => {
          this.loading = false;
          this.errorMessage = 'Error creating user. The e-mail may already be in use.';
        }
      });
    }
  }
}
