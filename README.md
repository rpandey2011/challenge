# challenge

Application is a simple example of Banking Account Service and Transfers

USAGE:

AccountService is used for Managing the user account details.

TransactionService is used for the fund transfer facilities between 2 accounts. 

TEST:

Following are the steps and sample JSON Request to test the application

1. Run the application (Application will start on 18080 port).

2. Add sample accounts using following services (Format <METHOD>: <URL>)


	POST: http://localhost:18080/v1/accounts/

	Request Body1: 
	{
		"accountId": "ACCT00001",
		"balance": 1000
	}

	Request Body 2: 
	{
		"accountId": "ACCT00002",
		"balance": 0
	}

	Request Body 3: 
	{
		"accountId": "ACCT00003",
		"balance": 100
	}

3. Use following service to transfer the funds between two Accounts

	POST: http://localhost:18080/v1/transfers

	Request Body 1: 
	{
		"sourceAccountId": "ACCT00001",
		"destinationAccountId": "ACCT00003",
		"amount": 150
	}

	Result: 

	Check balance ACCT00001

	GET: http://localhost:18080/v1/accounts/ACCT00001

	{
	    "accountId": "ACCT00001",
	    "balance": 850
	}

	Check balance ACCT00003

	GET: http://localhost:18080/v1/accounts/ACCT00003

	{
	    "accountId": "ACCT00003",
	    "balance": 250
	}

	Request Body 2:
	{
		"sourceAccountId": "ACCT00002",
		"destinationAccountId": "ACCT00003",
		"amount": 150
	}

	Result:

	Account transferring from does not exists.


	Request Body 3: 
	{
		"sourceAccountId": "ACCT00004",
		"destinationAccountId": "ACCT00003",
		"amount": 150
	}

	Result: 

	Account transferring from does not exists.
