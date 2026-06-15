import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { Role } from '../../../models/users/role.enum';
import { UserResponse } from '../../../models/users/user-response.model';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

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
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;
  searchTerm = '';
  roleFilter: Role | '' = '';
  Role = Role
  totalAdmins = 0;
  totalManagers = 0;
  totalAnalysts = 0;
  private searchSubject = new Subject<string>();

  constructor(
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadCounts();
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(term => {
      this.searchTerm = term;
      this.currentPage = 0;
      this.loadUsers();
    });
  }

  onSearch(term: string): void {
    this.searchSubject.next(term);
  }

  setRoleFilter(role: Role | ''): void {
    this.roleFilter = role;
    this.currentPage = 0;
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAll(this.currentPage, this.pageSize, this.searchTerm, this.roleFilter).subscribe({
      next: (data) => {
        this.users = data.content;
        this.totalElements = data.totalElements;
        this.totalPages = data.totalPages;
      },
      error: (err) => console.error('Erro:', err)
    });
  }

  loadCounts(): void {
    this.userService.getAll(0, 1, '', Role.ADMIN).subscribe(d => this.totalAdmins = d.totalElements);
    this.userService.getAll(0, 1, '', Role.FINANCIAL_MANAGER).subscribe(d => this.totalManagers = d.totalElements);
    this.userService.getAll(0, 1, '', Role.FINANCIAL_ANALYST).subscribe(d => this.totalAnalysts = d.totalElements);
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadUsers();
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

  countByRole(role: Role): number {
    return this.users.filter(u => u.role === role).length;
  }

  getRoles(): Role[] {
    return Object.values(Role);
  }

  getFilteredUsers(): UserResponse[] {
    if (!this.roleFilter) return this.users;
    return this.users.filter(u => u.role === this.roleFilter);
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
      },
      error: (err) => console.error('Erro ao deletar usuário:', err)
    });
  }
}