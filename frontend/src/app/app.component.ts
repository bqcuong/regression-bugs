import {MediaMatcher} from '@angular/cdk/layout';
import {ChangeDetectorRef, Component, OnDestroy} from '@angular/core';
import {ConfigService} from './config/config.service';
import {Config} from './config/config';
import {NavItem} from "./sidenav/nav-item";
import {NAV_ITEMS} from "./app-routing.module";
import {Location} from '@angular/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

/**
 * Main component for the App. The Navbar is based on this example:
 * https://stackblitz.com/angular/ngjvmobekyl?file=app%2Fsidenav-responsive-example.css
 */
export class AppComponent implements OnDestroy {
    mobileQuery: MediaQueryList;
    config: Config;
    navItems: NavItem[];

    private _mobileQueryListener: () => void;

    constructor(private changeDetectorRef: ChangeDetectorRef,
                private media: MediaMatcher,
                private location: Location,
                private configService: ConfigService) {
        this.mobileQuery = media.matchMedia('(max-width: 600px)');
        this._mobileQueryListener = () => changeDetectorRef.detectChanges();
        this.mobileQuery.addListener(this._mobileQueryListener);
        this.config = configService.getConfig();
        this.navItems = NAV_ITEMS;
        for (const navItem of this.navItems) {
            navItem.showSubItems = navItem.isCurrentlySelected(location.path());
        }

    }


    isInProduction() {
        return window.location.hostname === 'www.h2ms.org';
    }

    isSidebarOpenOnPageLoad() {
        // Disabling for demo until I can get the push content to side working again. -Ben
        // return false;
        // TODO: Re-enable default behavior
        return this.location.path() !== '/login' && !this.isMobileResolution();
    }

    private isMobileResolution() {
        return this.mobileQuery.matches;
    }

    switchConfigFile() {
        this.configService.toggleConfig();
    }

    ngOnDestroy(): void {
        this.mobileQuery.removeListener(this._mobileQueryListener);
    }
}
