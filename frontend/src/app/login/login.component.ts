import {Component, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

    hide = true;
    loginAttempts = 2;

    constructor(private auth: AuthService, private router: Router) {
    }

    ngOnInit() {
        this.auth.logout();
    }

    submit(email: string, password: string): void {
        this.auth.login(email, password)
            .subscribe(
            response => {
                    this.router.navigate(['dashboard']);
            },
            error => {
                if (error.status === 401) {
                    alert('login failed');
                    if (this.loginAttempts-- === 0) {
                        this.router.navigate(['password-recovery']);
                    }
                }
            });
    }
}
