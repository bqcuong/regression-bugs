import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent} from './login/login.component';
import {PrivacyComponent} from './privacy/privacy.component';
import {EventComponent} from './event/event.component';
import { NavItem } from './sidenav/nav-item';
import {ExportComponent} from './export/export.component';
import {AuthGuardService} from './auth/auth-guard.service';

/**
 * The actual available routes. Which links are routed to which components.
 */
const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'privacy', component: PrivacyComponent },
    { path: 'event', component: EventComponent, canActivate: [AuthGuardService]},
    { path: 'export', component: ExportComponent, canActivate: [AuthGuardService]},
    // TODO: route dashboard to the DashboardComponent when it is created.
    { path: 'dashboard', redirectTo: 'event', pathMatch: 'full', canActivate: [AuthGuardService]}, // a protected page
    // TODO: route password-recovery to the PasswordRecoveryComponent when it is created.
    { path: 'password-recovery', pathMatch: 'full', redirectTo: 'privacy'}, // an unprotected page just for testing
    { path: '', redirectTo: 'dashboard', pathMatch: 'full'}
];

/**
 * Displayed in the side nav bar. Leaving future navItems commented out.
 * TODO: Uncomment relevant navItem when a new page is created.
 */
export const NAV_ITEMS: NavItem[] = [
    new NavItem('Dashboard', '/dashboard'),
    // new NavItem('Reports', '/reports'),
    new NavItem('Observe', '/event'),
    NavItem.createNavItemWithSubItems('Settings', [
        // new NavItem('Account', '/account'),
        // new NavItem('Notifications', '/notifications'),
        // new NavItem('Sensors', '/sensors'),
        // new NavItem('RFIDs', '/rfids'),
        // new NavItem('People', '/people'),
        // new NavItem('Locations', '/locations'),
        new NavItem('Privacy', '/privacy'),
        new NavItem('Export All Observations', '/export'),
    ]),
    // new NavItem('Help', '/help'),
    // new NavItem('About', '/about'}
];

@NgModule({
    exports: [ RouterModule ],
    imports: [ RouterModule.forRoot(routes) ]
})
export class AppRoutingModule {}

