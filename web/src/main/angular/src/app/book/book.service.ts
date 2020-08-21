import { Injectable } from '@angular/core';
import { Book } from './';
import { PageableList } from '../common';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private url = '/api/v1/';  // URL to web api

  constructor(private http: HttpClient) { }

  getBookList(bookCollectionId: number, page: number, pageSize: number): Observable<PageableList<Book>> {
    return this.http.get<PageableList<Book>>(this.url + "bookCollections/" + bookCollectionId + "/books?page=" + page + "&pageSize=" + pageSize + "&graph=()");
  }

  getBook(id: number): Observable<Book> {
    return this.http.get<Book>(this.url + "books/" + id + "?graph=(bookCollection,bookMark)");
  }
}
