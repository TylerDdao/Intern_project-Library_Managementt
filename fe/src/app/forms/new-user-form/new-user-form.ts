import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Role } from '../../models/role';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-new-user-form',
  imports: [TranslateModule, ReactiveFormsModule],
  templateUrl: './new-user-form.html',
  styleUrl: './new-user-form.css',
})
export class NewUserForm {
  @Input() roles: Role[] = [];
  @Output() onClose = new EventEmitter<void>();
  constructor(
    private authService: AuthService,
    private translate: TranslateService,
    private cdr: ChangeDetectorRef
  ){}

  isUsernameInvalid:boolean = false;

  isVerifyingEmail:boolean = false;
  isEmailVerified:boolean=false;
  isEmailInvalid: boolean = false;
  isSendingVerificationEmail:boolean = false;

  newUserForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    address: new FormControl(''),
    role: new FormControl<number | null>(null, Validators.required),
    verificationCode: new FormControl('', Validators.required)
  });

  handleSendVerificationCode(){
    this.isVerifyingEmail = true;
    this.isSendingVerificationEmail = true;
    const email = this.newUserForm.get("email")?.value?.trim().toLowerCase();
    if (!email) {
      return;
    }
    const name = this.translate.instant("user.New-user")
    this.authService.sendVerificationCode(email, name).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          const message = this.translate.instant("verification.Your-verification-code-has-been-sent-to-your-email");
          alert(message)
          
        }
        if(data.code == "CODE-ALREADY-SENT"){
          const message = this.translate.instant("verification.Your-verification-code-has-already-been-sent-to-your-email");
          alert(message +"\n" + data.code)
        }
        if(data.code == "EMAIL-IN-USE"){
          const message = this.translate.instant("verification.Email-has-been-used");
          alert(message +"\n" + data.code)
          this.isVerifyingEmail = false;
        }
        this.isSendingVerificationEmail = false;
        this.cdr.markForCheck();
      },
      error: (err)=>{
        const message = this.translate.instant("verification.There-is-an-error-while-sending-verification-code");
        alert(message)
        console.error(err)
      }
    })
    this.cdr.markForCheck();
  }

  handleVerify(){
    const email = this.newUserForm.get("email")?.value?.trim().toLowerCase();
    if (!email) {
      return;
    }
    const verificationCode = this.newUserForm.get("verificationCode")?.value?.trim().toLowerCase();
    if(!verificationCode){
      return;
    }
    this.authService.submitVerificationCode(email, verificationCode).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          const message = this.translate.instant("verification.Your-email-has-been-verified");
          alert(message)
          this.isEmailVerified = true;
          this.isEmailInvalid = false;
          this.cdr.markForCheck();
        }
      },
      error: (err)=>{
        const message = this.translate.instant("verification.There-is-an-error-while-verifying-your-email");
        alert(message)
        console.error(err)
      }
    })
  }

  onSubmit(){
    console.log("Submit")
  }

  close(): void {
    this.onClose.emit();
  }
}
