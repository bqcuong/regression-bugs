import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

@Injectable()
export class ReportsService {
    /*
  /events/count/{timeframe}

  Example API points:
  localhost:8080/events/count/week
  localhost:8080/events/count/month
  localhost:8080/events/count/year
  localhost:8080/events/count/quarter
   */

    // todo: move h2ms specific stuff to config
    baseURL = 'http://localhost:8080/';
    plots = [{value: 'events/count/', viewValue: 'number of observations'}];
    timeGroupings = [{value: 'week', viewValue: 'week'},
        {value: 'month', viewValue: 'month'},
        {value: 'year', viewValue: 'year'},
        {value: 'quarter', viewValue: 'quarter'}];

    constructor(private http: HttpClient) { }

    fetchReport(plot: string, grouping: string) {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };
        this.http.get(this.baseURL + plot + grouping, httpOptions)
            .do(response => {
                alert(JSON.stringify(response));
                return response;
            });
    }

    getPlots() {
        return this.plots;
    }

    getGroupings() {
        return this.timeGroupings;
    }

}
