import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BorrowCardComponent } from './borrow-card-component';

describe('BorrowCardComponent', () => {
  let component: BorrowCardComponent;
  let fixture: ComponentFixture<BorrowCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BorrowCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BorrowCardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
