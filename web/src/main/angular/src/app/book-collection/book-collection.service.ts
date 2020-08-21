import { Injectable } from '@angular/core';
import { BookCollection } from './';
import { PageableList } from '../common';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class BookCollectionService {
  private url = '/api/v1/';  // URL to web api

  constructor(private http: HttpClient) { }

  getBookCollectionList(parentBookCollectionId: number, page: number, pageSize: number): Observable<PageableList<BookCollection>> {
    return this.http.get<PageableList<BookCollection>>(this.url + "bookCollections?parentBookCollectionId=" + parentBookCollectionId + "&page=" + page + "&pageSize=" + pageSize + "&graph=()");
  }

  getBookCollection(id: number): Observable<BookCollection> {
    return this.http.get<BookCollection>(this.url + "bookCollections/" + id + "?graph=(parentBookCollection)");
  }

  getRootBookCollection(): Observable<BookCollection> {
    return this.http.get<BookCollection>(this.url + "bookCollections/ROOT?graph=(parentBookCollection)");
  }
}
