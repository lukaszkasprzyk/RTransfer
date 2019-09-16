# RTransfer

TESTEDui
==========

Application is using mainly dropwizard.io framework to build quick microservices. Read more at [dropwizard.io](http://www.dropwizard.io).
Additionally as an embedded database: h2, testing framework: junit with easymock.
---
Requirements:
java 8, maven
---
* How to build it?
execute mvn clean install package.

* How to start application?
Application could be started as standalone java program with follwing instruction:
Go to transfertest-webapp directory and ececute following two commands

it creates tables and example on embeded database
```
	java -jar target\transfertest-executable.jar db migrate config.yml

```
starts server on localhost:8080
```
	java -jar target\transfertest-executable.jar start config.yml

```
Web application starts on http 8080 port with H2 database web manager on 8082.

You can also download builed application as archive from  https://drive.google.com/file/d/0B8RyLSfr0PWda1E2Q2dPRnR5UDQ/view?usp=sharing
and start it with command:  "java -jar transfertest-executable.jar start config.yml"


Integration tests
==========
It's possibility to test it with integration tests (TransferIntegrationTest), that are executed on real application as well, but it stats it's own application.

TransferIntegrationTest contains some integration tests, that are able to start real server with h2 database in memory. 
Those tests are independent with application started by user, but may have conflicts in http ports when running paralelly.
Those tests are presenting basic functions of transfer API.

API Description
==========

How to use it?
To send transfer it requires to call two restfull services (POST, consumes and produces application/json)
		
	** http://localhost:8080/transfer/create - responsible for pre validation 
	(if source account balance is fine, source account belongs to specified user, target account exists),
	 reply with transaction id strored on database with fxRate etc..


		takes request with body:
		{
		    "amount": 0.01,
		    "sourceAccountNumber": "MMM9876543211MMM9876543211",
		    "targetAccountNumber": "RRR1234567890RRR1234567890",
		    "creatorUserId": 2,
		    "currency": "EUR",
		    "reference": "TITLE 1503261467"
		}
		
		reply with :
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
	** http://localhost:8080/transfer/confirm - that service is doing real money transfer from user account A to account B (including validation again)
		takes request with body:
		{
			"transactionId": 1 -id from reply object of create service from transactionId field
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

