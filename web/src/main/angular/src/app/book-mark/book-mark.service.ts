import { Injectable } from '@angular/core';
import { BookMark } from '../book-mark';
import { PageableList } from '../common';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class BookMarkService {
  private url = '/api/v1/';  // URL to web api

  constructor(private http: HttpClient) { }

  getBookMarkList(page: number, pageSize: number): Observable<PageableList<BookMark>> {
    return this.http.get<PageableList<BookMark>>(this.url + "bookMarks?page=" + page + "&pageSize=" + pageSize + "&graph=(book(bookCollection))");
  }

  getBookMark(bookId: number): Observable<BookMark> {
    return this.http.get<BookMark>(this.url + "books/" + bookId + "/bookMark?graph=()");
  }

  createOrUpdateBookMark(bookId: number, bookMark: BookMark): Observable<BookMark> {
    return this.http.put<BookMark>(this.url + "books/" + bookId + "/bookMark", bookMark);
  }

  deleteBookMark(bookId: number, bookMark: BookMark): Observable<BookMark> {
    return this.http.delete<BookMark>(this.url + "books/" + bookId + "/bookMark");
  }
}
