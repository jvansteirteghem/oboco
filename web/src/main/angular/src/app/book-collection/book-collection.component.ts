import { Component, OnInit, OnDestroy } from '@angular/core';
import { Book, BookService } from '../book';
import { PageableList, DownloadService } from '../common';
import { BookCollection } from './book-collection';
import { BookCollectionService } from './book-collection.service';
import { ActivatedRoute, Router, Event, NavigationEnd } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-files',
  templateUrl: './book-collection.component.html',
  styleUrls: ['./book-collection.component.css']
})
export class BookCollectionComponent implements OnInit, OnDestroy {
  bookCollection: BookCollection;
  parentBookCollection: BookCollection;
  bookCollections: PageableList<BookCollection>;
  books: PageableList<Book>;
  navigationSubscription;
  error: string = "";

  constructor(private bookCollectionService: BookCollectionService, private bookService: BookService, private route: ActivatedRoute, private router: Router, private location: Location, private downloadService: DownloadService) { 
    
  }

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
    let bookCollectionId = null;
    if(this.route.snapshot.queryParamMap.has('bookCollectionId')) {
      bookCollectionId = +this.route.snapshot.queryParamMap.get('bookCollectionId');
    }

    if(bookCollectionId === null) {
      this.bookCollectionService.getRootBookCollection()
      .subscribe(
        bookCollection => {
          this.bookCollection = bookCollection;
          this.parentBookCollection = null;
          this.bookCollections = null;
          this.books = null;

          if(bookCollection) {
            this.parentBookCollection = bookCollection.parentBookCollection;

            this.bookCollectionService.getBookCollectionList(bookCollection.id, 1, 25)
            .subscribe(
              bookCollections => {
                this.bookCollections = bookCollections;

                this.error = "";
              },
              error => {
                this.error = error.error.code + ": " + error.error.description;
              }
            );

            this.bookService.getBookList(bookCollection.id, 1, 25)
            .subscribe(
              books => {
                this.books = books;

                this.error = "";
              },
              error => {
                this.error = error.error.code + ": " + error.error.description;
              }
            );
          }

          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    } else {
      this.bookCollectionService.getBookCollection(bookCollectionId)
      .subscribe(
        bookCollection => {
          this.bookCollection = bookCollection;
          this.parentBookCollection = null;
          this.bookCollections = null;
          this.books = null;

          if(bookCollection) {
            this.parentBookCollection = bookCollection.parentBookCollection;

            this.bookCollectionService.getBookCollectionList(bookCollection.id, 1, 25)
            .subscribe(
              bookCollections => {
                this.bookCollections = bookCollections;

                this.error = "";
              },
              error => {
                this.error = error.error.code + ": " + error.error.description;
              }
            );

            this.bookService.getBookList(bookCollection.id, 1, 25)
            .subscribe(
              books => {
                this.books = books;

                this.error = "";
              },
              error => {
                this.error = error.error.code + ": " + error.error.description;
              }
            );
          }

          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }

  book_onSelect(book: Book) {
    this.router.navigate(['/book'], {
        queryParams: { id: book.id }
    });
  }

  book_onDownload(book: Book) {
    this.downloadService.download('/api/v1/books/' + book.id + '.cbz', book.name + '.cbz');
  }

  books_onFirstPage() {
    if(this.books && this.books.firstPage) {
      this.bookService.getBookList(this.bookCollection.id, this.books.firstPage, this.bookCollections.pageSize)
      .subscribe(
        bookList => {
          this.books = bookList;
        
          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }

  books_onPreviousPage() {
    if(this.books && this.books.previousPage) {
      this.bookService.getBookList(this.bookCollection.id, this.books.previousPage, this.bookCollections.pageSize)
      .subscribe(
        bookList => {
          this.books = bookList;
        
          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }

  books_onNextPage() {
    if(this.books && this.books.nextPage) {
      this.bookService.getBookList(this.bookCollection.id, this.books.nextPage, this.bookCollections.pageSize)
      .subscribe(
        bookList => {
          this.books = bookList;
        
          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }

  books_onLastPage() {
    if(this.books && this.books.lastPage) {
      this.bookService.getBookList(this.bookCollection.id, this.books.lastPage, this.bookCollections.pageSize)
      .subscribe(
        bookList => {
          this.books = bookList;
        
          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }
  
  bookCollection_onSelect(bookCollection: BookCollection) {
    this.router.navigate(['/bookCollection'], {
      queryParams: { bookCollectionId: bookCollection.id }
    });
  }

  bookCollections_onFirstPage() {
    if(this.bookCollections && this.bookCollections.firstPage) {
      this.bookCollectionService.getBookCollectionList(this.bookCollection.id, this.bookCollections.firstPage, this.bookCollections.pageSize)
      .subscribe(
        bookCollectionList => {
          this.bookCollections = bookCollectionList;

          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }

  bookCollections_onPreviousPage() {
    if(this.bookCollections && this.bookCollections.previousPage) {
      this.bookCollectionService.getBookCollectionList(this.bookCollection.id, this.bookCollections.previousPage, this.bookCollections.pageSize)
      .subscribe(
        bookCollectionList => {
          this.bookCollections = bookCollectionList;

          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }

  bookCollections_onNextPage() {
    if(this.bookCollections && this.bookCollections.nextPage) {
      this.bookCollectionService.getBookCollectionList(this.bookCollection.id, this.bookCollections.nextPage, this.bookCollections.pageSize)
      .subscribe(
        bookCollectionList => {
          this.bookCollections = bookCollectionList;

          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }

  bookCollections_onLastPage() {
    if(this.bookCollections && this.bookCollections.lastPage) {
      this.bookCollectionService.getBookCollectionList(this.bookCollection.id, this.bookCollections.lastPage, this.bookCollections.pageSize)
      .subscribe(
        bookCollectionList => {
          this.bookCollections = bookCollectionList;

          this.error = "";
        },
        error => {
          this.error = error.error.code + ": " + error.error.description;
        }
      );
    }
  }
}
