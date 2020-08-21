import { Component, OnInit } from '@angular/core';
import { BookScanner } from './book-scanner';
import { BookScannerService } from './book-scanner.service';

@Component({
  selector: 'app-book-scanner',
  templateUrl: './book-scanner.component.html',
  styleUrls: ['./book-scanner.component.css']
})
export class BookScannerComponent implements OnInit {
  bookScanners: BookScanner[];
  error: string = "";

  constructor(private bookScannerService: BookScannerService) { }

  ngOnInit() {
    this.load();
  }

  load(): void {
    this.bookScannerService.getBookScannerList()
    .subscribe(
      bookScanners => {
        this.bookScanners = bookScanners;

        this.error = "";
      },
      error => {
        this.error = error.error.code + ": " + error.error.description;
      }
    );
  }

  onBookScannerStart(selectedBookScanner: BookScanner) {
    this.bookScannerService.startBookScanner(selectedBookScanner.id)
    .subscribe(
      () => {
      },
      error => {
        this.error = error.error.code + ": " + error.error.description;
      }
    );
  }

  onBookScannerStop(selectedBookScanner: BookScanner) {
    this.bookScannerService.stopBookScanner(selectedBookScanner.id)
    .subscribe(
      () => {
      },
      error => {
        this.error = error.error.code + ": " + error.error.description;
      }
    );
  }

  onBookScannerStatus(selectedBookScanner: BookScanner) {
    this.bookScannerService.getBookScanner(selectedBookScanner.id)
    .subscribe(
      bookScanner => {
        selectedBookScanner.status = bookScanner.status;

        this.error = "";
      },
      error => {
        this.error = error.error.code + ": " + error.error.description;
      }
    );
  }

}
