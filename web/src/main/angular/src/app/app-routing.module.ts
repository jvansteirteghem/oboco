import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BookComponent } from './book';
import { BookCollectionComponent } from './book-collection';
import { BookMarkComponent } from './book-mark';
import { BookScannerComponent } from './book-scanner';
import { UserComponent, UserCreateComponent, UserUpdateComponent, UserUpdatePasswordComponent, UserDeleteComponent } from './user';

import { HomeComponent } from './home';
import { LoginComponent } from './login';
import { AuthenticationGuard } from './common';

const routes: Routes = [
  { path: 'book', component: BookComponent, canActivate: [AuthenticationGuard] },
  { path: 'bookCollection', component: BookCollectionComponent, canActivate: [AuthenticationGuard] },
  { path: 'bookMark', component: BookMarkComponent, canActivate: [AuthenticationGuard] },
  { path: 'bookScanner', component: BookScannerComponent, canActivate: [AuthenticationGuard] },
  { path: 'user', component: UserComponent, canActivate: [AuthenticationGuard] },
  { path: 'userCreate', component: UserCreateComponent, canActivate: [AuthenticationGuard] },
  { path: 'userUpdate', component: UserUpdateComponent, canActivate: [AuthenticationGuard] },
  { path: 'userUpdatePassword', component: UserUpdatePasswordComponent, canActivate: [AuthenticationGuard] },
  { path: 'userDelete', component: UserDeleteComponent, canActivate: [AuthenticationGuard] },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'enabled', onSameUrlNavigation: 'reload', relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }