import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LayoutComponent } from './core/layout/layout/layout';
import { roleGuard } from './core/guards/role.guards';

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

      // DASHBOARD - todos
      {
        path: 'dashboard',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/home/dashboard/dashboard.component')
            .then(m => m.DashboardComponent)
      },

      // USERS - só ADMIN
      {
        path: 'users',
        canActivate: [authGuard, roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/users/user-list/user-list.component')
            .then(m => m.UserListComponent)
      },
      {
        path: 'users/new',
        canActivate: [authGuard, roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/users/user-form/user-form.component')
            .then(m => m.UserFormComponent)
      },
      {
        path: 'users/:id/edit',
        canActivate: [authGuard, roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/users/user-form/user-form.component')
            .then(m => m.UserFormComponent)
      },

      // CLIENTS - todos
      {
        path: 'clients',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/clients/client-list/client-list.component')
            .then(m => m.ClientListComponent)
      },
      {
        path: 'clients/new',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/clients/client-form/client-form.component')
            .then(m => m.ClientFormComponent)
      },
      {
        path: 'clients/:id/edit',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/clients/client-form/client-form.component')
            .then(m => m.ClientFormComponent)
      },

      // CONTRACTS - todos
      {
        path: 'contracts',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contracts/contract-list/contract-list.component')
            .then(m => m.ContractListComponent)
      },
      {
        path: 'contracts/new',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contracts/contract-form/contract-form.component')
            .then(m => m.ContractFormComponent)
      },
      {
        path: 'contracts/:id/items',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contract-items/contract-item/contract-item.component')
            .then(m => m.ContractItemsComponent)
      },
      {
        path: 'contracts/:id/edit',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contracts/contract-form/contract-form.component')
            .then(m => m.ContractFormComponent)
      },

      // INVOICES - todos
      {
        path: 'invoices',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-list/invoice-list.component')
            .then(m => m.InvoiceListComponent)
      },
      {
        path: 'invoices/new',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-form/invoice-form.component')
            .then(m => m.InvoiceFormComponent)
      },
      {
        path: 'invoices/:id/edit',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-form/invoice-form.component')
            .then(m => m.InvoiceFormComponent)
      },
      {
        path: 'invoices/:id/lines',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-lines/invoice-lines.component')
            .then(m => m.InvoiceLinesComponent)
      },
      {
        path: 'invoices/:id/detail',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-detail/invoice-detail.component')
            .then(m => m.InvoiceDetailComponent)
      },

      // FINANCIAL PARAMETERS - só ADMIN e GESTOR
      {
        path: 'financial-parameters',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER'])],
        loadComponent: () =>
          import('./features/finacial-parameters/financial-parameter-list/financial-parameter-list.component')
            .then(m => m.FinancialParameterListComponent)
      },
      {
        path: 'financial-parameters/new',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER'])],
        loadComponent: () =>
          import('./features/finacial-parameters/financial-parameter-form/financial-parameter-form.component')
            .then(m => m.FinancialParameterFormComponent)
      },
      {
        path: 'financial-parameters/:id/edit',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER'])],
        loadComponent: () =>
          import('./features/finacial-parameters/financial-parameter-form/financial-parameter-form.component')
            .then(m => m.FinancialParameterFormComponent)
      },

      // RECONCILIATION - todos
      {
        path: 'reconciliation',
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/reconciliations/reconciliation/reconciliation.component')
            .then(m => m.ReconciliationComponent)
      },
    ]
  },

  {
    path: '**',
    redirectTo: 'login'
  }
];