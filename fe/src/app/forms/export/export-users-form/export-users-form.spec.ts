import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExportUsersForm } from './export-users-form';

describe('ExportUsersForm', () => {
  let component: ExportUsersForm;
  let fixture: ComponentFixture<ExportUsersForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExportUsersForm],
    }).compileComponents();

    fixture = TestBed.createComponent(ExportUsersForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
