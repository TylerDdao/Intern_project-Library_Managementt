import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { AnnouncementService } from '../../services/announcement-service/announcement-service';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-signup-success',
  imports: [NavbarComponent, TranslateModule],
  templateUrl: './signup-success.html',
  styleUrl: './signup-success.css',
})
export class SignupSuccess {
  constructor(
    private router:Router,
    private announcementService: AnnouncementService,
    @Inject(PLATFORM_ID) private platformId: Object
  ){}

  handleBackToLogin(){
    this.router.navigate(['/login'])
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
    }
  }
}
