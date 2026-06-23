import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BorrowsManagement } from './borrows-management';

describe('BorrowsManagement', () => {
  let component: BorrowsManagement;
  let fixture: ComponentFixture<BorrowsManagement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BorrowsManagement],
    }).compileComponents();

    fixture = TestBed.createComponent(BorrowsManagement);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
