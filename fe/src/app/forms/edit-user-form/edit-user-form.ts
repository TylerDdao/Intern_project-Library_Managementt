import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, OnChanges, Output, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Role } from '../../models/role';
import { User } from '../../models/user';
import { isPlatformBrowser } from '@angular/common';
import { UserService } from '../../services/user-service/user-service';
import { AuthService } from '../../services/auth-service';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { getUser } from '../../util/session-storage';

@Component({
  selector: 'app-edit-user-form',
  imports: [TranslateModule, ReactiveFormsModule],
  templateUrl: './edit-user-form.html',
  styleUrl: './edit-user-form.css',
})

export class EditUserForm implements OnChanges {
  @Input() roles: Role[] = [];
  @Input() user!: User;
  @Output() onClose = new EventEmitter<void>();
  @Output() onChange = new EventEmitter<boolean>();

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
    private cdr: ChangeDetectorRef) {}

  newUserForm = new FormGroup({
    username: new FormControl('', Validators.required),
    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', [Validators.required, Validators.pattern(/^(?:(?:0|\+84)\d{9}|(?:\+1)?\d{10})$/)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    address: new FormControl(''),
    role: new FormControl<number | null>(null, Validators.required),
    verificationCode: new FormControl(''),
  });

  handleDeleteUser(){
    const message= this.translate.instant("usersManagement.Delete-user")
    const option = confirm(message + "?")
    if(!option) return
    this.userService.deleteUser(this.user).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.save(true)
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
      }
    })
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

  ngOnChanges(changes: SimpleChanges) {
    if (changes['user'] && this.user) {
      this.newUserForm.patchValue({
        username: this.user.username,
        fullName: this.user.fullName,
        phoneNumber: this.user.phoneNumber,
        email: this.user.email,
        address: this.user.address,
        role: this.user.role?.id ?? null
      });
    }
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

    const {username, fullName, phoneNumber, email, address, role} = this.newUserForm.value;

    if(this.user.username.trim().toLocaleLowerCase() !== username?.trim().toLocaleLowerCase() && this.isUsernameAvailable == null){
      this.isUsernameVerified = false;
      return;
    }

    if(this.user.email.trim().toLocaleLowerCase() !== email?.trim().toLocaleLowerCase()){
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
    const roleId = this.newUserForm.get('role')?.value;
    newUser.role = roleId != null ? { id: Number(roleId) } as Role : this.user.role;

    if(newUser.role?.id == this.user.role?.id){
      this.userService.updateUser(newUser).subscribe({
        next: (data:any)=>{
          if(data.code == "200"){
            if(userId == getUser()?.id){
              this.userService.setCurrentUser(newUser);
            }
            this.save(true);
          }
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate)
          this.save(false);
        }
      })
    }
    else{
      this.userService.updateUser(newUser).subscribe({
        next: (data:any)=>{
          if(data.code == "200"){
            this.userService.updateUserRole(newUser).subscribe({
              next: (data:any) => {
                if(data.code == "200"){
                  if(userId == getUser()?.id){
                    this.userService.setCurrentUser(newUser);
                  }
                  this.save(true);
                }
              },
              error: (err:HttpErrorResponse)=>{
                errorNoti(err, this.translate)
                this.save(false);
              }
            })
          }
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate)
          this.save(false);
        }
      }) 
    }
  }

  close(): void {
    this.onClose.emit();
  }

  save(isSave:boolean):void{
    this.onChange.emit(isSave);
  }

  ngOnInit(){
    if (isPlatformBrowser(this.platformId)) {
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