import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, take, switchMap, finalize } from 'rxjs/operators';
import { UserId } from './user-id';
import { AuthenticationService } from './authentication.service';
import { Router } from '@angular/router';

@Injectable()
export class UserTokenInterceptor implements HttpInterceptor {
  private refresh = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(public authenticationService: AuthenticationService, private router: Router) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let userId = this.authenticationService.getUserId();

    if(userId) {
        request = this.addToken(request, userId);
    }

    return next.handle(request).pipe(catchError(error => {
      if(error instanceof HttpErrorResponse && error.status === 401) {
        return this.handle401Error(request, next, error);
      } else {
        return this.handleError(request, next, error);
      }
    }));
  }

  private addToken(request: HttpRequest<any>, userId: UserId) {
    return request.clone({
      setHeaders: {
        'Authorization': `Bearer ${userId.idToken}`
      }
    });
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler, error) {
    if(this.refresh) {
      return this.refreshTokenSubject.pipe(
        filter(userId => userId != null),
        take(1),
        switchMap(userId => {
          return next.handle(this.addToken(request, userId));
        }));
    } else {
      this.refresh = true;
      this.refreshTokenSubject.next(null);

      return this.authenticationService.refresh().pipe(
        switchMap((userId: any) => {
          this.refresh = false;
          this.refreshTokenSubject.next(userId);
          return next.handle(this.addToken(request, userId));
        })
      );
    }
  }

  private handleError(request: HttpRequest<any>, next: HttpHandler, error) {
    if(this.refresh) {
      this.refresh = false;
      this.refreshTokenSubject.next(null);

      this.authenticationService.logout();

      this.router.navigate(["/login"]);
    }

    return throwError(error);
  }
}
