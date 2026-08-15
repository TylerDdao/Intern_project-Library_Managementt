import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Inject,
  OnChanges,
  Output,
  PLATFORM_ID,
  SimpleChanges
} from '@angular/core';

import { PolicyService } from '../../services/policy-service/policy-service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Policy } from '../../models/policy';
import { HttpErrorResponse } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { LoadingComponent } from '../../components/loading-component/loading-component';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { finalize, forkJoin } from 'rxjs';
import { errorNoti } from '../../util/error-notification';


@Component({
  selector: 'app-borrow-policy-form',
  imports: [
    TranslateModule,
    LoadingComponent,
    ReactiveFormsModule
  ],
  templateUrl: './borrow-policy-form.html',
  styleUrl: './borrow-policy-form.css',
})
export class BorrowPolicyForm implements OnChanges {

  @Output() onClose = new EventEmitter<void>();
  @Output() onChange = new EventEmitter<boolean>();


  constructor(
    private cdr: ChangeDetectorRef,
    private policyService: PolicyService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService
  ) {}


  isProcessing: boolean = false;
  isLoading: boolean = false;


  isValid: { [key: string]: boolean } = {
    borrowDuration: true,
    latePenaltyPerDay: true
  };
  borrowDuration!: Policy;
  latePenaltyPerDay!: Policy;
  borrowPolicyForm = new FormGroup({
    borrowDuration: new FormControl<number>(
      14,
      Validators.min(1)
    ),

    latePenaltyPerDay: new FormControl<number>(
      0,
      Validators.min(0)
    )
  });
  ngOnChanges(changes: SimpleChanges) {
    if (changes['borrowDuration'] && this.borrowDuration) {
      this.borrowPolicyForm.patchValue({
        borrowDuration: Number(this.borrowDuration.value)
      });
    }
  }
  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.fetchPolicy();
    }
  }
  fetchPolicy() {
    this.isLoading = true;
    forkJoin({
      borrowDuration: this.policyService.getPolicyByKey(
        "borrow_duration"
      ),
      latePenaltyPerDay: this.policyService.getPolicyByKey(
        "late_penalty_per_day"
      )
    })
    .pipe(
      finalize(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      })
    )
    .subscribe({
      next: (data: any) => {
        if (data.borrowDuration.code === "200") {
          this.borrowDuration = data.borrowDuration.data;
          this.borrowPolicyForm.patchValue({
            borrowDuration: Number(this.borrowDuration.value)
          });
        }
        if (data.latePenaltyPerDay.code === "200") {
          this.latePenaltyPerDay = data.latePenaltyPerDay.data;
          this.borrowPolicyForm.patchValue({latePenaltyPerDay: Number(this.latePenaltyPerDay.value)
          });
        }
      },
      error: (err: HttpErrorResponse) => {
        errorNoti(err,this.translate)
      }
    });
  }
  onSubmit() {
    if (this.isProcessing) {
      return;
    }
    const {borrowDuration, latePenaltyPerDay} = this.borrowPolicyForm.value;

    this.isValid["borrowDuration"] = this.borrowPolicyForm.get("borrowDuration")?.valid ?? false;
    this.isValid["latePenaltyPerDay"] =
      this.borrowPolicyForm.get("latePenaltyPerDay")?.valid ?? false;
    if (!this.borrowPolicyForm.valid) {
      return;
    }
    const requests = [];
    if (
      String(this.borrowDuration.value) !==
      String(borrowDuration)
    ) {
      requests.push(
        this.policyService.updatePolicy({
          key: 'borrow_duration',
          value: String(borrowDuration)
        })
      );
    }
    if (
      String(this.latePenaltyPerDay.value) !==
      String(latePenaltyPerDay)
    ) {
      requests.push(
        this.policyService.updatePolicy({
          key: 'late_penalty_per_day',
          value: String(latePenaltyPerDay)
        })
      );
    }
    if (requests.length === 0) {
      return;
    }
    this.isProcessing = true;
    forkJoin(requests)
      .pipe(
        finalize(() => {
          this.isProcessing = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({
        next: () => {
          const message = this.translate.instant("BorrowPolicyForm.Policy-updated");
          alert(message);
          this.borrowDuration.value = String(borrowDuration);
          this.latePenaltyPerDay.value = String(latePenaltyPerDay);
          this.onChange.emit(true);
        },
        error: (err: HttpErrorResponse) => {
          errorNoti(err, this.translate)
        }
      });
  }
  close(): void {
    this.onClose.emit();
  }
}