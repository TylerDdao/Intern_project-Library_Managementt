import { Component, Input } from '@angular/core';
import { User } from '../../models/user';
import { TranslateModule } from '@ngx-translate/core';
import { LoadingComponent } from "../loading-component/loading-component";

@Component({
  selector: 'app-user-card',
  imports: [TranslateModule],
  templateUrl: './user-card.html',
  styleUrl: './user-card.css',
})
export class UserCard {
  @Input({required: true}) user !: User
}
