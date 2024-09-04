import { Routes } from '@angular/router';
import { PortfolioComponent } from './portfolio/portfolio.component';
import { HomeComponent } from './home/home.component';
import { StockdetailsComponent } from './stockdetails/stockdetails.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthentificationComponent } from './authentification/authentification.component';
import { AuthGuard } from './authentification/AuthGuard';

export const routes: Routes = [
    { path: 'authentification', component: AuthentificationComponent },
    { path: 'dashboard', component: DashboardComponent },
    { path: 'portfolio', component: PortfolioComponent, canActivate: [AuthGuard] },
    { path: 'guide', component: HomeComponent},
    { path: 'stockdetails/:id', component: StockdetailsComponent },
];
