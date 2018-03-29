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
 * this class provides an http request interceptor to prepend tokens to requests from logged in users
 */
@Injectable()
export class TokenInterceptor implements HttpInterceptor {


    constructor(public auth: AuthService) {}


    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const token = this.auth.getToken();
        // todo: handle login case before the token is initialized in a more elegant way
        if (this.auth.isLoggedIn()) {
            return next.handle(request); // for now we don't prepend token until we are logged in
        }
        request = request.clone({
            setHeaders: {
                Authorization: `Bearer ${this.auth.getToken()}`
            }
        });
        return next.handle(request);
    }
}
