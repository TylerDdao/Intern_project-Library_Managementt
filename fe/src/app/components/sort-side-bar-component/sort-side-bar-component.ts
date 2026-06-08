import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';

export interface SideBarQuery {
  sortBy: string;
  filterBy: string[];
  searchQuery: string;
}

@Component({
  selector: 'app-sort-side-bar-component',
  imports: [FormsModule, TranslateModule],
  templateUrl: './sort-side-bar-component.html',
  styleUrl: './sort-side-bar-component.css',
})

export class SortSideBarComponent {
  @Input() sortBy: string[] = ["Test", "Test 2"];
  @Input() filterBy: string[] = [];

  @Output() onApply = new EventEmitter<SideBarQuery>();

  selectedSort: string = '';
  selectedFilters: string[] = [];
  searchQuery: string = 'Genres';

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
    });
  }
}
