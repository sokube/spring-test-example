import {TestBed} from '@angular/core/testing';

import {GreetingsService} from './greetings.service';
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {HttpClient} from "@angular/common/http";
import {pactWith} from "jest-pact";
import {Matchers} from '@pact-foundation/pact';

pactWith({consumer: 'greetings-ui', provider: 'greetings-service'}, provider => {
  describe('GreetingsService', () => {
    let service: GreetingsService;
    let httpClient: HttpClient;
    let httpTestingController: HttpTestingController;
    let baseUrl: string;


    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });

      httpClient = TestBed.inject(HttpClient);
      httpTestingController = TestBed.inject(HttpTestingController);
      baseUrl = provider.mockService.baseUrl;
      service = new GreetingsService(httpClient, baseUrl);
    });

    describe('createGreetings', () => {
      let id: string = '0f85c795-ce76-4f6f-8d0a-dc78ab7e319a'

      beforeEach(() => {
        provider.addInteraction({
          state: '',
          uponReceiving: 'A greeting creation request',
          withRequest: {
            method: 'POST',
            path: '/rest/api/v1/greetings',
            body: {
              name: Matchers.string('Max Planck'),
              type: Matchers.term({
                generate: 'Christmas',
                matcher: 'Christmas|Anniversary|Birthday'
              })
            }
          },
          willRespondWith: {
            status: 201,
            headers: {
              Location: Matchers.term({
                generate: baseUrl + '/rest/api/v1/greetings/' + id,
                matcher: '.+/rest/api/v1/greetings/[a-z0-9]{8}(-[a-z0-9]{4}){3}-[a-z0-9]{12}'
              })
            },
            body: {
              message: Matchers.string("Merry Christmas Max Planck !")
            }
          }
        })
      });

      test('should answer 201', (done) => {
        const name = 'Albert Einstein';
        const type = 'Birthday';
        service.createGreetings(name, type)
          .subscribe(greeting => {
            expect(greeting).toBeTruthy();
            expect(greeting.message).toEqual([expect.stringContaining(name), expect.stringContaining(type)]);
            expect(greeting.id).toBeTruthy()
            done();
          })
      }, 30000);
    });
  });
});
