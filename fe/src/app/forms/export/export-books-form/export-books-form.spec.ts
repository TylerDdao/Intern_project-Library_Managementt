import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExportBooksForm } from './export-books-form';

describe('ExportBooksForm', () => {
  let component: ExportBooksForm;
  let fixture: ComponentFixture<ExportBooksForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExportBooksForm],
    }).compileComponents();

    fixture = TestBed.createComponent(ExportBooksForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
