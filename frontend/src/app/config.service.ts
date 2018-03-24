import { Injectable } from '@angular/core';
import {Config} from './config';
import * as h2ms from './h2ms-config';
import * as gloves from './gloves-config';

/**
 * The config service provides a Config file.
 */
@Injectable()
export class ConfigService {
  useH2MSConfig = false;

  constructor() { }
  getConfig(): Config {
    if (this.useH2MSConfig) {
      return h2ms.CONFIG;
    } else {
      return gloves.CONFIG;
    }
  }
}
