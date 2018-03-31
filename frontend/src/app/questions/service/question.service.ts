import { Injectable } from '@angular/core';

import { DropdownQuestion } from '../question-dropdown';
import { QuestionBase } from '../question-base';
import { TextboxQuestion } from '../question-textbox';
import {CheckboxQuestion} from '../question-checkbox';
import {ResourcesQuestion} from '../../model/resourcesQuestion';
import { EventTemplateEntityService } from '../../';
import { Question} from '../../model/question';

@Injectable()
export class QuestionService {

    constructor(private templateService: EventTemplateEntityService) { }

    resourcesQuestion: ResourcesQuestion;

    // Todo: make asynchronous
    getQuestions() {

        /*const questions: QuestionBase<any>[] = [];

        this.templateService.eventTemplateQuestionsUsingGET1(0)
            .subscribe(resourcesQuestion => this.resourcesQuestion = resourcesQuestion);

        this.resourcesQuestion.content.forEach((q: Question) => {
            const params = {
                id: this.resourcesQuestion.links.find((link) =>  link.rel === 'self').href,
                question: q.question,
                options: q.options,
                required: q.required,
                priority: q.priority
            };

            if (q.answerType === 'options') {
                questions.push(new DropdownQuestion(params));
            } else if (q.answerType === 'boolean') {
                questions.push(new CheckboxQuestion(params));
            }
        });*/

        const questions: QuestionBase<any>[] = [

            new DropdownQuestion({
                id: 'location',
                question: 'Location',
                options: [
                    'Hospital 1',
                    'Hospital 2',
                    'Hospital 3',
                    'Hospital 4'
                ],
                required: true,
                priority: 1
            }),

            new DropdownQuestion({
                id: 'http://test.h2ms.org:81/questions/2',
                question: 'Employee Type',
                options: [
                    'Physician',
                    'Nurse',
                    'Surgeon',
                    'Janitor'
                ],
                required: true,
                priority: 2
            }),

            new DropdownQuestion({
                id: 'subject',
                question: 'Person',
                options: [
                    'Handwasher, Joe',
                    'Smith, John',
                    'Strange, Stephen',
                    'Von Doom, Victor'
                ],
                required: true,
                priority: 3
            }),

            new DropdownQuestion({
                id: 'http://test.h2ms.org:81/questions/4',
                question: 'Event',
                options: [
                    'Entering',
                    'Inside',
                    'Leaving'
                ],
                required: true,
                priority: 4
            }),

            new DropdownQuestion({
                id: 'http://test.h2ms.org:81/questions/5',
                question: 'Hygiene Type',
                options: [
                    'Type 1',
                    'Type 2',
                    'Type 3'
                ],
                required: true,
                priority: 5
            }),

            new CheckboxQuestion({
                id: 'http://test.h2ms.org:81/questions/6',
                question: 'Washed?',
                required: false,
                priority: 6
            }),
        ];

        return questions.sort((a, b) => a.priority - b.priority);
    }
}
