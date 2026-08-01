import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BorrowPolicyForm } from './borrow-policy-form';

describe('BorrowPolicyForm', () => {
  let component: BorrowPolicyForm;
  let fixture: ComponentFixture<BorrowPolicyForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BorrowPolicyForm],
    }).compileComponents();

    fixture = TestBed.createComponent(BorrowPolicyForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
