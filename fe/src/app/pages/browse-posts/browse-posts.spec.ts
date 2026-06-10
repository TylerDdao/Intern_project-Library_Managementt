import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowsePosts } from './browse-posts';

describe('BrowsePosts', () => {
  let component: BrowsePosts;
  let fixture: ComponentFixture<BrowsePosts>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BrowsePosts],
    }).compileComponents();

    fixture = TestBed.createComponent(BrowsePosts);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
