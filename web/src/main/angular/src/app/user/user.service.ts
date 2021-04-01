import { Injectable } from '@angular/core';
import { User } from './user';
import { UserPassword } from './user-password';
import { BookCollection } from '../book-collection/book-collection';
import { PageableList } from '../common';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private url;  // URL to web api

  constructor(private http: HttpClient) { 
    this.url = environment.baseUrl + '/api/v1/';
  }

  getAuthenticatedUser(): Observable<User> {
    return this.http.get<User>(this.url + "users/ME?graph=(rootBookCollection)");
  }

  updateAuthenticatedUserPassword(userPassword: UserPassword): Observable<User> {
    return this.http.patch<User>(this.url + "users/ME/password", userPassword);
  }

  getUserList(page: number, pageSize: number): Observable<PageableList<User>> {
    return this.http.get<PageableList<User>>(this.url + "users?page=" + page + "&pageSize=" + pageSize + "&graph=()");
  }

  getUser(id: number): Observable<User> {
    return this.http.get<User>(this.url + "users/" + id + "?graph=(rootBookCollection)");
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.url + "users", user);
  }

  updateUser(user: User): Observable<User> {
    return this.http.put<User>(this.url + "users/" + user.id, user);
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete<User>(this.url + "users/" + id);
  }

  getRootBookCollectionList(): Observable<BookCollection[]> {
    return this.http.get<BookCollection[]>(this.url + "users/bookCollections?graph=()");
  }
}
