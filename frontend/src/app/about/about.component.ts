import { Component, OnInit } from '@angular/core';
import {ConfigService} from '../config/config.service';
import {Config} from '../config/config';

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.css']
})
export class AboutComponent implements OnInit {
    config: Config;

  constructor(private configService: ConfigService) {
      this.config = configService.getConfig();
  }

  ngOnInit() {
  }

}
