import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MiniPostCardComponent } from './mini-post-card-component';

describe('MiniPostCardComponent', () => {
  let component: MiniPostCardComponent;
  let fixture: ComponentFixture<MiniPostCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MiniPostCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MiniPostCardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
