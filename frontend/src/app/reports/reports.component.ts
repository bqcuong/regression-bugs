import { Component, OnInit } from '@angular/core';
import {ReportsService} from './reports.service';
import {delay} from "rxjs/operator/delay";

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {

    progressBarIsHidden = true;

    selectedPlot: string;
    selectedGrouping: string;

    plots;
    groupings;

  constructor(private reportsService: ReportsService) {
      this.plots = reportsService.getPlots();
      this.groupings = reportsService.getGroupings();
  }

  ngOnInit() {}

    submit() {
      // todo: make sure valid input selection

        this.progressBarIsHidden = false;
        this.reportsService.fetchReport(this.selectedPlot, this.selectedGrouping)
            .subscribe(
                response => {
                    this.progressBarIsHidden = true;
                    alert(JSON.stringify(response));
                    },
                error => {
                    this.progressBarIsHidden = true;
                    if (error.status === 401) {
                        alert('authentication error: please login');
                    }
                    }
                    );
    }

}
