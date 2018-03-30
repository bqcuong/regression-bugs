import {Component, OnInit} from '@angular/core';
import {QuestionService} from '../questions/service/question.service';

@Component({
    selector: 'app-event',
    templateUrl: './event.component.html',
    styleUrls: ['./event.component.css'],
    providers: [QuestionService]
})
export class EventComponent implements OnInit {

    questions: any[];

    constructor(service: QuestionService) {
        this.questions = service.getQuestions();
    }

    ngOnInit() {
    }

}
