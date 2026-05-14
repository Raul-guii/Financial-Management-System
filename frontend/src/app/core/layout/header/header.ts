import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef, HostListener } from '@angular/core';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { NotificationResponse } from '../../../models/notifications/notification-response.model';
import { NotificationType } from '../../../models/notifications/notification-type.enum.model';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class HeaderComponent implements OnInit {
  notifications: NotificationResponse[] = [];
  showNotifications = false;
  unreadCount = 0;

  private routeTitles: Record<string, string> = {
    '/dashboard':           'Dashboard',
    '/users':               'Usuários',
    '/clients':             'Clientes',
    '/contracts':           'Contratos',
    '/invoices':            'Faturas',
    '/financial-parameters':'Parâmetros Financeiros',
    '/reconciliation':      'Reconciliação',
  };

  currentTitle = 'Dashboard';

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadNotifications();

    // atualiza título conforme a rota
    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd)
    ).subscribe((e: any) => {
      const base = '/' + e.urlAfterRedirects.split('/')[1];
      this.currentTitle = this.routeTitles[base] ?? 'SGF';
      this.cdr.detectChanges();
    });
  }

  loadNotifications(): void {
    this.notificationService.getMyNotifications().subscribe({
      next: (data) => {
        this.notifications = data;
        this.unreadCount = data.filter(n => !n.isRead).length;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erro ao carregar notificações:', err)
    });
  }

toggleNotifications(): void {
  this.showNotifications = !this.showNotifications;

  if (this.showNotifications) {
    this.loadNotifications();

    if (this.unreadCount > 0) {
      this.notificationService.markAllAsRead().subscribe({
        next: () => {
          this.notifications = this.notifications.map(n => ({ ...n, isRead: true }));
          this.unreadCount = 0;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Erro ao marcar notificações:', err)
      });
    }
  }
}

  closeNotifications(): void {
    this.showNotifications = false;
  }

  // fecha dropdown ao clicar fora
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.notif-wrap')) {
      this.showNotifications = false;
      this.cdr.detectChanges();
    }
  }

  getPageTitle(): string {
    return this.currentTitle;
  }

  getUserName(): string {
    return this.authService.getUser()?.name ?? 'Usuário';
  }

  getUserRole(): string {
    const role = this.authService.getUser()?.role ?? '';
    const map: Record<string, string> = {
      ADMIN:               'Administrador',
      FINANCIAL_MANAGER:   'Gestor',
      FINANCIAL_ANALYST:   'Analista'
    };
    return map[role] ?? role;
  }

  getUserInitial(): string {
    return (this.authService.getUser()?.name ?? 'U')[0].toUpperCase();
  }

  getNotifIcon(type: NotificationType): string {
    return type === NotificationType.INVOICE_DUE_SOON ? 'ti-clock' : 'ti-info-circle';
  }

  getNotifIconClass(type: NotificationType): string {
    return type === NotificationType.INVOICE_DUE_SOON ? 'icon-due' : 'icon-system';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  openNotification(notification: NotificationResponse): void {
    this.showNotifications = false;

    const match = notification.message.match(/#(\d+)/);
    if (match) {
      const invoiceId = match[1];
      this.router.navigate(['/invoices', invoiceId, 'detail']);
    }
  }

}