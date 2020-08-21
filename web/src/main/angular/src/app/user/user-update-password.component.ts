import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from './user';
import { UserPassword } from './user-password';
import { UserService } from './user.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-update-password',
  templateUrl: './user-update-password.component.html',
  styleUrls: ['./user-update-password.component.css']
})
export class UserUpdatePasswordComponent implements OnInit {
  authenticatedUserForm: FormGroup;
  authenticatedUserForm_error = '';
  authenticatedUserForm_submitted = false;
  authenticatedUserForm_loading = false;

  constructor(private formBuilder: FormBuilder, private userService: UserService, private router: Router) { }

  ngOnInit() {
      this.load();
  }

  load(): void {
    this.authenticatedUserForm = this.formBuilder.group({
      password: ['', Validators.required],
      updatePassword: ['', Validators.required]
    });
  }

  authenticatedUserForm_onCancel() {
    this.router.navigate(["/user"]);
  }

  authenticatedUserForm_onUpdatePassword() {
    this.authenticatedUserForm_submitted = true;

    // stop here if form is invalid
    if (this.authenticatedUserForm.invalid) {
        return;
    }

    this.authenticatedUserForm_loading = true;

    let userPassword: UserPassword = new UserPassword();
    userPassword.password = this.authenticatedUserForm.controls.password.value;
    userPassword.updatePassword = this.authenticatedUserForm.controls.updatePassword.value;

    this.userService.updateAuthenticatedUserPassword(userPassword)
      .pipe(first())
      .subscribe(
          data => {
            this.authenticatedUserForm_error = "";
            this.authenticatedUserForm_loading = false;

            this.router.navigate(["/user"]);
          },
          error => {
              // response body
              this.authenticatedUserForm_error = error.error.code + ": " + error.error.description;
              this.authenticatedUserForm_loading = false;
          });
  }
}
