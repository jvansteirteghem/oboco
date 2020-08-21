import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { UserId, AuthenticationService, PageableList } from '../common';


@Component({ templateUrl: 'home.component.html', styleUrls: ['./home.component.css'] })
export class HomeComponent implements OnInit {
    userIdObservable: Observable<UserId>;

    constructor(private authenticationService: AuthenticationService) { }

    ngOnInit() {
        this.userIdObservable = this.authenticationService.getUserIdObservable();
    }
}