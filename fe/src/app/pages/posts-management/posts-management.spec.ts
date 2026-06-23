import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PostsManagement } from './posts-management';

describe('PostsManagement', () => {
  let component: PostsManagement;
  let fixture: ComponentFixture<PostsManagement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostsManagement],
    }).compileComponents();

    fixture = TestBed.createComponent(PostsManagement);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
