import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnnouncementsManagementPage } from './announcements-management-page';

describe('AnnouncementsManagementPage', () => {
  let component: AnnouncementsManagementPage;
  let fixture: ComponentFixture<AnnouncementsManagementPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnnouncementsManagementPage],
    }).compileComponents();

    fixture = TestBed.createComponent(AnnouncementsManagementPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
