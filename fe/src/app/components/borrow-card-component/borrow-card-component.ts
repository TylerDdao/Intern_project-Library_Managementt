import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, OnChanges, Output, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { LanguageService } from '../../services/language-service/language-service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Borrow } from '../../models/borrow';
import { BorrowService } from '../../services/borrow-service/borrow-service';
import { errorNoti } from '../../util/error-notification';
import { errorImage } from '../../../assets/constants';
import { formatCurrency } from '../../util/format-number';
import { Policy } from '../../models/policy';
import { PolicyService } from '../../services/policy-service/policy-service';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { LoadingComponent } from "../loading-component/loading-component";

@Component({
selector: 'app-borrow-card-component',
imports: [TranslateModule, LoadingComponent],
templateUrl: './borrow-card-component.html',
styleUrl: './borrow-card-component.css',
})
export class BorrowCardComponent implements OnChanges {
    @Input({ required: true }) borrow!: Borrow;
    @Input() editable: boolean = false;

    isLoading:boolean = false

    backendUrl = environment.apiUrl;

    dueIn: number = 0;
    dueDate: Date = new Date();
    borrowOn: Date = new Date();
    formattedBorrowedOn: string = '';
    formattedDueDate: string = '';
    bookCover: string = '';

    isReturned: boolean = false;

    latePenalty!: Policy

    constructor(
        public langService: LanguageService, 
        private translate: TranslateService, 
        private cdr: ChangeDetectorRef,
        private borrowService: BorrowService,
        private policyService: PolicyService,
        @Inject(PLATFORM_ID) private platformId: Object
    ) {}

    ngOnInit(){
        if(isPlatformBrowser(this.platformId)){
            this.fetchLatePenalty();
        }
    }

    fetchLatePenalty(){
        this.policyService.getPolicyByKey("late_penalty_per_day").subscribe({
            next: (data:any) => {
                if(data.code == "200"){
                    this.latePenalty = data.data;
                    this.cdr.markForCheck();
                }
            }
        })
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['borrow'] && this.borrow?.book?.title) {
            this.dueDate = new Date(this.borrow.dueDate);
            this.borrowOn = new Date(this.borrow.createdAt);
            this.bookCover = this.borrow.book.title.replaceAll(' ', '-').toLowerCase();
            this.isReturned = !this.borrow.active;

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

    get penalty():string{
        if(this.borrow.penalty){
            return formatCurrency(this.borrow.penalty);
        }
        else{return ""}
    }

    get penaltyFee():string{
        if(this.latePenalty){
            return formatCurrency(parseFloat(this.latePenalty.value));
        }
        else{return ""}
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

    getWebpCover(coverUrl: string | null | undefined): string {
        if (!coverUrl) {
        return 'default.webp';
        }

        return coverUrl.replace(/\.(jpg|jpeg|png)$/i, '.webp');
    }

    onImageError(event: Event): void {
        const image = event.target as HTMLImageElement;

        image.onerror = null;
        image.src = errorImage;
    }

    handleReturn(){
        const message = this.translate.instant('borrowManagement.Confirm-return');
        const confirmed = confirm(message+"?");
        if(confirmed){
            this.isLoading=true
            this.borrow.active = false
            this.borrowService.returnBorrow(this.borrow).subscribe({
                next: (data: any)=>{
                    if(data.code == "200"){
                        this.isReturned = true;
                    }
                    this.isLoading=false;
                    this.cdr.markForCheck()
                },
                error: (err) =>{
                    errorNoti(err, this.translate);
                    this.isLoading=false;
                    this.cdr.markForCheck()
                }
            })
        }
    }
}