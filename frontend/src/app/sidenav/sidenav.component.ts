import {MediaMatcher} from '@angular/cdk/layout';
import {ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import { Location } from '@angular/common';
import { NAV_ITEMS } from '../app-routing.module';
import {NavItem} from '../nav-item';
import {MatSidenav} from "@angular/material";

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.css']
})
/**
 * The SideNavComponent lists the navigation menu for the app. It is based on the following:
 * https://stackblitz.com/angular/ngjvmobekyl?file=app%2Fsidenav-responsive-example.css
 */
export class SidenavComponent {

    @ViewChild(MatSidenav)
    private matSidenav: MatSidenav;

    mobileQuery: MediaQueryList;
    navItems: NavItem[];

    private _mobileQueryListener: () => void;

    constructor(private changeDetectorRef: ChangeDetectorRef,
                private media: MediaMatcher,
                private location: Location) {
        this.mobileQuery = media.matchMedia('(max-width: 600px)');
        this._mobileQueryListener = () => changeDetectorRef.detectChanges();
        this.mobileQuery.addListener(this._mobileQueryListener);
        this.navItems = NAV_ITEMS;
        for (const navItem of this.navItems) {
            navItem.showSubItems = navItem.isCurrentlySelected(location.path());
        }

    }

    isSidebarOpenOnPageLoad() {
        return this.location.path() !== '/login' && !this.isMobileResolution();
    }

    private isMobileResolution() {
        return this.mobileQuery.matches;
    }

    ngOnDestroy(): void {
        this.mobileQuery.removeListener(this._mobileQueryListener);
    }

    /**
     * Used by the AppComponent to toggle the sidenav open and shut.
     */
    toggle(): void {
        this.matSidenav.toggle();
    }
}
