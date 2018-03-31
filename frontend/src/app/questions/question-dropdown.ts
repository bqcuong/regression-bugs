import { QuestionBase } from './question-base';

export class DropdownQuestion extends QuestionBase<string> {
    answerType = 'dropdown';
    options: string[] = [];

    constructor(params: {} = {}) {
        super(params);
        this.options = params['options'] || [];
    }
}
