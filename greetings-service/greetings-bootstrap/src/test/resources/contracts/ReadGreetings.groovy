package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath '/rest/api/v1/greetings/03e805ff-5860-49a6-88bc-a1dcda0dd4b4'
    }
    response {
        status OK()
        headers {
            header 'Content-Type': 'application/json'
        }
        body(
                type: $(producer(regex('ANNIVERSARY|BIRTHDAY|CHRISTMAS')), consumer('ANNIVERSARY')),
                name: $(producer(regex('[a-zA-Z\\s]+')), consumer('Albert Einstein'))
        )
    }
}