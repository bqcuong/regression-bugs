import { Injectable } from '@angular/core';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Oauth} from './oauth';


@Injectable()
export class AuthService {

    constructor(private http: HttpClient) { }

    client_id = 'h2ms';
    secret = 'secret';
    grant_type = 'password';
    tokenURL = 'http://test.h2ms.org:81/oauth/token';

    login(email: string, password: string) {
        // expect request to return:
        const httpOptions = {
            headers: new HttpHeaders({
                'Authorization': 'Basic ' + btoa(this.client_id + ':' + this.secret),
                'Content-Type': 'application/x-www-form-urlencoded'
            })
        };
        const dataString = 'grant_type=' + this.grant_type + '&username=' + email + '&password=' + password;
        return this.http.post<Oauth>(this.tokenURL, dataString, httpOptions)
            .do(user => {
                if (user && user.access_token) {
                    localStorage.setItem('currentUser', JSON.stringify(user));
                }
                return user;
                // todo: handle http request error (e.g. bad login credentials)
            });
    }

    logout(): void {
        if (localStorage.removeItem('currentUser')) {
            // todo: place logout request
        }
    }

    getToken(): string {
        // todo check and refresh token as needed here
        return JSON.parse(localStorage.getItem('currentUser')).access_token;
    }

    isLoggedIn(): boolean {
        if (localStorage.getItem('currentUser')) {
            return true;
        }
        return false;
    }
}
