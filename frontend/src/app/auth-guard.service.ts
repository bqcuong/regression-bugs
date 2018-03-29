import { Injectable } from '@angular/core';
import {AuthService} from "./auth.service";
import {Router} from "@angular/router";

@Injectable()
export class AuthGuardService {

  constructor(private authService: AuthService, private router: Router) { }

    canActivate(): boolean {
        if (this.authService.isAuthenticated()) return true;

        this.router.navigate(['login']);
        return false;
    }

}
