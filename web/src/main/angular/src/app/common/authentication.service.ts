import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, tap, catchError, finalize } from 'rxjs/operators';
import { UserId } from './user-id';
import { UserNamePassword } from './user-name-password';
import { UserToken } from './user-token';
import { environment } from '../../environments/environment';


@Injectable({ providedIn: 'root' })
export class AuthenticationService {
    private url;  // URL to web api
    private userIdSubject: BehaviorSubject<UserId>;

    constructor(private http: HttpClient) { 
        this.url = environment.baseUrl + '/api/v1/';
        this.userIdSubject = new BehaviorSubject<UserId>(JSON.parse(localStorage.getItem("userId")));
    }

    getUserId(): UserId {
        return this.userIdSubject.value;
    }

    getUserIdObservable():  Observable<UserId> {
        return this.userIdSubject.asObservable();
    }

    login(name: string, password: string) {
        let userNamePassword: UserNamePassword = new UserNamePassword();
        userNamePassword.name = name;
        userNamePassword.password = password;

        return this.http.post<any>(this.url + "authentication", userNamePassword)
            .pipe(map(userId => {
                // store user details and jwt token in local storage to keep user logged in between page refreshes
                localStorage.setItem("userId", JSON.stringify(userId));
                this.userIdSubject.next(userId);

                return userId;
            }));
    }

    refresh() {
        let userToken: UserToken = new UserToken();
        let userId: UserId = this.getUserId();

        if(userId) {
            userToken.token = userId.refreshToken;
        }

        return this.http.post<any>(this.url + "authentication/refresh", userToken)
        .pipe(tap(
                userId => {
                    // store user details and jwt token in local storage to keep user logged in between page refreshes
                    localStorage.setItem("userId", JSON.stringify(userId));
                    this.userIdSubject.next(userId);
                    return userId;
                })
        )
    }

    logout() {
        // remove user from local storage to log user out
        localStorage.removeItem("userId");
        this.userIdSubject.next(null);
    }
}