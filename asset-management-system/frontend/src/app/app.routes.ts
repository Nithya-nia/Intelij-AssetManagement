import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { LoginComponent } from './components/login/login.component';
import { LayoutComponent } from './components/layout/layout.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { EmployeesComponent } from './components/employees/employees.component';
import { LaptopsComponent } from './components/laptops/laptops.component';
import { MobileSimComponent } from './components/mobile-sim/mobile-sim.component';
import { AssetHistoryComponent } from './components/asset-history/asset-history.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'employees', component: EmployeesComponent },
      { path: 'laptops', component: LaptopsComponent },
      { path: 'mobile-sim', component: MobileSimComponent },
      { path: 'mobiles', redirectTo: 'mobile-sim' },
      { path: 'sim-cards', redirectTo: 'mobile-sim' },
      { path: 'sim-assignments', redirectTo: 'mobile-sim' },
      { path: 'asset-history', component: AssetHistoryComponent }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
