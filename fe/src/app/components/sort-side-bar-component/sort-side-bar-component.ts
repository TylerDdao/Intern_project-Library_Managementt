import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';

export interface SideBarQuery {
  sortBy: string;
  filterBy: string[];
  searchQuery: string;
  isClear: boolean;
}

@Component({
  selector: 'app-sort-side-bar-component',
  imports: [FormsModule, TranslateModule],
  templateUrl: './sort-side-bar-component.html',
  styleUrl: './sort-side-bar-component.css',
})

export class SortSideBarComponent {
  @Input() sortBy: string[] = [];
  @Input() filterBy: string[] = [];
  @Input() isSearch:boolean = false;

  @Output() onApply = new EventEmitter<SideBarQuery>();

  selectedSort: string = '';
  selectedFilters: string[] = [];
  searchQuery: string = '';
  isClear:boolean = false;

  onFilterChange(option: string, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.selectedFilters.push(option);
    } else {
      this.selectedFilters = this.selectedFilters.filter(f => f !== option);
    }
  }

  apply(): void {
    this.onApply.emit({
      sortBy: this.selectedSort,
      filterBy: this.selectedFilters,
      searchQuery: this.searchQuery,
      isClear: this.isClear
    });
  }

  handleClear():void {
    this.onApply.emit({
      sortBy: '',
      filterBy: [],
      searchQuery: '',
      isClear: true
    })
  }
}
