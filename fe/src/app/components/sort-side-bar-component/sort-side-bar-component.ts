import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
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
  @Input() isSearch: boolean = false;

  @Output() onApply = new EventEmitter<SideBarQuery>();

  selectedSort: string = '';
  selectedFilters: string[] = [];
  searchQuery: string = '';
  isClear: boolean = false;

  isExpandFilter: boolean = false;

  constructor(
    private cdr: ChangeDetectorRef,
  ) {}


  handleExpandFiler(): void {
    this.isExpandFilter = !this.isExpandFilter;
  }


  onFilterChange(option: string, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;

    if (checked) {
      if (!this.selectedFilters.includes(option)) {
        this.selectedFilters.push(option);
      }
    } else {
      this.selectedFilters = this.selectedFilters.filter(
        filter => filter !== option
      );
    }

    this.isClear = false;

    this.cdr.markForCheck();
  }


  apply(): void {
    this.isClear = false;

    this.onApply.emit({
      sortBy: this.selectedSort,
      filterBy: this.selectedFilters,
      searchQuery: this.searchQuery,
      isClear: this.isClear
    });
  }


  handleClear(): void {

    // Reset all local values
    this.selectedSort = '';
    this.selectedFilters = [];
    this.searchQuery = '';
    this.isExpandFilter = false;

    this.isClear = true;

    // Notify parent component
    this.onApply.emit({
      sortBy: '',
      filterBy: [],
      searchQuery: '',
      isClear: true
    });

    this.cdr.markForCheck();
  }

}