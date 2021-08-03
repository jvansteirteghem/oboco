import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { BookScannerComponent } from './book-scanner.component';

describe('BookScannerComponent', () => {
  let component: BookScannerComponent;
  let fixture: ComponentFixture<BookScannerComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ BookScannerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BookScannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
