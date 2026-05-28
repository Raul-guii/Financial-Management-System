import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.scss']
})
export class SidebarComponent {

  isOpen = false;

  constructor(private authService: AuthService){}

  toggle() {
    this.isOpen = !this.isOpen;
  }

  close() {
    this.isOpen = false;
  }

  isAdmin(): boolean {
    return this.authService.getUser()?.role === 'ADMIN';
  }

  isAdminOrGestor(): boolean {
    const role = this.authService.getUser()?.role ?? '';
    return ['ADMIN', 'FINANCIAL_MANAGER'].includes(role);
  }
}