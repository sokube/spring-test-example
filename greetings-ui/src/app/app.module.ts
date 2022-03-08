import { InjectionToken, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';

export const APIURL = new InjectionToken<string>('The base API URL');

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, HttpClientModule],
  providers: [
    {
      provide: APIURL,
      useValue: 'http://localhost:8080',
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
