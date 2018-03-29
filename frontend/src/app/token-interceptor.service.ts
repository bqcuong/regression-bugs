import { Injectable } from '@angular/core';
import {
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpInterceptor
} from '@angular/common/http';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs/Observable';


/**
 * this class provides an http request interceptor to add tokens to requests from logged in users
 */
@Injectable()
export class TokenInterceptor implements HttpInterceptor {


    constructor(public auth: AuthService) {}


    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (this.auth.isLoggedIn()) {
            request = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${this.auth.getToken()}`
                }
            });
        }
        return next.handle(request); // don't add token until we are logged in
    }
}
