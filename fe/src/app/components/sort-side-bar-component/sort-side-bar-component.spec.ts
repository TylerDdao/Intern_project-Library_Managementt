import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SortSideBarComponent } from './sort-side-bar-component';

describe('SortSideBarComponent', () => {
  let component: SortSideBarComponent;
  let fixture: ComponentFixture<SortSideBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SortSideBarComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SortSideBarComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
