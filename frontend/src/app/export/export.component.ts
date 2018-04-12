import { Component } from '@angular/core';
import { saveAs } from 'file-saver/FileSaver';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-export',
  templateUrl: './export.component.html',
    styleUrls: ['./export.component.css', '../card.css'],
  providers: [HttpClient]
})
export class ExportComponent {

  constructor(private httpClient: HttpClient) { }

  saveFile() {
    const headers = new HttpHeaders();
    headers.append('Accept', 'text/csv');
    this.httpClient.get('http://test.h2ms.org:81/events/export/csv', { headers: headers, responseType: 'text' })
        .subscribe(
            (response) => {
            this.saveToFileSystem(response);
            },
            (error) => {
                alert('Error downloading data file. Please report this error to a developer.');
                console.log(error);
            });
  }

  private saveToFileSystem(response) {
    const now = new Date();
    const timestamp = '' + now.getFullYear() + (now.getMonth() + 1) + now.getDate() +  now.getHours() + now.getMinutes() + now.getSeconds();
    const filename = 'events-'.concat(timestamp).concat('.csv');
    const blob = new Blob([response], { type: 'text/csv' });
    saveAs(blob, filename);
  }

}
