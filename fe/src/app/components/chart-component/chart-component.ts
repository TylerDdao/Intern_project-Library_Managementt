import { isPlatformBrowser } from '@angular/common';
import { Component, ElementRef, ViewChild, Input, Inject, PLATFORM_ID, ChangeDetectorRef

 } from '@angular/core';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'app-chart-component',
  imports: [],
  templateUrl: './chart-component.html',
  styleUrl: './chart-component.css',
})
export class ChartComponent {
  @Input() labels: string[] = [];
  @Input() data: number[] = [];
  @Input() label: string = '';
  @Input() type: any = 'bar';
  @Input() colors: string[] = [];

  @ViewChild('myChart') myChart!: ElementRef;
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  private getBackgroundColor(): string | string[] {
      return this.colors.length > 0 ? this.colors : '#2C5EAD';
  }

  private chartInstance: Chart | null = null;

  ngAfterViewInit() {
      if (!isPlatformBrowser(this.platformId)) return;
      this.renderChart();
  }

  ngOnChanges() {
      if (this.chartInstance) {
          this.chartInstance.data.labels = this.labels;
          this.chartInstance.data.datasets[0].data = this.data;
          this.chartInstance.data.datasets[0].backgroundColor = this.getBackgroundColor();
          this.chartInstance.update();
      }
  }

  private renderChart() {
      this.chartInstance = new Chart(this.myChart.nativeElement, {
          type: this.type,
          data: {
              labels: this.labels,
              datasets: [{
                  label: this.label,
                  data: this.data,
                  backgroundColor: this.getBackgroundColor(),
              }]
          },
          options: { responsive: true }
      });
  }
}
