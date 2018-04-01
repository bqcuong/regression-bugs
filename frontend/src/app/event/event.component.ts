import {Component, OnInit} from '@angular/core';
import {EventTemplate} from '../';
import {ActivatedRoute} from '@angular/router';
import {CheckboxQuestion} from '../questions/question-checkbox';
import {DropdownQuestion} from '../questions/question-dropdown';
import {Question} from '../model/question';

@Component({
    selector: 'app-event',
    templateUrl: './event.component.html',
    styleUrls: ['./event.component.css'],
})
export class EventComponent implements OnInit {

    questions: any[] = [];
    eventTemplate: EventTemplate;

    constructor(private actr: ActivatedRoute) { }

    ngOnInit() {
        const locationResolver = this.actr.snapshot.data.locationResolver;

        // Add a question for Location, and populate it's options with results from locationResolver
        const locOptions = locationResolver._embedded.locations.map(location => location.name);
        const locParams = {
            id: 'location',
            question: 'Location',
            options: locOptions,
            required: true
        }
        this.questions.push(new DropdownQuestion(locParams));

        // Populate the dynamic questions
        const questionResolver = this.actr.snapshot.data.questionResolver;
        questionResolver._embedded.questions
            .sort((a, b) => a.priority - b.priority)
            .forEach((q: Question) => {
                const params = {
                    id: questionResolver._links.self.href,
                    question: q.question,
                    options: q.options,
                    required: q.required,
                    priority: q.priority
                };

                if (q.answerType === 'options') {
                    this.questions.push(new DropdownQuestion(params));
                } else if (q.answerType === 'boolean') {
                    this.questions.push(new CheckboxQuestion(params));
                }
            });
    }

}
