import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { Role } from '../../../models/users/role.enum';
import { UserResponse } from '../../../models/users/user-response.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {
  users: UserResponse[] = [];
  userToDelete: UserResponse | null = null;

  constructor(
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: (data) => {
        this.users = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erro ao carregar usuários:', err)
    });
  }

  getInitials(name: string): string {
    return name
      .split(' ')
      .slice(0, 2)
      .map(n => n[0])
      .join('')
      .toUpperCase();
  }

  getAvatarClass(role: Role): string {
    const map: Record<Role, string> = {
      [Role.ADMIN]: 'av-red',
      [Role.FINANCIAL_MANAGER]: 'av-purple',
      [Role.FINANCIAL_ANALYST]: 'av-teal'
    };
    return map[role] ?? 'av-teal';
  }

  getRoleBadgeClass(role: Role): string {
    const map: Record<Role, string> = {
      [Role.ADMIN]: 'badge-admin',
      [Role.FINANCIAL_MANAGER]: 'badge-gestor',
      [Role.FINANCIAL_ANALYST]: 'badge-analista'
    };
    return map[role] ?? '';
  }

  getRoleLabel(role: Role): string {
    const map: Record<Role, string> = {
      [Role.ADMIN]: 'Admin',
      [Role.FINANCIAL_MANAGER]: 'Gestor',
      [Role.FINANCIAL_ANALYST]: 'Analista'
    };
    return map[role] ?? role;
  }

  openDeleteConfirm(user: UserResponse): void {
    this.userToDelete = user;
  }

  cancelDelete(): void {
    this.userToDelete = null;
  }

  confirmDelete(): void {
    if (!this.userToDelete) return;
    this.userService.delete(this.userToDelete.id).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== this.userToDelete!.id);
        this.userToDelete = null;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erro ao deletar usuário:', err)
    });
  }
}