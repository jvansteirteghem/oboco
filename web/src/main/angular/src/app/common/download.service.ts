import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, Subscriber } from 'rxjs';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
  })
export class DownloadService {
    constructor(
        private http: HttpClient, private sanitizer: DomSanitizer
    ) {
    }

    downloadAsSafeUrl(url: string): Observable<SafeUrl> {
        url = environment.baseUrl + url;
        return new Observable((observer: Subscriber<SafeUrl>) => {
            let objectUrl: string = null;

            this.http
                .get(url, {
                    responseType: 'blob'
                })
                .subscribe(m => {
                    objectUrl = URL.createObjectURL(m);
                    observer.next(this.sanitizer.bypassSecurityTrustUrl(objectUrl));
                });

            return () => {
                if (objectUrl) {
                    URL.revokeObjectURL(objectUrl);
                    objectUrl = null;
                }
            };
        });
    }

    download(url: string, name: string) {
        url = environment.baseUrl + url;
        this.http
            .get(url, {
                responseType: 'blob'
            })
            .subscribe(m => {
                let objectUrl: string = null;
                objectUrl = URL.createObjectURL(m);

                let a = document.createElement('a');
                a.href = objectUrl;
                a.download = name;
                a.click();

                if (objectUrl) {
                    URL.revokeObjectURL(objectUrl);
                    objectUrl = null;
                }
            });
    }
}