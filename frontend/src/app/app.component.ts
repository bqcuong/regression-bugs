import {MediaMatcher} from '@angular/cdk/layout';
import { ChangeDetectorRef, Component } from '@angular/core';
import {ConfigService} from './config.service';
import { Location } from '@angular/common';
import { NAV_ITEMS } from './app-routing.module';
import {NavItem} from "./nav-item";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

/**
 * Main component for the App. The Navbar is based on this example:
 * https://stackblitz.com/angular/ngjvmobekyl?file=app%2Fsidenav-responsive-example.css
 */
export class AppComponent {
    mobileQuery: MediaQueryList;
    title: String;
    navItems: NavItem[];

    private _mobileQueryListener: () => void;

    constructor(private changeDetectorRef: ChangeDetectorRef,
                private media: MediaMatcher,
                private configService: ConfigService,
                private location: Location) {
        this.mobileQuery = media.matchMedia('(max-width: 600px)');
        this._mobileQueryListener = () => changeDetectorRef.detectChanges();
        this.mobileQuery.addListener(this._mobileQueryListener);
        this.title = configService.getConfig().navbarTitle;
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
}
