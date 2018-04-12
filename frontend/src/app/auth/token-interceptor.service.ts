import { Injectable } from '@angular/core';
import {
    HttpRequest,
    HttpHandler,
    HttpInterceptor
} from '@angular/common/http';
import { AuthService } from './auth.service';



@Injectable()
export class TokenInterceptor implements HttpInterceptor {

    constructor(public auth: AuthService) {}

    intercept(request: HttpRequest<any>, next: HttpHandler) {
        if (request.url !== this.auth.getTokenURL()
            && this.auth.isLoggedIn()) {
            this.auth.getToken().subscribe(token => {
                request = request.clone({
                    headers: request.headers.set('Authorization', `Bearer ${token}`)
                });
                console.log('inside interceptor, token: ' + token + ', req: ' + JSON.stringify(request));

                return next.handle(request).subscribe();
            });
        } else {
            console.log('inside interceptor, auth url used, no token needed: ');
            return next.handle(request);
        }
    }
}
