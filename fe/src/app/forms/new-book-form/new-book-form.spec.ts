import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewBookForm } from './new-book-form';

describe('NewBookForm', () => {
  let component: NewBookForm;
  let fixture: ComponentFixture<NewBookForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewBookForm],
    }).compileComponents();

    fixture = TestBed.createComponent(NewBookForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
