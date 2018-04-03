import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

@Injectable()
export class ReportsService {


    constructor(private http: HttpClient) { }

    fetchReport(url: string) {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
            })
        };
        return this.http.get(url, httpOptions);
    }
}
