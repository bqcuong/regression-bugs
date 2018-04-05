import {MediaMatcher} from '@angular/cdk/layout';
import {ChangeDetectorRef, Component, OnDestroy, ViewChild} from '@angular/core';
import {Location} from '@angular/common';
import {NAV_ITEMS} from '../app-routing.module';
import {NavItem} from './nav-item';
import {MatSidenav} from '@angular/material';
import {ConfigService} from '../config/config.service';
import {Config} from '../config/config';
import {Title} from '@angular/platform-browser';


@Component({
    selector: 'app-sidenav',
    templateUrl: './sidenav.component.html',
    styleUrls: ['./sidenav.component.css']
})
/**
 * The SideNavComponent lists the navigation menu for the app. It is based on the following:
 * https://stackblitz.com/angular/ngjvmobekyl?file=app%2Fsidenav-responsive-example.css
 */
export class SidenavComponent implements OnDestroy {

    @ViewChild(MatSidenav)
    private matSidenav: MatSidenav;

    mobileQuery: MediaQueryList;
    navItems: NavItem[];
    config: Config;

    private _mobileQueryListener: () => void;

    constructor(private changeDetectorRef: ChangeDetectorRef,
                private media: MediaMatcher,
                private location: Location,
                private configService: ConfigService,
                private titleService: Title) {
        this.config = configService.getConfig();
        this.mobileQuery = media.matchMedia('(max-width: 600px)');
        this._mobileQueryListener = () => changeDetectorRef.detectChanges();
        this.mobileQuery.addListener(this._mobileQueryListener);
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
        return false;
        // TODO: Re-enable default behavior
        // return this.location.path() !== '/login' && !this.isMobileResolution();
    }

    private isMobileResolution() {
        return this.mobileQuery.matches;
    }

    ngOnDestroy(): void {
        this.mobileQuery.removeListener(this._mobileQueryListener);
    }

    switchConfigFile() {
        this.configService.toggleConfig();
        this.setTitle(this.config.appName);
    }

    /**
     * todo: move to app module
     */
    public setTitle(newTitle: string) {
        this.titleService.setTitle(newTitle);
    }

    /**
     * Used by the AppComponent to toggle the sidenav open and shut.
     */
    toggle(): void {
        this.matSidenav.toggle();
    }
}
