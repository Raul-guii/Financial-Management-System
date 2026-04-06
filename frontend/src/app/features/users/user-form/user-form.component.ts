import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, FormGroup } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss'], 
})
export class UserFormComponent implements OnInit {

  form!: FormGroup; 
  id?: number;

  constructor(
    private fb: FormBuilder,
    private service: UserService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {

    this.form = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: [''],
      role: ['']
    });

    const paramId = this.route.snapshot.paramMap.get('id');
    this.id = paramId ? Number(paramId) : undefined;

    if (this.id) {
      this.service.getById(this.id).subscribe(user => {
        this.form.patchValue({
          name: user.name,
          email: user.email,
          role: user.role
        });
      });
    }
  }

  submit() {
  if (this.form.invalid) return;

  const payload: any = { ...this.form.value };

  if (!payload.password) delete payload.password;
  if (!payload.role) delete payload.role;

  if (this.id) {
    this.service.update(this.id, payload).subscribe(() => {
      this.router.navigate(['/users']);
    });
  } else {
    this.service.create(payload).subscribe(() => {
      this.router.navigate(['/users']);
    });
  }
}

  cancel() {
  this.router.navigate(['/users']);
}
}