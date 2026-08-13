import { Component } from '@angular/core';
import { LoadingComponent } from "../../components/loading-component/loading-component";

@Component({
  selector: 'app-test-page',
  imports: [LoadingComponent],
  templateUrl: './test-page.html',
  styleUrl: './test-page.css',
})
export class TestPage {}
