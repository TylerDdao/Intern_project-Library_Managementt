import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExportBorrowsForm } from './export-borrows-form';

describe('ExportBorrowsForm', () => {
  let component: ExportBorrowsForm;
  let fixture: ComponentFixture<ExportBorrowsForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExportBorrowsForm],
    }).compileComponents();

    fixture = TestBed.createComponent(ExportBorrowsForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
