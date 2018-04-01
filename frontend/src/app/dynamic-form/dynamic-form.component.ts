import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { QuestionBase } from '../questions/question-base';
import { QuestionControlService } from '../questions/service/question-control.service';
import {Answer, Event} from '../';
import { EventEntityService} from '../';

@Component({
    selector: 'app-dynamic-form',
    templateUrl: './dynamic-form.component.html',
    providers: [ QuestionControlService,
        EventEntityService]
})
export class DynamicFormComponent implements OnInit {

    @Input() questions: QuestionBase<any>[] = [];
    form: FormGroup;

    constructor(private questionControlService: QuestionControlService,
                private eventEntityService: EventEntityService) {  }

    ngOnInit() {
        this.form = this.questionControlService.toFormGroup(this.questions);
    }

    onSubmit() {
        const answers: Array<Answer> = [];

        const values = this.form.value;

        // transform the value object into an array of Answers
        for (const property of Object.entries(values)) {
            if (property[0] !== 'location' && property[0] !== 'subject') {
                const answer: Answer = {
                    question: property[0],
                    value: property[1]
                };
                answers.push(answer);
            }
        }

        const event: Event = {
            eventTemplate: 'http://test.h2ms.org:81/eventTemplates/1',
            answers: answers,
            location: this.form.value['location'],
            observer: this.form.value['subject'],
            subject: this.form.value['subject'],
            timestamp: new Date()
        };

        this.eventEntityService.saveEventUsingPOST(event).subscribe();
    }
}
