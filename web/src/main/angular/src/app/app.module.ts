import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';

import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS }    from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list'; 
import { MatCheckboxModule } from '@angular/material/checkbox';

import { BookComponent } from './book';
import { BookCollectionComponent } from './book-collection';

import { HomeComponent } from './home';
import { LoginComponent } from './login';

import { DownloadPipe, UserTokenInterceptor } from './common';
import { BookMarkComponent } from './book-mark';
import { BookScannerComponent } from './book-scanner';
import { UserComponent, UserCreateComponent, UserUpdateComponent, UserUpdatePasswordComponent, UserDeleteComponent } from './user';


@NgModule({
  declarations: [
    AppComponent,
    BookCollectionComponent,
    BookComponent,
    HomeComponent,
    LoginComponent,
    DownloadPipe,
    BookMarkComponent,
    BookScannerComponent,
    UserComponent,
    UserCreateComponent,
    UserUpdateComponent,
    UserUpdatePasswordComponent,
    UserDeleteComponent

  ],
  imports: [
    HttpClientModule,
    BrowserModule,
	ReactiveFormsModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatCardModule,
    MatButtonModule,
    MatGridListModule,
    MatIconModule,
    MatToolbarModule,
    MatInputModule,
    MatListModule,
    MatCheckboxModule
  ],
  providers: [
  		{ provide: LocationStrategy, useClass: HashLocationStrategy },
        { provide: HTTP_INTERCEPTORS, useClass: UserTokenInterceptor, multi: true }
    ],
  bootstrap: [AppComponent]
})
export class AppModule { }
