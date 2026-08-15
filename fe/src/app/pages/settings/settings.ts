import { ChangeDetectorRef, Component, Inject, OnChanges, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { isPlatformBrowser } from '@angular/common';
import { UserService } from '../../services/user-service/user-service';
import { User } from '../../models/user';
import { getUser } from '../../util/session-storage';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { AnnouncementService } from '../../services/announcement-service/announcement-service';
import { Announcement } from '../../models/announcement';
import { AnnouncementComponent } from "../../components/announcement-component/announcement-component";

@Component({
  selector: 'app-settings',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule, AnnouncementComponent],
  templateUrl: './settings.html',
  styleUrl: './settings.css',
})
export class Settings{
  user!:User;
  isUsernameAvailable : boolean | null = null;
  isUsernameVerified: boolean | null = null;

  isEmailVerified:boolean | null = null;
  isSendingVerificationEmail:boolean = false;
  isVerifyingEmail:boolean = false;


  isEmailUsed: boolean = false;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private userService: UserService, 
    private translate: TranslateService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private router:Router,
    private announcementService: AnnouncementService
  ){}

    announcements:Announcement[] =[]
    handleCloseAnnouncement(id: number) {
      this.announcementService.closeAnnouncement(id);
      this.announcements = this.announcementService.getAnnouncements();
      this.cdr.markForCheck();
    }

  newUserForm = new FormGroup({
    username: new FormControl('', Validators.required),
    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', [Validators.required, Validators.pattern(/^(?:(?:0|\+84)\d{9}|(?:\+1)?\d{10})$/)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    address: new FormControl(''),
    verificationCode: new FormControl('')
  });

  handleDeleteUser(){
    const message = this.translate.instant("form.Confirm-delete")
    const option = confirm(message + "?")
    if(!option) return
    this.userService.deleteMe().subscribe({
      next:(data:any)=>{
        if(data.code == "200"){
          sessionStorage.removeItem('token')
          sessionStorage.removeItem('user')
          sessionStorage.removeItem('authorities')
          this.router.navigate(['/login'])
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err,this.translate)
      }
    })
  }

  handleChangePassword(){
    this.router.navigate(['/settings/change-password'])
  }

  handleSendVerificationCode(){
    this.isSendingVerificationEmail = true;
    this.isEmailUsed = false;
    this.newUserForm.get('verificationCode')?.enable();
    const email = this.newUserForm.get("email")?.value?.trim().toLowerCase();
    if (!email) {
      return;
    }
    this.authService.sendVerificationCode(email, this.user.fullName).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          const message = this.translate.instant("verification.Your-verification-code-has-been-sent-to-your-email");
          alert(message)
          this.isVerifyingEmail = true;
        }
        this.isSendingVerificationEmail = false;
        this.cdr.markForCheck();
      },
      error: (err: HttpErrorResponse)=>{
        errorNoti(err, this.translate);
        if (err.error.code === "EMAIL-IN-USE") {
          this.isEmailUsed = true;
        }
        this.isSendingVerificationEmail = false;
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
            this.newUserForm.get('verificationCode')?.disable();
          }
          else{
            this.isEmailVerified = false;
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

  handleResetEmail(){
    this.isEmailVerified = false;
    this.isVerifyingEmail = false;
    this.isEmailUsed = false;
    this.newUserForm.patchValue({
      email: this.user.email
    })
    this.cdr.markForCheck()
  }

  handleResetUsername(){
    this.isUsernameAvailable = null;
    this.newUserForm.patchValue({
      username: this.user.username
    })
    this.isUsernameAvailable=null;
    this.cdr.markForCheck()
  }

  checkUsername() {
    this.isUsernameVerified = true;
    const username = this.newUserForm.get('username')?.value?.trim();
    if (!username){
      return;
    }

    this.userService.checkUsernameAvailability(username).subscribe({
      next: (data: any) => {
        if(data.data === true){
          this.isUsernameAvailable = true;
        }
        else{
          this.isUsernameAvailable = false;
        }
        this.cdr.markForCheck();
      },
      error: (err) => console.error(err)
    });
  }

  onSubmit(){
    const userId = this.user.id;

    const {username, fullName, phoneNumber, email, address} = this.newUserForm.value;

    if(this.user.username.trim().toLocaleLowerCase() !== username?.trim().toLocaleLowerCase() && this.isUsernameAvailable == null){
      this.isUsernameVerified = false;
      return;
    }

    if(this.user.email.trim().toLocaleLowerCase() !== email?.trim().toLocaleLowerCase() && this.isEmailVerified !== true){
      this.isEmailVerified = false;
      return;
    }
    if(!this.newUserForm.valid){
      
      return
    }

    let newUser: User = {...this.user};
    newUser.id = userId;
    newUser.username = this.newUserForm.get('username')?.value?.trim() ?? this.user.username;
    newUser.fullName = this.newUserForm.get('fullName')?.value?.trim() ?? this.user.fullName;
    newUser.email = this.newUserForm.get('email')?.value?.trim() ?? this.user.email;
    newUser.phoneNumber = this.newUserForm.get('phoneNumber')?.value?.trim() ?? this.user.phoneNumber;
    newUser.address = this.newUserForm.get('address')?.value?.trim() ?? this.user.address;

    this.userService.updateMe(newUser).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.userService.setCurrentUser(newUser);
          const message = this.translate.instant("user.Your-account-has-been-saved")
          alert(message)
          this.cdr.markForCheck();
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
      }
    })
  }

  ngOnInit(){
    if (isPlatformBrowser(this.platformId)) {
      this.announcements = this.announcementService.getAnnouncements()
      const sessionUser = getUser();
      if(sessionUser){
        this.user = sessionUser;
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
        this.isUsernameVerified = null;
        this.cdr.markForCheck();
      });
      this.newUserForm.get('email')?.valueChanges.subscribe(() => {
        this.isEmailVerified = null;
        this.isEmailUsed = false;
        this.cdr.markForCheck();
      });
    }
  }
}
