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
        data: { title: 'Dashboard - SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/home/dashboard/dashboard.component')
            .then(m => m.DashboardComponent)
      },

      // USERS - só ADMIN
      {
        path: 'users',
        data: { title: 'Usuários — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/users/user-list/user-list.component')
            .then(m => m.UserListComponent)
      },
      {
        path: 'users/new',
        data: { title: 'Novo Usuário — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/users/user-form/user-form.component')
            .then(m => m.UserFormComponent)
      },
      {
        path: 'users/:id/edit',
        data: { title: 'Atualizar Dados de Usuário — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/users/user-form/user-form.component')
            .then(m => m.UserFormComponent)
      },

      // CLIENTS - todos
      {
        path: 'clients',
        data: { title: 'Clientes — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/clients/client-list/client-list.component')
            .then(m => m.ClientListComponent)
      },
      {
        path: 'clients/new',
        data: { title: 'Novo Cliente — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/clients/client-form/client-form.component')
            .then(m => m.ClientFormComponent)
      },
      {
        path: 'clients/:id/edit',
        data: { title: 'Atualizar Dados de Cliente — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/clients/client-form/client-form.component')
            .then(m => m.ClientFormComponent)
      },

      // CONTRACTS - todos
      {
        path: 'contracts',
        data: { title: 'Contratos — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contracts/contract-list/contract-list.component')
            .then(m => m.ContractListComponent)
      },
      {
        path: 'contracts/new',
        data: { title: 'Novo Contrato — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contracts/contract-form/contract-form.component')
            .then(m => m.ContractFormComponent)
      },
      {
        path: 'contracts/:id/items',
        data: { title: 'Itens de Contrato — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contract-items/contract-item/contract-item.component')
            .then(m => m.ContractItemsComponent)
      },
      {
        path: 'contracts/:id/edit',
        data: { title: 'Atualizar Dados de Cliente — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/contracts/contract-form/contract-form.component')
            .then(m => m.ContractFormComponent)
      },

      // INVOICES - todos
      {
        path: 'invoices',
        data: { title: 'Faturas — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-list/invoice-list.component')
            .then(m => m.InvoiceListComponent)
      },
      {
        path: 'invoices/new',
        data: { title: 'Nova Fatura — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-form/invoice-form.component')
            .then(m => m.InvoiceFormComponent)
      },
      {
        path: 'invoices/:id/edit',
        data: { title: 'Atualizar Dados de Fatura — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-form/invoice-form.component')
            .then(m => m.InvoiceFormComponent)
      },
      {
        path: 'invoices/:id/lines',
        data: { title: 'Linhas de Fatura — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-lines/invoice-lines.component')
            .then(m => m.InvoiceLinesComponent)
      },
      {
        path: 'invoices/:id/detail',
        data: { title: 'Detalhes de Fatura — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER', 'FINANCIAL_ANALYST'])],
        loadComponent: () =>
          import('./features/invoices/invoice-detail/invoice-detail.component')
            .then(m => m.InvoiceDetailComponent)
      },

      // FINANCIAL PARAMETERS - só ADMIN e GESTOR
      {
        path: 'financial-parameters',
        data: { title: 'Parâmetros — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER'])],
        loadComponent: () =>
          import('./features/finacial-parameters/financial-parameter-list/financial-parameter-list.component')
            .then(m => m.FinancialParameterListComponent)
      },
      {
        path: 'financial-parameters/new',
        data: { title: 'Novo Parâmetro — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER'])],
        loadComponent: () =>
          import('./features/finacial-parameters/financial-parameter-form/financial-parameter-form.component')
            .then(m => m.FinancialParameterFormComponent)
      },
      {
        path: 'financial-parameters/:id/edit',
        data: { title: 'Atualizar Parâmetro — SGF' },
        canActivate: [authGuard, roleGuard(['ADMIN', 'FINANCIAL_MANAGER'])],
        loadComponent: () =>
          import('./features/finacial-parameters/financial-parameter-form/financial-parameter-form.component')
            .then(m => m.FinancialParameterFormComponent)
      },

      // RECONCILIATION - todos
      {
        path: 'reconciliation',
        data: { title: 'Reconciliação — SGF' },
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
