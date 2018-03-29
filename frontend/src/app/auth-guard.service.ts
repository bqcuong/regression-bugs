import { Injectable } from '@angular/core';
import {AuthService} from './auth.service';
import {CanActivate, Router} from '@angular/router';

/**
 * this class protects routes to frontend pages that are meant for logged in users only
 */
@Injectable()
export class AuthGuardService implements CanActivate {

  constructor(private authService: AuthService, private router: Router) { }

    canActivate(): boolean {
      // todo: find a more elegant way to determine if the user has ever logged in
    if (!this.authService.getToken().match('')) {
            return true;
        }
        this.router.navigate(['login']);
        return false;
    }

}
