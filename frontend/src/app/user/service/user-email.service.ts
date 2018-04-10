import {Injectable} from '@angular/core';
import {Router} from '@angular/router';

@Injectable()
export class UserEmailService {

    localStorageKey = 'MyLittleUserEmailKey';

    constructor(private router: Router) {
    }

    setEmail(email: string): void {
        localStorage.setItem(this.localStorageKey, email);
    }

    getEmail(): string {
        const stored = localStorage.getItem(this.localStorageKey);

        // If we don't have an email, something is wrong.
        if (!!stored) {
            this.router.navigate(['login']);
        }
        return stored;
    }

}
