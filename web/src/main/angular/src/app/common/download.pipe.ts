import { Pipe, PipeTransform } from '@angular/core';
import { SafeUrl } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DownloadService } from './download.service';

// chrome://blob-internals/
// <img [attr.src]="url | download | async"/>
// https://stackoverflow.com/questions/41372332/get-http-localhost4200-null-404-not-found-in-angular2-with-angular-cli
@Pipe({
  name: 'download',
})
export class DownloadPipe implements PipeTransform {
  constructor(private downloadService: DownloadService) { }

    transform(url): Observable<SafeUrl> {
      return this.downloadService.downloadAsSafeUrl(url);
    }
}