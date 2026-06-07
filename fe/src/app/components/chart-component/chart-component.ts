import { isPlatformBrowser } from '@angular/common';
import { Component, OnInit, ElementRef, ViewChild, Input, Inject, PLATFORM_ID

 } from '@angular/core';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'app-chart-component',
  imports: [],
  templateUrl: './chart-component.html',
  styleUrl: './chart-component.css',
})
export class ChartComponent {
  @Input() labels: string[] = [];  // X axis
  @Input() data: number[] = [];    // Y axis
  @Input() label: string = '';     // dataset label
  @Input() type: any = 'bar';      // chart type

  @ViewChild('myChart') myChart!: ElementRef;
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  ngAfterViewInit() {
    if (!isPlatformBrowser(this.platformId)) return;
    new Chart(this.myChart.nativeElement, {
      type: this.type,
      data: {
        labels: this.labels,
        datasets: [{
          label: this.label,
          data: this.data,
          backgroundColor: '#3b82f6',
        }]
      },
      options: { responsive: true }
    });
  }
}
