import {Component, OnInit} from '@angular/core';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

    hide = true;

    constructor(private auth: AuthService) {
    }

    ngOnInit() {
    }

    submit(email: string, password: string): void {
        let retries = 3;
        this.auth.login(email, password).subscribe({
            next(login) {
                if (login) {
                    this.router.navigate(['dashboard']);
                } else if (retries-- === 0) {
                    this.router.navigate(['password-recovery']);
                }
            }
        });
    }
}
