import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent} from './login/login.component';
import {PrivacyComponent} from './privacy/privacy.component';
import {EventComponent} from './event/event.component';
import { NavItem } from './nav-item';

/**
 * The actual available routes. Which links are routed to which components.
 */
const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: '', redirectTo: 'login', pathMatch: 'full'},
    // TODO: Route about to the AboutComponent when it is created.
    { path: 'dashboard', redirectTo: 'login', pathMatch: 'full'},
    { path: 'privacy', component: PrivacyComponent },
    { path: 'event', component: EventComponent }
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
        // new NavItem('Export All Observations', '/export'),
    ]),
    // new NavItem('Help', '/help'),
    // new NavItem('About', '/about'}
];

@NgModule({
    exports: [ RouterModule ],
    imports: [ RouterModule.forRoot(routes) ]
})
export class AppRoutingModule {}

