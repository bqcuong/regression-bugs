import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ConfigService} from '../config/config.service';
import {Config} from '../config/config';
import {MatDialog, MatDialogRef} from "@angular/material";
import {FormSubmissionDialogComponent} from "../dynamic-form/dynamic-form.component";

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
        const url = this.config.getBackendUrl() + '/api/passwords/reset/' + emailAddress;
        this.http.get(url).subscribe(
            data => this.successDialog(),
            error => {
                this.errorDialog();
                console.log(error);
            }
        );
    }

    successDialog(): void {
        this.dialog.open(SuccessSubmissionDialogComponent, {
            width: '270px',
        });
    }

    errorDialog(): void {
        this.dialog.open(FailedSubmissionDialogComponent, {
            width: '270px',
        });
    }
}

@Component({
    templateUrl: 'success-submission-dialog.html'
})
export class SuccessSubmissionDialogComponent {

    constructor(public dialogRef: MatDialogRef<FormSubmissionDialogComponent>) {
    }

    closeDialog(): void {
        this.dialogRef.close();
    }
}

@Component({
    templateUrl: 'failed-submission-dialog.html'
})
export class FailedSubmissionDialogComponent {

    constructor(public dialogRef: MatDialogRef<FormSubmissionDialogComponent>) {
    }

    closeDialog(): void {
        this.dialogRef.close();
    }
}