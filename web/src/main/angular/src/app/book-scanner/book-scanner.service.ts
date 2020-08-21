import { Injectable } from '@angular/core';
import { PageableList } from '../common';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { BookScanner } from './book-scanner';

@Injectable({
  providedIn: 'root'
})
export class BookScannerService {
  private url = '/api/v1/';  // URL to web api

  constructor(private http: HttpClient) { }

  startBookScanner(id: string): Observable<any> {
    return this.http.post(this.url + "bookScanners/" + id + "/start", {});
  }

  stopBookScanner(id: string): Observable<any> {
    return this.http.post(this.url + "bookScanners/" + id + "/stop", {});
  }

  getBookScanner(id: string): Observable<BookScanner> {
    return this.http.get<BookScanner>(this.url + "bookScanners/" + id);
  }

  getBookScannerList(): Observable<BookScanner[]> {
    return this.http.get<BookScanner[]>(this.url + "bookScanners");
  }
}
