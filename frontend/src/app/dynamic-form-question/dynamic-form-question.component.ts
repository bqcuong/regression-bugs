import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { QuestionBase } from '../questions/question-base';

@Component({
    selector: 'app-question',
    templateUrl: './dynamic-form-question.component.html'
})
export class DynamicFormQuestionComponent {
    @Input() question: QuestionBase<any>;
    @Input() form: FormGroup;
    get isValid() {
        if (this !== undefined && Object.keys(this.form.controls).length !== 0) {
            return this.form.controls[this.question.id].valid;
        }
        return false;
    }
}
