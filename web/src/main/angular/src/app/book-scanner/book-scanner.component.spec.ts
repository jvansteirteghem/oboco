import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BookScannerComponent } from './book-scanner.component';

describe('BookScannerComponent', () => {
  let component: BookScannerComponent;
  let fixture: ComponentFixture<BookScannerComponent>;

  beforeEach(async(() => {
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
