import { Injectable } from '@angular/core';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Oauth} from './oauth';
import {ConfigService} from '../config/config.service';
import {Config} from '../config/config';
import {Router} from '@angular/router';
import {timer} from "rxjs/observable/timer";



@Injectable()
export class AuthService {

    private config: Config;
    private readonly tokenURL: string;
    private isRefreshingToken: boolean;
    private localStorageKey = 'h2msCookie';
    private client_id = 'h2ms';
    // todo secure secret
    private secret = 'secret';

    private timer;
    private timerSubscription;

    constructor(private http: HttpClient,
                private configService: ConfigService,
                private router: Router) {
        this.config = configService.getConfig();
        this.tokenURL = this.config.backendURL + ':' + this.config.backendPort + '/oauth/token';
        this.isRefreshingToken = false;
    }

    login(email: string, password: string) {
        // expect request to return:
        const httpOptions = {
            headers: new HttpHeaders({
                'Authorization': 'Basic ' + btoa(this.client_id + ':' + this.secret),
                'Content-Type': 'application/x-www-form-urlencoded'
            })
        };
        const dataString = 'grant_type=password&username=' + email + '&password=' + password;
        return this.http.post<Oauth>(this.tokenURL, dataString, httpOptions)
            .do(response => {
                if (response && response.access_token) {
                    localStorage.setItem(this.localStorageKey, JSON.stringify(response));
                    // subtract 10s from refresh time to account for travel delay
                    this.timer = timer(0, (response.expires_in - 10) * 1000);
                    this.timerSubscription = this.timer.subscribe(() => this.refreshToken());
                    return response;
                }
            });
    }

    logout(): void {
        if (localStorage.removeItem(this.localStorageKey)) {
            // todo: place logout request to backend
            this.timerSubscription;
        }
        this.router.navigate(['login']);
    }

    getToken(): string {

        return JSON.parse(localStorage.getItem(this.localStorageKey)).access_token;
    }

    isLoggedIn(): boolean {
        return !!localStorage.getItem(this.localStorageKey);
    }

    refreshToken() {
        if (!this.isRefreshingToken) {
            this.isRefreshingToken = true;
            const httpOptions = {
                headers: new HttpHeaders({
                    'Authorization': 'Basic ' + btoa(this.client_id + ':' + this.secret),
                    'Content-Type': 'application/x-www-form-urlencoded'
                })
            };
            const dataString = 'grant_type=refresh_token&client_id=' + this.client_id + '&refresh_token='
                + JSON.parse(localStorage.getItem(this.localStorageKey)).refresh_token;
            return this.http.post<Oauth>(this.tokenURL, dataString, httpOptions)
                .do(response => {
                    if (response && response.access_token) {
                        localStorage.setItem(this.localStorageKey, JSON.stringify(response));
                        this.isRefreshingToken = false;
                        return response;
                    }
                });
        }
    }

    getTokenURL() {
        return this.tokenURL;
    }
}
