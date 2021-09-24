import { Injectable } from '@angular/core';
import { BookCollection } from './';
import { PageableList } from '../common';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BookCollectionService {
  private url;  // URL to web api

  constructor(private http: HttpClient) { 
    this.url = environment.baseUrl + '/api/v1/';
  }

  getBookCollectionList(parentBookCollectionId: number, page: number, pageSize: number): Observable<PageableList<BookCollection>> {
    return this.http.get<PageableList<BookCollection>>(this.url + "bookCollections/" + parentBookCollectionId + "/bookCollections?page=" + page + "&pageSize=" + pageSize + "&graph=()");
  }

  getBookCollection(id: number): Observable<BookCollection> {
    return this.http.get<BookCollection>(this.url + "bookCollections/" + id + "?graph=(parentBookCollection)");
  }

  getRootBookCollection(): Observable<BookCollection> {
    return this.http.get<BookCollection>(this.url + "bookCollections/ROOT?graph=(parentBookCollection)");
  }
}
