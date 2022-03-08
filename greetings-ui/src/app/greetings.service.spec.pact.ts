import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Matchers, Pact } from '@pact-foundation/pact';
import * as path from 'path';
import { firstValueFrom } from 'rxjs';
import { APIURL } from './app.module';
import { Greeting, GreetingsService } from './greetings.service';

describe('GreetingsService', () => {
  const provider: Pact = new Pact({
    port: 54_546,
    log: path.resolve(process.cwd(), 'pacts', 'logs', 'pact.log'),
    dir: path.resolve(process.cwd(), 'pacts'),
    spec: 3,
    logLevel: 'info',
    consumer: 'greetings-ui',
    provider: 'greetings-service',
  });

  beforeAll(async () => {
    await provider.setup();
  });

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [GreetingsService, { provide: APIURL, useValue: '' }],
    });
  });

  afterEach(async () => {
    await provider.verify();
  });

  afterAll(async () => {
    await provider.finalize();
  });

  describe('createGreetings', () => {
    const id: string = '0f85c795-ce76-4f6f-8d0a-dc78ab7e319a';
    const greetingMessage: string = 'Merry Christmas Max Planck !';

    beforeEach(async () => {
      await provider.addInteraction({
        state: '',
        uponReceiving: 'A greeting creation request',
        withRequest: {
          method: 'POST',
          path: '/rest/api/v1/greetings',
          body: {
            name: Matchers.string('Max Planck'),
            type: Matchers.term({
              generate: 'Christmas',
              matcher: '^Christmas$|^Anniversary$|^Birthday$',
            }),
          },
        },
        willRespondWith: {
          status: 201,
          headers: {
            Location: Matchers.term({
              generate: `/rest/api/v1/greetings/${id}`,
              matcher:
                '/rest/api/v1/greetings/[a-z0-9]{8}(-[a-z0-9]{4}){3}-[a-z0-9]{12}',
            }),
          },
          body: {
            message: Matchers.string(greetingMessage),
          },
        },
      });
    });

    test('should answer 201', async () => {
      const name = 'Albert Einstein';
      const type = 'Birthday';

      const greetingsService: GreetingsService =
        TestBed.inject(GreetingsService);

      const greeting: Greeting = await firstValueFrom(
        greetingsService.createGreetings(name, type)
      );

      expect(greeting).toBeTruthy();
      // TODO: Uncomment next line when id retrieved from the Location header in the greetings service
      // expect(greeting.id).toBeTruthy();
      expect(greeting.message).toBe(greetingMessage);
    });
  });
});
