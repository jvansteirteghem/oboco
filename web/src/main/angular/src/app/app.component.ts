import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { OnInit } from '@angular/core';

import { UserId, AuthenticationService } from './common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'oboco';
  userId: UserId;
  
  constructor(
        private router: Router,
        private authenticationService: AuthenticationService
    ) { }

    ngOnInit() {
        this.authenticationService.getUserIdObservable().subscribe(userId => this.userId = userId);
    }

    logout() {
        this.authenticationService.logout();
        this.router.navigate(['/login']);
    }
}
