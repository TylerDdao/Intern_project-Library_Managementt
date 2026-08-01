import { ChangeDetectorRef, Component, EventEmitter, Inject, OnChanges, Output, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { PolicyService } from '../../services/policy-service/policy-service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Policy } from '../../models/policy';
import { HttpErrorResponse } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { LoadingComponent } from '../../components/loading-component/loading-component';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

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

  policy!: Policy

  borrowPolicyForm = new FormGroup({
    borrowDuration: new FormControl<number>(14, Validators.required),
  });

  ngOnChanges(changes: SimpleChanges) {
    if (changes['borrowDuration'] && this.policy) {
      this.borrowPolicyForm.patchValue({
        borrowDuration: Number(this.policy.value)
      });
    }
  }

  fetchPolicy(){
    this.policyService.getPolicyByKey("borrow_duration").subscribe({
      next: (data: any)=>{
        if(data.code == "200"){
          this.policy = data.data;
          this.borrowPolicyForm.patchValue({
            borrowDuration: Number(this.policy.value)
          });
          this.cdr.markForCheck();
          this.cdr.markForCheck()
        }
      },
      error: (err:HttpErrorResponse)=>{
        const message = this.translate.instant("error.An-error-has-occured")
        alert(message + "\n" + err.error.code)
        console.error(err.error.code + ": " + err.error.message)
      }
    })
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
