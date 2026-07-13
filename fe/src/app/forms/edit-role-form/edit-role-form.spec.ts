import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditRoleForm } from './edit-role-form';

describe('EditRoleForm', () => {
  let component: EditRoleForm;
  let fixture: ComponentFixture<EditRoleForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditRoleForm],
    }).compileComponents();

    fixture = TestBed.createComponent(EditRoleForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
