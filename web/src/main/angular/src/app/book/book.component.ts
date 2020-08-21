import { Component, OnInit } from '@angular/core';
import { Book } from './book';
import { BookService } from './book.service';
import { BookCollection } from '../book-collection';
import { BookMark, BookMarkService } from '../book-mark';
import { ActivatedRoute, Router, Event, NavigationEnd } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-file',
  templateUrl: './book.component.html',
  styleUrls: ['./book.component.css']
})
export class BookComponent implements OnInit {
  book: Book;
  bookCollection: BookCollection;
  bookMark: BookMark;
  page;
  error: string = "";
  bookMark_error: string = "";

  constructor(private bookService: BookService, private bookMarkService: BookMarkService, private route: ActivatedRoute, private router: Router, private location: Location) { }

  ngOnInit() {
    this.load();
  }

  load(): void {
    let id = +this.route.snapshot.queryParamMap.get('id');
    let page = +this.route.snapshot.queryParamMap.get('page');
    if(page) {
      this.page = page;
    } else {
      this.page = 1;
    }
    
    this.bookService.getBook(id)
    .subscribe(
      book => {
        this.book = book;
        this.bookCollection = book.bookCollection;
        this.bookMark = book.bookMark;
        
        this.error = "";
      },
      error => {
        this.error = error.error.code + ": " + error.error.description;
      }
    );
  }

  bookMark_onMark() {
    let bookMark: BookMark = new BookMark();
    bookMark.page = this.page;
    this.bookMarkService.createOrUpdateBookMark(this.book.id, bookMark)
    .subscribe(
      bookMark => {
        this.bookMark = bookMark;
      
        this.bookMark_error = "";
      },
      error => {
        this.bookMark_error = error.error.code + ": " + error.error.description;
      }
    );
  }

  bookMark_onGoToMark() {
    this.page = this.bookMark.page;
  }

  onFirstPage() {
    if(this.book) {
      this.page = 1;
    }
  }

  onPreviousPage() {
    if(this.book) {
      if(this.page > 1) {
        this.page = this.page - 1;
      }
    }
  }

  onNextPage() {
    if(this.book) {
      if(this.page < this.book.numberOfPages) {
        this.page = this.page + 1;
      }
    }
  }

  onLastPage() {
    if(this.book) {
      this.page = this.book.numberOfPages;
    }
  }

  bookCollection_onSelect(bookCollection: BookCollection) {
    this.router.navigate(['/bookCollection'], {
      queryParams: { bookCollectionId: bookCollection.id }
    });
  }

}
