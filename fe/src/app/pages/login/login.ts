import { ChangeDetectorRef, Component, ElementRef, Inject, PLATFORM_ID, ViewChild } from '@angular/core';
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
import { environment } from '../../../environments/environment';

declare const turnstile: any;

@Component({
  selector: 'app-login',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule, RouterLink, AnnouncementComponent],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  wrongCredential = false;
  @ViewChild('turnstileContainer') turnstileContainer!: ElementRef<HTMLDivElement>;

  constructor(
    public langService: LanguageService,
    private authService: AuthService,
    protected router: Router,
    private cdr: ChangeDetectorRef,
    private userService: UserService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private announcementService: AnnouncementService
  ){}

  turnstileToken: string | null = null;
  private turnstileWidgetId: string | null = null;

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.renderTurnstile();
  }

  private renderTurnstile(attempt = 0): void {
    if (typeof turnstile !== 'undefined') {
      this.turnstileWidgetId = turnstile.render(this.turnstileContainer.nativeElement, {
        sitekey: environment.turnstileSitekey,
        theme: 'light',
        callback: (token: string) => {
          this.turnstileToken = token;
          this.cdr.markForCheck();
        },
      });
      return;
    }
    if (attempt < 20) {
      setTimeout(() => this.renderTurnstile(attempt + 1), 100);
    }
  }

  announcements: Announcement[] = []
  handleCloseAnnouncement(id: number) {
    this.announcementService.closeAnnouncement(id);
    this.announcements = this.announcementService.getAnnouncements();
    this.cdr.markForCheck();
  }

  login(username: string, password: string){
    removeAuthInfo();
    this.authService.login(username, password, this.turnstileToken!).subscribe({
        next: (data: any) => {
            if(data.code == 200){
                sessionStorage.setItem("token", data.data.token);
                this.userService.setCurrentUser(data.data.user);
                sessionStorage.setItem("authorities", JSON.stringify(data.data.authorities));
                window.location.href = "/home";
            }
        },
        error: (err) => {
            this.wrongCredential = true;
            this.turnstileToken = null;
            if (this.turnstileWidgetId) {
                turnstile.reset(this.turnstileWidgetId); // force a new challenge
            }
            this.cdr.markForCheck();
            console.error(err);
        }
    });
  }

  loginForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  onSubmit() {
    if (this.loginForm.invalid || !this.turnstileToken) return;

    const { username, password } = this.loginForm.value;
    this.login(username ?? '', password ?? '');
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.announcements = this.announcementService.getAnnouncements();
    }
  }
}
