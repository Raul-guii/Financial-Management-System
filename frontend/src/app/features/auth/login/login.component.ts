import { ChangeDetectorRef, Component, NgZone } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  email = '';
  password = '';
  errorMessage = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private ngZone: NgZone,
  ) {}

  login() {
    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.email, this.password).pipe(
      finalize(() => {
        this.ngZone.run(() => { 
          this.loading = false;
        });
      })
    ).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err) => {
        console.log('ERROR RECEIVED IN COMPONENT', err);
        this.loading = false;
        this.errorMessage = 'Invalid e-mail or password';
}
      
    });
  }
}
