import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Oauth} from './oauth';

/**
 * this class provides a login service and holds an auth token
 */
@Injectable()
export class AuthService {

    constructor(private http: HttpClient) { }

    o: Oauth;
    client_id = 'h2ms';
    secret = 'secret';
    grant_type = 'password';
    tokenURL = 'http://test.h2ms.org:81/oauth/token';

    login(email: string, password: string): Observable<boolean> {
        // expect request to return:
        const headers = new HttpHeaders().set('user', this.client_id).set('pass', this.secret);

        const dataString = 'grant_type=' + this.grant_type + '&username=' + email + '&password=' + password;
        // todo: handle http request error (e.g. bad login credentials)
        this.http.post<Oauth>(this.tokenURL, dataString, { headers: headers, withCredentials: true}).subscribe({
            next(oa) {
                this.o = oa;
            }
        });
        return Observable.of(this.isLoggedIn());
    }

    logout(): void {
        // todo: implement logout
    }

    /**
     * might refresh token
     * @returns {string}
     */
    getToken(): string {
        // todo call refresh function
        return this.o.access_token;
    }

    /**
     * does not refresh token
     * @returns {boolean}
     */
    isLoggedIn(): boolean {
        return !this.o.access_token.match('');
    }
}
