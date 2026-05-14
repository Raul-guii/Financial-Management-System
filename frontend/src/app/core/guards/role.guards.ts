import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const user = authService.getUser();

    if (!user || !allowedRoles.includes(user.role)) {
      router.navigate(['/dashboard']);
      return false;
    }

    return true;
  };
};