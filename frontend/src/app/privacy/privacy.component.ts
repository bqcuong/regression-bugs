import { Component, OnInit } from '@angular/core';
import {ConfigService} from "../config.service";
import {Config} from "../config";

@Component({
  selector: 'app-privacy',
  templateUrl: './privacy.component.html',
  styleUrls: ['./privacy.component.css']
})

/**
 * A component for the H2MS Privacy Policy.
 */
export class PrivacyComponent implements OnInit {
  config: Config;
  constructor(private configService: ConfigService) {
    this.config = configService.getConfig();
  }
  ngOnInit() { }
}
