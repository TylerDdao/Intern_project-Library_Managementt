import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnDevPage } from './on-dev-page';

describe('OnDevPage', () => {
  let component: OnDevPage;
  let fixture: ComponentFixture<OnDevPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OnDevPage],
    }).compileComponents();

    fixture = TestBed.createComponent(OnDevPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
