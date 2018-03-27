import { Injectable } from '@angular/core';

import { DropdownQuestion } from '../question-dropdown';
import { QuestionBase } from '../question-base';
import { TextboxQuestion } from '../question-textbox';
import {CheckboxQuestion} from '../question-checkbox';

@Injectable()
export class QuestionService {

    // Todo: get from a remote source of question metadata
    // Todo: make asynchronous
    getQuestions() {

        let questions: QuestionBase<any>[] = [

            new DropdownQuestion({
                id: '1',
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
                id: '2',
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
                id: '3',
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
                id: '4',
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
                id: '5',
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
                id: '6',
                question: 'Washed?',
                required: true,
                priority: 6
            }),
        ];

        return questions.sort((a, b) => a.priority - b.priority);
    }
}
