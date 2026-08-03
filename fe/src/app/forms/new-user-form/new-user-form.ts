import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, Output, PLATFORM_ID } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Role } from '../../models/role';
import { AuthService } from '../../services/auth-service';
import { HttpErrorResponse } from '@angular/common/http';
import { UserService } from '../../services/user-service/user-service';
import { isPlatformBrowser } from '@angular/common';

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
    private cdr: ChangeDetectorRef,
    private userService: UserService,
    @Inject(PLATFORM_ID) private platformId:Object
  ){}

  isPasswordMatch:boolean = true;
  isPasswordValid:boolean = true;

  isUsernameInvalid:boolean = false;
  isUsernameAvailable : boolean | null = null;

  isVerifyingEmail:boolean = false;
  isEmailVerified:boolean=false;
  isEmailInvalid: boolean = false;
  isSendingVerificationEmail:boolean = false;

  newUserForm = new FormGroup({
    username: new FormControl('', Validators.required),
    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', [Validators.required, Validators.pattern(/^(?:(?:0|\+84)\d{9}|(?:\+1)?\d{10})$/)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    address: new FormControl(''),
    role: new FormControl<number | null>(null, Validators.required),
    verificationCode: new FormControl('', Validators.required),
    password: new FormControl('', [Validators.required, Validators.pattern(/^(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/)]),
    confirmPassword: new FormControl('', [Validators.required, Validators.pattern(/^(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/)])
  });

  handleSendVerificationCode(){
    this.isVerifyingEmail = true;
    this.isSendingVerificationEmail = true;
    this.newUserForm.get('verificationCode')?.enable();
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
          if(data.data.verified){
            const message = this.translate.instant("verification.Your-email-has-been-verified");
            alert(message)
            this.isEmailVerified = true;
            this.isEmailInvalid = false;
            this.newUserForm.get('verificationCode')?.disable();
          }
          else{
            const message = this.translate.instant("verification.Incorrect-verification-code");
            alert(message)
          }
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

  checkUsername() {
    this.isUsernameInvalid = false;
    const username = this.newUserForm.get('username')?.value?.trim();
    if (!username){
      return;
    }

    this.userService.checkUsernameAvailability(username).subscribe({
      next: (data: any) => {
        if(data.data === true){
          this.isUsernameAvailable = true;
          this.isUsernameInvalid = false;
        }
        else{
          this.isUsernameAvailable = false;
        }
        this.cdr.markForCheck();
      },
      error: (err:HttpErrorResponse) => console.error(err)
    });
  }

  ngOnInit(){
    if (isPlatformBrowser(this.platformId)) {
      this.newUserForm.get('username')?.valueChanges.subscribe(() => {
        this.isUsernameAvailable = null;
        this.isUsernameInvalid = false;
        this.cdr.markForCheck();
      });
      this.newUserForm.get('email')?.valueChanges.subscribe(() => {
        this.isEmailInvalid = false;
        this.cdr.markForCheck();
      });
      this.newUserForm.get('password')?.valueChanges.subscribe(() => {
        this.isPasswordValid = this.newUserForm.get('password')?.valid ?? false;
        this.isPasswordMatch = true;
        this.cdr.markForCheck();
      });
    }
  }

  onSubmit(){
    console.log("Submit")
    const {username, fullName, phoneNumber, email, address, password, confirmPassword} = this.newUserForm.value;
    if(password!=confirmPassword){
      this.isPasswordMatch = false;
      this.cdr.markForCheck();
      return;
    }
    if(this.isUsernameAvailable == false || this.isUsernameAvailable == null){
        this.isUsernameInvalid = true;
        return;
      }
    if(this.isEmailVerified == false){
      this.isEmailInvalid = true;
      return;
    }
  }

  close(): void {
    this.onClose.emit();
  }
}
