import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { AuthService } from '../../services/auth-service';
import { Router, RouterLink } from '@angular/router';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { LanguageService } from '../../services/language-service/language-service';
import { UserService } from '../../services/user-service/user-service';
import { AnnouncementComponent } from "../../components/announcement-component/announcement-component";
import { Announcement } from '../../models/announcement';
import { AnnouncementService } from '../../services/announcement-service/announcement-service';
import { isPlatformBrowser } from '@angular/common';
import { removeAuthInfo } from '../../util/session-storage';

@Component({
  selector: 'app-login',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule, RouterLink, AnnouncementComponent],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  wrongCredential = false;

  constructor(
    public langService: LanguageService,
    private authService: AuthService,
    protected router: Router,
    private cdr: ChangeDetectorRef,
    private userService: UserService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private announcementService: AnnouncementService
  ){}

  announcements: Announcement[] = []
  handleCloseAnnouncement(id: number) {
    this.announcementService.closeAnnouncement(id);
    this.announcements = this.announcementService.getAnnouncements();
    this.cdr.markForCheck();
  }

  login(username: string, password: string){
    removeAuthInfo()
    this.authService.login(username, password).subscribe({
      next: (data: any) => {
        console.log(data)
        if(data.code == 200){
          sessionStorage.setItem("token", data.data.token);
          this.userService.setCurrentUser(data.data.user);
          sessionStorage.setItem("authorities", JSON.stringify(data.data.authorities));
          this.router.navigate(['/home']);
        }
      },
      error: (err) => {
        this.wrongCredential = true
        this.cdr.markForCheck()
        console.error(err);
      }
    })
  }

  loginForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  onSubmit() {
    if (this.loginForm.invalid) return;

    const { username, password } = this.loginForm.value;
    
    this.login(username ?? '', password ?? '')
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.announcements = this.announcementService.getAnnouncements();
    }
  }
}
