import { Component, OnInit } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {

/*
/events/count/{timeframe}

Example API points:
localhost:8080/events/count/week
localhost:8080/events/count/month
localhost:8080/events/count/year
localhost:8080/events/count/quarter
 */

    baseURL = 'http://test.h2ms.org:81/'
    plots = [{value: 'events/count/', viewValue: 'number of observations'}];
    groupings = [{value: 'week', viewValue: 'week'},
        {value: 'month', viewValue: 'month'},
        {value: 'year', viewValue: 'year'},
        {value: 'quarter', viewValue: 'quarter'}];

  constructor(private http: HttpClient) { }

  ngOnInit() { }

  fetchReport() {
      // todo: make seaparate requests to fetch report options, and fetch a specific report

  }

}
