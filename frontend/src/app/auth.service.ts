import { Injectable } from '@angular/core';
import {Observable} from "rxjs/Observable";
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';

@Injectable()
export class AuthService {

  constructor() { }

    isLoggedIn = false;

    login(email: string, password: string): Observable<boolean> {
        // todo: replace with https request
        if (email.match("email") && password.match("password")) {
            return Observable.of(true).delay(1000).do(val => this.isLoggedIn = true);
        }
        return Observable.of(false).delay(1000).do(val => this.isLoggedIn = false);
    }

    logout(): void {
        this.isLoggedIn = false;
    }

    isAuthenticated(): boolean {
        // todo: replace with session token
        return this.isLoggedIn;
    }

}
