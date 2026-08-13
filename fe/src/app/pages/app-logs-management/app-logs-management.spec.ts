import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppLogsManagement } from './app-logs-management';

describe('AppLogsManagement', () => {
  let component: AppLogsManagement;
  let fixture: ComponentFixture<AppLogsManagement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppLogsManagement],
    }).compileComponents();

    fixture = TestBed.createComponent(AppLogsManagement);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
