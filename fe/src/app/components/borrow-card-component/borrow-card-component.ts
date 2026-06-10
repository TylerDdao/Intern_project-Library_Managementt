import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { LanguageService } from '../../services/language-service/language-service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Borrow } from '../../models/borrow';

@Component({
  selector: 'app-borrow-card-component',
  imports: [TranslateModule],
  templateUrl: './borrow-card-component.html',
  styleUrl: './borrow-card-component.css',
})
export class BorrowCardComponent implements OnChanges {
    @Input({ required: true }) borrow!: Borrow;

    dueIn: number = 0;
    dueDate: Date = new Date();
    borrowOn: Date = new Date();
    formattedBorrowedOn: string = '';
    formattedDueDate: string = '';
    bookCover: string = '';

    constructor(public langService: LanguageService, private translate: TranslateService) {}

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['borrow'] && this.borrow?.book?.title) {
            this.dueDate = new Date(this.borrow.dueDate);
            this.borrowOn = new Date(this.borrow.createdAt);
            this.bookCover = this.borrow.book.title.replaceAll(' ', '-').toLowerCase();

            const today = new Date();
            today.setHours(0, 0, 0, 0);
            const due = new Date(this.dueDate);
            due.setHours(0, 0, 0, 0);

            const diffMs = due.getTime() - today.getTime();
            this.dueIn = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

            this.formatDate();

            this.translate.onLangChange.subscribe(() => {
                this.formatDate();
            });
        }
    }

    formatDate(): void {
        this.formattedDueDate = this.dueDate.toLocaleDateString(this.langService.currentLang, {
            year: 'numeric',
            month: 'long',
            day: '2-digit',
        });

        this.formattedBorrowedOn = this.borrowOn.toLocaleDateString(this.langService.currentLang, {
            year: 'numeric',
            month: 'long',
            day: '2-digit',
        });
    }
}