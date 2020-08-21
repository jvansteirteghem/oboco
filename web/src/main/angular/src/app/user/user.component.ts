import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthenticationService, UserId, PageableList } from '../common';
import { User } from './user';
import { UserService } from './user.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  userId: UserId;
  users: PageableList<User>;

  constructor(private formBuilder: FormBuilder, private authenticationService: AuthenticationService, private userService: UserService, private router: Router) { }

  ngOnInit() {
      this.load();
  }

  load(): void {
    this.userId = this.authenticationService.getUserId();

    if(this.userId && this.userId.roles.includes("ADMINISTRATOR")) {
      this.userService.getUserList(1, 25)
      .subscribe(users => {
        this.users = users;
      });
    }
  }

  user_updatePassword() {
    this.router.navigate(["/userUpdatePassword"]);
  }

  user_create() {
    this.router.navigate(["/userCreate"]);
  }

  user_update(user: User) {
    this.router.navigate(["/userUpdate"], {
      queryParams: { id: user.id }
    });
  }

  user_delete(user: User) {
    this.router.navigate(["/userDelete"], {
      queryParams: { id: user.id }
    });
  }

  users_onFirstPage() {
    if(this.users && this.users.firstPage) {
      this.userService.getUserList(this.users.firstPage, this.users.pageSize)
      .subscribe(users => {
        this.users = users;
      });
    }
  }

  users_onPreviousPage() {
    if(this.users && this.users.previousPage) {
      this.userService.getUserList(this.users.previousPage, this.users.pageSize)
      .subscribe(users => {
        this.users = users;
      });
    }
  }

  users_onNextPage() {
    if(this.users && this.users.nextPage) {
      this.userService.getUserList(this.users.nextPage, this.users.pageSize)
      .subscribe(users => {
        this.users = users;
      });
    }
  }

  users_onLastPage() {
    if(this.users && this.users.lastPage) {
      this.userService.getUserList(this.users.lastPage, this.users.pageSize)
      .subscribe(users => {
        this.users = users;
      });
    }
  }
}
