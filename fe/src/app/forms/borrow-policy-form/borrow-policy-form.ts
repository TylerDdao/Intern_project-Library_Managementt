import { ChangeDetectorRef, Component, EventEmitter, Inject, OnChanges, Output, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { PolicyService } from '../../services/policy-service/policy-service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Policy } from '../../models/policy';
import { HttpErrorResponse } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { LoadingComponent } from '../../components/loading-component/loading-component';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { formatCurrency } from '../../util/format-number';

@Component({
  selector: 'app-borrow-policy-form',
  imports: [TranslateModule , LoadingComponent, ReactiveFormsModule],
  templateUrl: './borrow-policy-form.html',
  styleUrl: './borrow-policy-form.css',
})
export class BorrowPolicyForm implements OnChanges{
@Output() onClose = new EventEmitter<void>();
@Output() onChange = new EventEmitter<boolean>();

  constructor(
    private cdr: ChangeDetectorRef,
    private policyService: PolicyService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService
  ){}

  isLoadingpolicies: boolean= true;

  borrowDuration!: Policy
  latePenaltyPerDay!: Policy

  borrowPolicyForm = new FormGroup({
    borrowDuration: new FormControl<number>(14),
    latePenaltyPerDay: new FormControl<number>(0)
  });

  ngOnChanges(changes: SimpleChanges) {
    if (changes['borrowDuration'] && this.borrowDuration) {
      this.borrowPolicyForm.patchValue({
        borrowDuration: Number(this.borrowDuration.value)
      });
    }
  }

  fetchPolicy(){
    this.isLoadingpolicies = true;
    this.policyService.getPolicyByKey("borrow_duration").subscribe({
      next: (data: any)=>{
        if(data.code == "200"){
          this.borrowDuration = data.data;
          this.borrowPolicyForm.patchValue({
            borrowDuration: Number(this.borrowDuration.value)
          });
          this.checkLoadingDone();
        }
      },
      error: (err:HttpErrorResponse)=>{
        const message = this.translate.instant("error.An-error-has-occured")
        alert(message + "\n" + err.error.code)
        console.error(err.error.code + ": " + err.error.message)
      }
    })

    this.policyService.getPolicyByKey("late_penalty_per_day").subscribe({
      next: (data: any)=>{
        if(data.code == "200"){
          this.latePenaltyPerDay = data.data;
          this.borrowPolicyForm.patchValue({
            latePenaltyPerDay: Number(this.latePenaltyPerDay.value)
          });
          this.checkLoadingDone();
        }
      },
      error: (err:HttpErrorResponse)=>{
        const message = this.translate.instant("error.An-error-has-occured")
        alert(message + "\n" + err.error.code)
        console.error(err.error.code + ": " + err.error.message)
      }
    })

    this.isLoadingpolicies = false;
  }

  checkLoadingDone(){
    if(this.borrowDuration && this.latePenaltyPerDay){
      this.isLoadingpolicies = false;
      this.cdr.markForCheck();
    }
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.fetchPolicy()
    }
  }

  onSubmit(){
    const { borrowDuration } = this.borrowPolicyForm.value;

    const borrowPolicy: Policy = {
      key: 'borrow_duration',
      value: String(borrowDuration)
    };

    this.policyService.updatePolicy(borrowPolicy).subscribe({
      next: (data: any) => {
        if (data.code == "200") {
          const message = this.translate.instant("BorrowPolicyForm.Policy-updated")
          alert(message)
          this.cdr.markForCheck();
        }
      },
      error: (err:HttpErrorResponse) => {
        const message = this.translate.instant("error.An-error-has-occured")
        alert(message+"\n"+err.error.code);
        console.error(err.error.code + ": " +err.error.message);
      }
    });
  }

  close(): void {
    this.onClose.emit();
  }
}
