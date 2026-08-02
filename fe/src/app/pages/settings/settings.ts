import { ChangeDetectorRef, Component, Inject, OnChanges, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { LanguageService } from '../../services/language-service/language-service';
import { Router, RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { isPlatformBrowser } from '@angular/common';
import { UserService } from '../../services/user-service/user-service';
import { User } from '../../models/user';
import { getUser } from '../../util/session-storage';
import { HttpErrorResponse } from '@angular/common/http';
import { Role } from '../../models/role';
import { errorNoti } from '../../util/error-notification';

@Component({
  selector: 'app-settings',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule, RouterLink],
  templateUrl: './settings.html',
  styleUrl: './settings.css',
})
export class Settings implements OnChanges{
  user!:User
  isUsernameAvailable : boolean | null = null;

  isUsernameInvalid: boolean = false;

  isVerifyingEmail:boolean = false;
  isEmailVerified:boolean=false;
  isEmailInvalid: boolean = false;
  isSendingVerificationEmail:boolean = false;

  isEmailUsed: boolean = false;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private userService: UserService, 
    private translate: TranslateService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef) {}

  newUserForm = new FormGroup({
    username: new FormControl('', Validators.required),
    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', [Validators.required, Validators.pattern(/^(0|\+84)[0-9]{9}$/)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    address: new FormControl(''),
    verificationCode: new FormControl('')
  });

  handleSendVerificationCode(){
    this.isVerifyingEmail = true;
    this.isSendingVerificationEmail = true;
    this.newUserForm.get('verificationCode')?.enable();
    const email = this.newUserForm.get("email")?.value?.trim().toLowerCase();
    if (!email) {
      return;
    }
    this.authService.sendVerificationCode(email, this.user?.fullName).subscribe({
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
      error: (err: HttpErrorResponse)=>{
        const message = this.translate.instant("verification.There-is-an-error-while-sending-verification-code");
        alert(message + '\n' + err.error.code + ': ' + err.error.message )
        this.isSendingVerificationEmail = false;
        console.error(err.error.message);
        this.cdr.markForCheck();
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
        errorNoti(err, this.translate)
      }
    })
  }

  handleResetEmail(){
    this.isEmailVerified = false;
    this.isVerifyingEmail = false;
    this.isEmailUsed = false;
    this.newUserForm.patchValue({
      email: this.user.email
    })
    this.cdr.markForCheck()
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['user'] && this.user) {
      this.newUserForm.patchValue({
        username: this.user.username,
        fullName: this.user.fullName,
        phoneNumber: this.user.phoneNumber,
        email: this.user.email,
        address: this.user.address,
      });
    }
  }

  handleResetUsername(){
    this.isUsernameInvalid = false;
    this.newUserForm.patchValue({
      username: this.user.username
    })
    this.isUsernameAvailable=null;
    this.cdr.markForCheck()
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
      error: (err:HttpErrorResponse) =>{
        errorNoti(err, this.translate)
      } 
    });
  }

  onSubmit(){
    const userId = this.user.id;
    const {username, fullName, phoneNumber, email, address} = this.newUserForm.value;
    if(username?.trim() !== this.user.username.trim()){
      if(this.isUsernameAvailable == false || this.isUsernameAvailable == null){
        this.isUsernameInvalid = true;
        return;
      }
    }
    if(email?.trim().toLocaleLowerCase() !== this.user.email.trim().toLocaleLowerCase()){
      if(this.isEmailVerified == false){
        this.isEmailInvalid = true;
        return;
      }
    }

    let newUser: User = {...this.user};
    newUser.id = userId;
    newUser.username = this.newUserForm.get('username')?.value?.trim() ?? this.user.username;
    newUser.fullName = this.newUserForm.get('fullName')?.value?.trim() ?? this.user.fullName;
    newUser.email = this.newUserForm.get('email')?.value?.trim() ?? this.user.email;
    newUser.phoneNumber = this.newUserForm.get('phoneNumber')?.value?.trim() ?? this.user.phoneNumber;
    newUser.address = this.newUserForm.get('address')?.value?.trim() ?? this.user.address;

    this.userService.updateUser(newUser).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          const message = this.translate.instant("user.Your-account-has-been-saved");
          alert(message)
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
      }
    })
  }

  ngOnInit(){
    if (isPlatformBrowser(this.platformId)) {
      const user = getUser();
      if (user) {
        this.user = user;
        this.newUserForm.patchValue({
          username: this.user.username,
          fullName: this.user.fullName,
          phoneNumber: this.user.phoneNumber,
          email: this.user.email,
          address: this.user.address,
        });
      }
      this.newUserForm.get('username')?.valueChanges.subscribe(() => {
        this.isUsernameAvailable = null;
        this.isUsernameInvalid = false;
        this.cdr.markForCheck();
      });
      this.newUserForm.get('email')?.valueChanges.subscribe(() => {
        this.isEmailInvalid = false;
        this.isEmailVerified=false;
        this.newUserForm.patchValue({
          verificationCode: ""
        })
        this.cdr.markForCheck();
      });
    }
  }
}
