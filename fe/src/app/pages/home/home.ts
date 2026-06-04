import { Component } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-home',
  imports: [TranslateModule, NavbarComponent],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {}
