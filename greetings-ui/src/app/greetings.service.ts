import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { APIURL } from './app.module';

export interface Greeting {
  message: string;
  id: string;
}

@Injectable({
  providedIn: 'root',
})
export class GreetingsService {
  constructor(
    private http: HttpClient,
    @Inject(APIURL) private apiurl: String
  ) {}

  // TODO put type in a enumish class and put it in an interface describing object
  createGreetings(name: string, type: string): Observable<Greeting> {
    console.log('post for' + name + type);
    return this.http
      .post<Greeting>(
        `${this.apiurl}/rest/api/v1/greetings`,
        {
          name,
          type,
        },
        { observe: 'response' }
      )
      .pipe(
        // TODO handle error
        map((response) => {
          if (!response) {
            throw new Error('Response is empty !');
          } else if (!response.body) {
            throw new Error('Wrong body type !');
          }
          return {
            message: response.body.message,
            // FIXME extract id from location header
            id: '',
          };
        })
      );
  }
}
