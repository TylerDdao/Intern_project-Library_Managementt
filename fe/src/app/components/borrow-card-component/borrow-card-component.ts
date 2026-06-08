import { Component, Input } from '@angular/core';
import { LanguageService } from '../../services/language-service/language-service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-borrow-card-component',
  imports: [TranslateModule],
  templateUrl: './borrow-card-component.html',
  styleUrl: './borrow-card-component.css',
})
export class BorrowCardComponent {
  @Input() bookName: string = 'Null';
  @Input() author: string = 'Null';
  @Input() borrowedOn: Date = new Date();
  @Input() dueDate: Date = new Date();

  dueIn: number = 0;

  formattedBorrowedOn: string = '';
  formattedDueDate: string = '';
  bookCover: string =''

  constructor(public langService: LanguageService, private translate: TranslateService) {}

  ngOnInit(): void {
    this.bookCover = this.bookName.replaceAll(" ", "-").toLocaleLowerCase()

    let today = new Date();
    let diffMs = this.dueDate.getTime() - today.getTime();
    this.dueIn = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

    this.formatDate();

    this.translate.onLangChange.subscribe(() => {
        this.formatDate();
    });
  }

  formatDate(): void {
    this.formattedDueDate = this.dueDate.toLocaleDateString(this.langService.currentLang, {
        year: 'numeric',
        month: 'long',
        day: '2-digit',
    });

    this.formattedBorrowedOn = this.borrowedOn.toLocaleDateString(this.langService.currentLang, {
        year: 'numeric',
        month: 'long',
        day: '2-digit',
    });
  }
}
