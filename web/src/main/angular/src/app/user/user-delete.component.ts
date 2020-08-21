import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from './user';
import { UserService } from './user.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-user-delete',
  templateUrl: './user-delete.component.html',
  styleUrls: ['./user-delete.component.css']
})
export class UserDeleteComponent implements OnInit {
  userForm: FormGroup;
  userForm_error = '';
  userForm_submitted = false;
  userForm_loading = false;
  user: User;

  constructor(private formBuilder: FormBuilder, private userService: UserService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
      this.load();
  }

  load(): void {
    this.userForm = this.formBuilder.group({
      name: {value: '', disabled: true},
      password: {value: '', disabled: true},
      roleAdministrator: {value: '', disabled: true},
      roleUser: {value: '', disabled: true}
    });

    let id = +this.route.snapshot.queryParamMap.get('id');

    this.userService.getUser(id)
    .subscribe(user => {
      this.user = user;

      this.userForm.controls.name.setValue(this.user.name);

      if(this.user.roles.includes("ADMINISTRATOR")) {
        this.userForm.controls.roleAdministrator.setValue(true);
      }

      if(this.user.roles.includes("USER")) {
        this.userForm.controls.roleUser.setValue(true);
      }
    });
  }

  userForm_onCancel() {
    this.router.navigate(["/user"]);
  }

  userForm_onDelete() {
    this.userForm_submitted = true;

    // stop here if form is invalid
    if (this.userForm.invalid) {
        return;
    }

    this.userForm_loading = true;

    this.userService.deleteUser(this.user.id)
      .pipe(first())
      .subscribe(
          data => {
            this.userForm_error = "";
            this.userForm_loading = false;

            this.router.navigate(["/user"]);
          },
          error => {
              // response body
              this.userForm_error = error.error.code + ": " + error.error.description;
              this.userForm_loading = false;
          });
  }
}
