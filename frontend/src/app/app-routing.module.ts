import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent} from "./login/login.component";
import {PrivacyComponent} from "./privacy/privacy.component";

/**
 * The actual available routes. Which links are routed to which components.
 */
const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: '', redirectTo: 'login', pathMatch: 'full'},
    // TODO: Route about to the AboutComponent when it is created.
    { path: 'dashboard', redirectTo: 'login', pathMatch: 'full'},
    { path: 'privacy', component: PrivacyComponent },
];

/**
 * Displayed in the side nav bar. Leaving future navItems commented out.
 * TODO: Uncomment relevant navItem when a new page is created.
 */
export const NAV_ITEMS = [
    {display: 'Dashboard', link: '/dashboard'},
    // {display: 'Reports', link: '/reports'},
    // {display: 'Observe', link: '/observe'},
    {display: 'Settings', showSubItems: false, subItems: [
        // {display: 'Account', link: '/account'},
        // {display: 'Notifications', link: '/notifications'},
        // {display: 'Sensors', link: '/sensors'},
        // {display: 'RFIDs', link: '/rfids'},
        // {display: 'People', link: '/people'},
        // {display: 'Locations', link: '/locations'},
        {display: 'Privacy', link: '/privacy'},
        // {display: 'Export All Observations', link: '/export'},
    ]},
    // {display: 'Help', link: '/help'},
    // {display: 'About', link: '/about'}
];

@NgModule({
    exports: [ RouterModule ],
    imports: [ RouterModule.forRoot(routes) ]
})
export class AppRoutingModule {}

