import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Configuration } from './configuration';

import { BasicErrorControllerService } from './api/basicErrorController.service';
import { EventControllerService } from './api/eventController.service';
import { EventEntityService } from './api/eventEntity.service';
import { EventTemplateEntityService } from './api/eventTemplateEntity.service';
import { LocationEntityService } from './api/locationEntity.service';
import { ProfileControllerService } from './api/profileController.service';
import { QuestionEntityService } from './api/questionEntity.service';
import { ReaderEntityService } from './api/readerEntity.service';
import { RoleEntityService } from './api/roleEntity.service';
import { UserEntityService } from './api/userEntity.service';
import { WristBandEntityService } from './api/wristBandEntity.service';

@NgModule({
  imports:      [ CommonModule, HttpClientModule ],
  declarations: [],
  exports:      [],
  providers: [
    BasicErrorControllerService,
    EventControllerService,
    EventEntityService,
    EventTemplateEntityService,
    LocationEntityService,
    ProfileControllerService,
    QuestionEntityService,
    ReaderEntityService,
    RoleEntityService,
    UserEntityService,
    WristBandEntityService ]
})
export class ApiModule {
    public static forRoot(configurationFactory: () => Configuration): ModuleWithProviders {
        return {
            ngModule: ApiModule,
            providers: [ { provide: Configuration, useFactory: configurationFactory } ]
        }
    }

    constructor( @Optional() @SkipSelf() parentModule: ApiModule) {
        if (parentModule) {
            throw new Error('ApiModule is already loaded. Import your base AppModule only.');
        }
    }
}
