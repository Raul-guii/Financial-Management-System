import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LayoutComponent } from './core/layout/layout/layout';

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

      // DASHBOARD ---------
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/home/dashboard/dashboard.component')
            .then(m => m.DashboardComponent)
      },

      // USERS -------------
      {
        path: 'users',
        loadComponent: () =>
          import('./features/users/user-list/user-list.component')
            .then(m => m.UserListComponent)
      },
      {
        path: 'users/new',
        loadComponent: () =>
         import('./features/users/user-form/user-form.component')
          .then(m => m.UserFormComponent)
      },
      {
        path: 'users/edit/:id',
        loadComponent: () =>
          import('./features/users/user-form/user-form.component')
            .then(m => m.UserFormComponent)
      },

      // CLIENTS ------------
      {
        path: 'clients',
        loadComponent: () =>
          import('./features/clients/client-list/client-list.component')
            .then(m => m.ClientListComponent)
      },
      {
        path: 'clients/new',
        loadComponent: () =>
          import('./features/clients/client-form/client-form.component')
            .then(m => m.ClientFormComponent)
      },
      {
        path: 'clients/edit/:id',
        loadComponent: () =>
          import('./features/clients/client-form/client-form.component')
            .then(m => m.ClientFormComponent)
      },

      //CONTRACTS ---------
      {
        path: 'contracts',
        loadComponent: () =>
          import('./features/contracts/contract-list/contract-list.component')
            .then(m => m.ContractListComponent)
      },
      {
        path: 'contracts/new',
        loadComponent: () =>
          import('./features/contracts/contract-form/contract-form.component')
            .then(m => m.ContractFormComponent)
      },
      {
        path: 'contracts/edit/:id',
        loadComponent: () =>
          import('./features/contracts/contract-form/contract-form.component')
            .then(m => m.ContractFormComponent)
      },
      {
        path: 'contracts/:id/items',
        loadComponent: () =>
          import('./features/contracts/contract-items-list/contract-items-list.component')
            .then(m => m.ContractItemsListComponent)
      },
      {
        path: 'contracts/:id/items/new',
        loadComponent: () =>
          import('./features/contracts/contract-items/contract-items-form.component')
            .then(m => m.ContractItemsFormComponent)
      },
      {
        path: 'contracts/:id/invoice/new',
        loadComponent: () =>
          import('./features/invoices/invoice-form/invoice-form.component')
            .then(m => m.InvoiceFormComponent)
      },
      {
        path: 'invoices',
        loadComponent: () =>
          import('./features/invoices/invoice-list/invoice-list.component')
            .then(m => m.InvoiceListComponent)
      },
      {
        path: 'invoices/:id',
        loadComponent: () =>
          import('./features/invoices/invoice-detail/invoice-detail.component')
            .then(m => m.InvoiceDetailComponent)
      }
    ]
  },

  {
    path: '**',
    redirectTo: 'login'
  }
];