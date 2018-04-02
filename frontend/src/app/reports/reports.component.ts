import { Component, OnInit } from '@angular/core';
import {ReportsService} from './reports.service';

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
                    if (JSON.stringify(response).match('{}')) {
                        alert('no data');
                    } else {
                        alert(JSON.stringify(response));
                    }
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
