import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { BookMarkComponent } from './book-mark.component';

describe('BookMarkComponent', () => {
  let component: BookMarkComponent;
  let fixture: ComponentFixture<BookMarkComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ BookMarkComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BookMarkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
