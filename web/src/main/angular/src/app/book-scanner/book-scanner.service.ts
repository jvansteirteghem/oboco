import { Injectable } from '@angular/core';
import { PageableList } from '../common';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { BookScanner } from './book-scanner';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BookScannerService {
  private url;  // URL to web api

  constructor(private http: HttpClient) { 
    this.url = environment.baseUrl + '/api/v1/';
  }

  startBookScanner(id: string, mode: string): Observable<any> {
    return this.http.post(this.url + "bookScanners/" + id + "/start?mode=" + mode, {});
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
