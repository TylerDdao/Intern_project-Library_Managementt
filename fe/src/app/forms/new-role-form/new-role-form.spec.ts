import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewRoleForm } from './new-role-form';

describe('NewRoleForm', () => {
  let component: NewRoleForm;
  let fixture: ComponentFixture<NewRoleForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewRoleForm],
    }).compileComponents();

    fixture = TestBed.createComponent(NewRoleForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
