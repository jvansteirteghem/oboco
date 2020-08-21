import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { UserId, AuthenticationService } from './common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'oboco';
  userId: UserId;
  
  constructor(
        private router: Router,
        private authenticationService: AuthenticationService
    ) {
        this.authenticationService.getUserIdObservable().subscribe(userId => this.userId = userId);
    }

    logout() {
        this.authenticationService.logout();
        this.router.navigate(['/login']);
    }
}
