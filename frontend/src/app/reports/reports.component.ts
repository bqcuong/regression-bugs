import {Component, OnInit} from '@angular/core';
import {ReportsService} from './reports.service';
import * as c3 from 'c3';
import {ChartAPI} from 'c3';
import {FormControl, Validators} from "@angular/forms";

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {

    // todo: styles below should be added to styleURLs above when patternfly style is ready
    // '../../../node_modules/patternfly/dist/css/patternfly.min.css',
    // '../../../node_modules/patternfly/dist/css/patternfly-additions.css'

        /*
    /events/count/{timeframe}

    Example API points:
    localhost:8080/events/count/week
      {
      "1st (2017)" : 10,
      "2nd (2017)": 5,
      ...
      "52nd (2017)": 3
      }

    localhost:8080/events/count/month
      {
        "January (2017)": 30,
        "February (2017)": 40
      }

    localhost:8080/events/count/year
      {
        "2017": 300,
        "2018": 1000
      }

    localhost:8080/events/count/quarter
      {
      "Q1 (2017)": 100,
      "Q2 (2017)": 200,
      "Q3 (2017)": 300,
      "Q4 (2017)": 400,
      "Q1 (2018)": 500,
      "Q2 (2018)": 600
      }

     */

    // todo: move h2ms-specific stuff to config
    baseURL = 'http://localhost:8080/';
    plots = [{value: 'events/count/', viewValue: 'number of observations'}];
    timeGroupings = [{value: 'year', viewValue: 'year'},
        {value: 'quarter', viewValue: 'quarter'},
        {value: 'month', viewValue: 'month'},
        {value: 'week', viewValue: 'week'}];

    // todo: chart title

    // make false when retrieving data from backend
    progressBarIsHidden = true;

    // make false when data has been retrieved and json is empty
    noDataMessageIsHidden = true;

    // chart related variables
     chart: ChartAPI;

    /**
     * form controls allow required fields
     */
    plotFormControl = new FormControl('', [
        Validators.required,
    ]);

    groupingFormControl = new FormControl('', [
        Validators.required,
    ]);


    constructor(private reportsService: ReportsService) {}

    ngOnInit() {
        this.progressBarIsHidden = true;
        this.noDataMessageIsHidden = true;
    }

    /**
     * This function submits a url to the reports service to retrieve a report json
     */
    submit(selectedPlot: string, selectedGrouping: string) {
      // todo: make sure valid input selection
        if (selectedPlot && selectedGrouping) {
            this.progressBarIsHidden = false;
            this.reportsService.fetchReport(this.baseURL + selectedPlot + selectedGrouping)
                .subscribe(
                    response => {
                        if (JSON.stringify(response).match('{}')) {
                            this.noDataMessageIsHidden = true;
                        } else {
                            this.noDataMessageIsHidden = false;
                            this.makeBarPlot(selectedPlot, selectedGrouping, response);
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

    /**
     * a function to make a determination about which type of plot to make and calls helper to convert
     * json to plot data
     */
    makeBarPlot(selectedPlot: string, selectedGrouping: string, data: Object) {
      if (selectedPlot.match('events/count/')) {
          if (selectedGrouping.match('year')) {
              this.makeBarPlotNumObsByYear(data);
          } else if (selectedGrouping.match('quarter')) {
              this.makeBarPlotNumObsByQuarter(data);
          } else if (selectedGrouping.match('month')) {
              this.makeBarPlotNumObsByMonth(data);
          } else if (selectedGrouping.match('week')) {
              this.makeBarPlotNumObsByWeek(data);
          }
      }
    }

    /**
     * converts json for number of observations by year to plot data
     */
    makeBarPlotNumObsByYear(data: Object) {
        // todo: remove demo data
        // const responseByYear = {
        //     '2017': 100,
        //     '2018': 600
        // };

        const columns: string[] = new Array();
        const values: [[string | number]] = [['obs']];

        for (const key of Object.keys(data)) {
            columns.push(key);
            values[0].push(data[key]);
        }

        this.groupedBarPlot(values, columns, false);

    }

    /**
     * converts json for number of observations by quarter to plot data
     */
    makeBarPlotNumObsByQuarter(data: Object) {
        // todo: remove demo data
        // const responseByQuarter = {
        //     'Q1 (2017)': 100,
        //     'Q2 (2017)': 200,
        //     'Q3 (2017)': 300,
        //     'Q4 (2017)': 400,
        //     'Q1 (2018)': 500,
        //     'Q2 (2018)': 600
        // };

        const categories: string[] = new Array();
        const groupedColumnsData: [[string | number]] = [['Q1'], ['Q2'], ['Q3'], ['Q4']];

        for (const key of Object.keys(data)) {
            const quarter = parseInt(key.substr(1, 1), 10);
            groupedColumnsData[quarter - 1].push(data[key]);

            const year = key.substr(4, 4);
            if (categories.indexOf(year) === -1) {
                categories.push(year);
            }
        }
        this.groupedBarPlot(groupedColumnsData, categories, true);
    }

    /**
     * converts json for number of observations by month to plot data
     */
    makeBarPlotNumObsByMonth(data: Object) {
        // todo: remove demo data
        // const responseByMonth = {
        //     'January (2017)': 100,
        //     'February (2017)': 200,
        //     'March (2017)': 300,
        //     'April (2017)': 400,
        //     'May (2017)': 500,
        //     'January (2018)': 600
        // };

        const referenceMonths = ['January', 'February', 'March', 'April', 'May', 'June',
            'July', 'August', 'September', 'October', 'November', 'December'];

        const categories: string[] = new Array();
        const groupedColumnsData: [[string | number]] =
            [['January'], ['February'], ['March'], ['April'], ['May'], ['June'],
                ['July'], ['August'], ['September'], ['October'], ['November'], ['December']
            ];

        for (const key of Object.keys(data)) {
            const endOfMonthName = key.indexOf(' (');
            const month = key.substr(0, endOfMonthName);
            groupedColumnsData[referenceMonths.indexOf(month)].push(data[key]);

            const year = key.substr( key.length - 5, 4);
            if (categories.indexOf(year) === -1) {
                categories.push(year);
            }
        }
        this.groupedBarPlot(groupedColumnsData, categories, true);
    }

    /**
     * converts json for number of observations by week to plot data
     */
    makeBarPlotNumObsByWeek(data: Object) {
        // todo: remove demo data
        // const responseByWeek = {
        //     '1st (2017)': 100,
        //     '2nd (2017)': 200,
        //     '3rd (2017)': 300,
        //     '4th (2017)': 400,
        //     '1st (2018)': 500,
        //     '4th (2018)': 600
        // };

        const categories: string[] = new Array();
        const groupedColumnsData: [[string | number]] = [['1']];
        const referenceWeeks: string[] = ['1st'];

        for (let i = 2; i <= 52; i++) {
            groupedColumnsData.push(['' + i]);
            referenceWeeks.push(this.getGetOrdinal(i));
        }

        for (const key of Object.keys(data)) {
            const endOfWeekOrdName = key.indexOf(' (');
            const week = key.substr(0, endOfWeekOrdName);
            groupedColumnsData[referenceWeeks.indexOf(week)].push(data[key]);

            const year = key.substr( key.length - 5, 4);
            if (categories.indexOf(year) === -1) {
                categories.push(year);
            }
        }
        this.groupedBarPlot(groupedColumnsData, categories, true);
    }

    /**
     * a helper function to add bar plot data to this.chart
     */
    groupedBarPlot(groupedColumnsData, categories, legendShow) {
        // todo: fully implement pf style
        // const verticalBarChartConfig = patternfly.c3ChartDefaults().getDefaultGroupedBarConfig(categories);
        // verticalBarChartConfig.bindto = '#chart';
        // verticalBarChartConfig.data = {
        //             columns: groupedColumnsData,
        //             type: 'bar'};
        // verticalBarChartConfig.axis = {
        //             x: {categories: categories,
        //                 type: 'category'}};
        // verticalBarChartConfig.legend = {show: legendShow};
        // this.chart = c3.generate(verticalBarChartConfig);
        this.chart = c3.generate({
            bindto: '#chart',
            data: {
                columns: groupedColumnsData,
                type: 'bar'
            },
            axis: {
                x: {
                    categories: categories,
                    type: 'category'
                }
            },
            legend: {
                show: legendShow
            }
        });
    }

    /**
     * a helder function to convert a number to its ordinal (1st, 2nd, etc.) string value
     */
    getGetOrdinal(n: number): string {
        const s = ['th', 'st', 'nd', 'rd'],
            v = n % 100;
        return n + (s[(v - 20) % 10] || s[v] || s[0]);
    }
}
