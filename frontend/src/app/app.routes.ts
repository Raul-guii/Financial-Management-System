import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LayoutComponent } from './core/layout/layout/layout';
import { roleGuard } from './core/guards/role.guards';
import { AuditLogListComponent } from './features/audit-log-list/audit-log-list.component';

export const routes: Routes = [

  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component')
        .then(m => m.LoginComponent)
  },

  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
    
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },

      // DASHBOARD - all
      {
        path: 'dashboard',
        data: { title: 'Dashboard - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/home/dashboard/dashboard.component')
            .then(m => m.DashboardComponent)
      },

      // USERS - ADMIN only
      {
        path: 'users',
        data: { title: 'Users - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/users/user-list/user-list.component')
            .then(m => m.UserListComponent)
      },
      {
        path: 'users/new',
        data: { title: 'New User - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/users/user-form/user-form.component')
            .then(m => m.UserFormComponent)
      },
      {
        path: 'users/:id/edit',
        data: { title: 'Update User Data - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/users/user-form/user-form.component')
            .then(m => m.UserFormComponent)
      },

      // CLIENTS - all
      {
        path: 'clients',
        data: { title: 'Clients - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/clients/client-list/client-list.component')
            .then(m => m.ClientListComponent)
      },
      {
        path: 'clients/new',
        data: { title: 'New Client - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/clients/client-form/client-form.component')
            .then(m => m.ClientFormComponent)
      },
      {
        path: 'clients/:id/edit',
        data: { title: 'Update Client Data - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/clients/client-form/client-form.component')
            .then(m => m.ClientFormComponent)
      },

      // CONTRACTS - all
      {
        path: 'contracts',
        data: { title: 'Contracts - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contracts/contract-list/contract-list.component')
            .then(m => m.ContractListComponent)
      },
      {
        path: 'contracts/new',
        data: { title: 'New Contract - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contracts/contract-form/contract-form.component')
            .then(m => m.ContractFormComponent)
      },
      {
        path: 'contracts/:id/items',
        data: { title: 'Contract Items - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contract-items/contract-item/contract-item.component')
            .then(m => m.ContractItemsComponent)
      },
      {
        path: 'contracts/:id/edit',
        data: { title: 'Update Contract Data - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contracts/contract-form/contract-form.component')
            .then(m => m.ContractFormComponent)
      },

      // INVOICES - all
      {
        path: 'invoices',
        data: { title: 'Invoices - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-list/invoice-list.component')
            .then(m => m.InvoiceListComponent)
      },

      {
        path: 'invoices/:id/lines',
        data: { title: 'Invoice Lines - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-lines/invoice-lines.component')
            .then(m => m.InvoiceLinesComponent)
      },
      {
        path: 'invoices/:id/detail',
        data: { title: 'Invoice Details - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-detail/invoice-detail.component')
            .then(m => m.InvoiceDetailComponent)
      },

      // REFUNDS - ADMIN and MANAGER
      {
        path: 'refunds',
        data: { title: 'Refunds - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER'])],
        loadComponent: () =>
          import('./features/refund-list/refund-list.component')
            .then(m => m.RefundListComponent)
      },

      // FINANCIAL PARAMETERS - ADMIN and MANAGER only
      {
        path: 'financial-parameters',
        data: { title: 'Parameters - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER'])],
        loadComponent: () =>
          import('./features/finacial-parameters/financial-parameter-list/financial-parameter-list.component')
            .then(m => m.FinancialParameterListComponent)
      },
      {
        path: 'financial-parameters/new',
        data: { title: 'New Parameter - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER'])],
        loadComponent: () =>
          import('./features/finacial-parameters/financial-parameter-form/financial-parameter-form.component')
            .then(m => m.FinancialParameterFormComponent)
      },
      {
        path: 'financial-parameters/:id/edit',
        data: { title: 'Update Parameter - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER'])],
        loadComponent: () =>
          import('./features/finacial-parameters/financial-parameter-form/financial-parameter-form.component')
            .then(m => m.FinancialParameterFormComponent)
      },

      // RECONCILIATION - all
      {
        path: 'reconciliation',
        data: { title: 'Reconciliation - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/reconciliations/reconciliation/reconciliation.component')
            .then(m => m.ReconciliationComponent)
      },

      {
         path: 'audit-logs',
         data: { title: 'Audit Logs - SGF' },
         component: AuditLogListComponent,
         canActivate: [authGuard] 
      }
    ]
  },

  {
    path: '**',
    redirectTo: 'login'
  }
];
