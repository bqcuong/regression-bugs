import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { AppRoutingModule } from './app-routing.module';
import {
    MatButtonModule,
    MatCardModule, MatCheckboxModule, MatDialogModule, MatDividerModule, MatFormFieldModule,
    MatIconModule,
    MatInputModule, MatListModule, MatSidenavModule, MatToolbarModule
} from '@angular/material';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { PrivacyComponent } from './privacy/privacy.component';
import {MediaMatcher} from '@angular/cdk/layout';
import {ConfigService} from './config.service';
import { SidenavComponent } from './sidenav/sidenav.component';



@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    PrivacyComponent,
    SidenavComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MatFormFieldModule,
      MatInputModule,
      BrowserAnimationsModule,
      MatCardModule,
      MatCheckboxModule,
      MatDividerModule,
      MatButtonModule,
      MatDialogModule,
      MatIconModule,
      MatToolbarModule,
      MatSidenavModule,
      MatListModule
  ],
  providers: [ MediaMatcher, ConfigService ],
  bootstrap: [AppComponent]
})
export class AppModule { }
