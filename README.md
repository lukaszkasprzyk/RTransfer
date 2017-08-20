RTransfer
==========
*Application is using mainly dropwizard.io framework to build quick microservices.

Additionally as an embedded database: h2, testing framework: junit with easymock.
---
Requirements:
java 8, maven
---
How to build it?

execute mvn clean install package.
---
How to start it?

Go to transfertest-webapp directory
ececute 
1. "java -jar target\transfertest-executable.jar db migrate config.yml" - it creates tables and example on embeded database
2. "java -jar target\transfertest-executable.jar start config.yml" - starts server on localhost:8080

Web application starts on http 8080 port with H2 database web manager on 8082.
---
How to use it?
To send transfer it requires to call two restfull services (POST, consumes and produces application/json)
1. transfer/create - responsible for pre validation (if source account balance is fine, source account belongs to specified user, target account exists), reply with transaction id strored on database with fxRate etc..

takes request with body:
{
    "amount": 0.01,
    "sourceAccountNumber": "MMM9876543211MMM9876543211",
    "targetAccountNumber": "RRR1234567890RRR1234567890",
    "creatorUserId": 2,
    "currency": "EUR",
    "reference": "TITLE 1503261467"
}

reply with 
{
    "transactionId": 1,
    "amount": 0.01,
    "sourceAccountNumber": "MMM9876543211MMM9876543211",
    "targetAccountNumber": "RRR1234567890RRR1234567890",
    "creatorUserId": 2,
    "fxRate": 3.33,
    "reference": "TITLE 1503261467",
    "status": "NEW",
    "currency": "EUR"
}
2. transfer/confirm - that service is doing real money transfer from user account A to account B (including validation again)
takes request with body:
{
	"transactionId": 1
}

reply with final transfer and source account balance
{
    "transfer": {
        "transactionId": 2,
        "amount": 0.01,
        "sourceAccountNumber": "MMM9876543211MMM9876543211",
        "targetAccountNumber": "RRR1234567890RRR1234567890",
        "creatorUserId": 2,
        "fxRate": 3.33,
        "reference": "TITLE 1503262591",
        "status": "SUCCESS",
        "currency": "EUR"
    },
    "account": {
        "accountId": 3,
        "balance": 9.98,
        "accountNumber": "MMM9876543211MMM9876543211",
        "userid": 2,
        "currency": "EUR"
    }
}

---
TransferIntegrationTest contains some integration tests, that are able to start real server with h2 database in memory.
Those tests are presenting basic functions of transfer API.
