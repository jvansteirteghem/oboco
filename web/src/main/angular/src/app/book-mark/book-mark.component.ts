import { Component, OnInit, OnDestroy } from '@angular/core';
import { PageableList } from '../common';
import { Book } from '../book';
import { BookCollection } from '../book-collection';
import { BookMark } from './book-mark';
import { BookMarkService } from './book-mark.service';
import { ActivatedRoute, Router, Event, NavigationEnd } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-book-mark',
  templateUrl: './book-mark.component.html',
  styleUrls: ['./book-mark.component.css']
})
export class BookMarkComponent implements OnInit, OnDestroy {
  bookMarks: PageableList<BookMark>;
  navigationSubscription;
  error: string = "";

  constructor(private bookMarkService: BookMarkService, private route: ActivatedRoute, private router: Router, private location: Location) { }

  ngOnInit() {
    this.navigationSubscription = this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        this.load();
      }
    });

    this.load();
  }

  ngOnDestroy() {
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

  load(): void {
    this.bookMarkService.getBookMarkList(1, 25)
    .subscribe(
      bookMarks => {
        this.bookMarks = bookMarks;
      
        this.error = "";
      },
      error => {
        this.error = error.error.code + ": " + error.error.description;
      }
    );
  }

  bookMark_onSelect(bookMark: BookMark) {
    this.router.navigate(['/book'], {
        queryParams: { id: bookMark.book.id, page: bookMark.page }
    });
  }

  bookMarks_onFirstPage() {
    if(this.bookMarks && this.bookMarks.firstPage) {
      this.bookMarkService.getBookMarkList(this.bookMarks.firstPage, this.bookMarks.pageSize)
      .subscribe(
        bookMarks => {
          this.bookMarks = bookMarks;
        
          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }

  bookMarks_onPreviousPage() {
    if(this.bookMarks && this.bookMarks.previousPage) {
      this.bookMarkService.getBookMarkList(this.bookMarks.previousPage, this.bookMarks.pageSize)
      .subscribe(
        bookMarks => {
          this.bookMarks = bookMarks;
        
          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }

  bookMarks_onNextPage() {
    if(this.bookMarks && this.bookMarks.nextPage) {
      this.bookMarkService.getBookMarkList(this.bookMarks.nextPage, this.bookMarks.pageSize)
      .subscribe(
        bookMarks => {
          this.bookMarks = bookMarks;
        
          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }

  bookMarks_onLastPage() {
    if(this.bookMarks && this.bookMarks.lastPage) {
      this.bookMarkService.getBookMarkList(this.bookMarks.lastPage, this.bookMarks.pageSize)
      .subscribe(
        bookMarks => {
          this.bookMarks = bookMarks;
        
          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }

}
