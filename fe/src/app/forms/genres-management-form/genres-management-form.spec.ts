import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenresManagementForm } from './genres-management-form';

describe('GenresManagementForm', () => {
  let component: GenresManagementForm;
  let fixture: ComponentFixture<GenresManagementForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenresManagementForm],
    }).compileComponents();

    fixture = TestBed.createComponent(GenresManagementForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
