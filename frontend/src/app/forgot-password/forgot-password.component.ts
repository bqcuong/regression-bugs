import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ConfigService} from '../config/config.service';
import {Config} from '../config/config';
import {MatDialog} from '@angular/material';
import {DIALOG_STYLE} from '../../dialog/dialog';

@Component({
    selector: 'app-forgot-password',
    templateUrl: './forgot-password.component.html',
    styleUrls: ['./forgot-password.component.css', '../card.css']
})
export class ForgotPasswordComponent {

    private config: Config;

    constructor(private http: HttpClient,
                private configService: ConfigService,
                private dialog: MatDialog) {
        this.config = this.configService.getConfig();
    }

    sendRecoveryEmail(emailAddress: string) {
        const callbackUrl = encodeURIComponent(this.config.getFrontendUrl() + '/reset-password/' + emailAddress);
        const sendRecoveryEmailUrl = this.config.getBackendUrl() + '/api/passwords/reset/' + encodeURIComponent(emailAddress)
            + '?resetPasswordCallback=' + callbackUrl;

        this.http.get(sendRecoveryEmailUrl).subscribe(
            data => this.successDialog(),
            error => {
                alert('Failed to send recovery email. Please contact a developer about this issue.');
                console.log(error);
            }
        );
    }

    successDialog(): void {
        this.dialog.open(SuccessfullySentPasswordRecoveryEmailComponent, DIALOG_STYLE);
    }
}

@Component({
    templateUrl: 'success-submission-dialog.html'
})
export class SuccessfullySentPasswordRecoveryEmailComponent {
}
