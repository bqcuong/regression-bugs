import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import 'rxjs/add/operator/switchMap';
import {ConfigService} from '../config/config.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';

@Component({
    selector: 'app-reset-password',
    templateUrl: './reset-password.component.html',
    styleUrls: ['./reset-password.component.css', '../card.css']
})
export class ResetPasswordComponent implements OnInit {

    email = 'admin@h2ms.org';
    resetToken: string;
    hide = true;
    private config;

    constructor(private http: HttpClient,
                private route: ActivatedRoute,
                private configService: ConfigService) {
        this.config = configService.getConfig();
    }

    ngOnInit() {
        this.route.paramMap.subscribe(
            params => this.resetToken = params.get('resetToken')
        );
    }

    savePassword(password: string) {
        let headers = new HttpHeaders();
        headers = headers.set('Content-Type', 'application/json');

        this.http.post<any>(this.config.getBackendUrl() + '/api/passwords/reset/token',
            {
                'token': this.resetToken,
                'password': password
            }, {
                headers: headers
            }
        ).subscribe(
            data => alert('success'),
            error => {
                alert('fail');
                console.log(error);
            }
        );
    }
}
