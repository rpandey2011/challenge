package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transactions;
import com.db.awmd.challenge.exception.AccountDoesNotExistsException;
import com.db.awmd.challenge.exception.BusinessException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.TechnicalException;
import com.db.awmd.challenge.utils.Utility;

@Service
public class TransactionService {

	@Autowired
	private AccountsService accountService;

	@Autowired
	private EmailNotificationService emailNotificationService;

	private static Logger log = LoggerFactory.getLogger(TransactionService.class);

	public synchronized void transferFunds(final Transactions transaction)
			throws AccountDoesNotExistsException, InsufficientBalanceException, TechnicalException {

		if (transaction.getSourceAccountId().equals(transaction.getDestinationAccountId())) {
			throw new BusinessException("From and To Account connot be same.");
		}

		Account sourceAccount = accountService.getAccount(transaction.getSourceAccountId());
		Account destinationAccount = accountService.getAccount(transaction.getDestinationAccountId());

		if (sourceAccount == null) {
			throw new AccountDoesNotExistsException("Account transferring from does not exists.");
		}

		if (destinationAccount == null) {
			throw new AccountDoesNotExistsException("Account transferring to does not exisits.");
		}

		final BigDecimal sourceBalance = sourceAccount.getBalance();
		final BigDecimal destinationBalance = destinationAccount.getBalance();

		if (Utility.validateAmount(sourceAccount.getBalance(), transaction.getAmount())) {
			sourceAccount.setBalance(sourceAccount.getBalance().subtract(transaction.getAmount()));
			destinationAccount.setBalance(destinationAccount.getBalance().add(transaction.getAmount()));

			try {
				// Update the account detail to Account
				accountService.updateAccount(sourceAccount);
				accountService.updateAccount(destinationAccount);
			} catch (Exception e) {
				sourceAccount.setBalance(sourceBalance);
				destinationAccount.setBalance(destinationBalance);

				try {
					// Roll-back the account to the original value
					accountService.updateAccount(sourceAccount);
					accountService.updateAccount(destinationAccount);
				} catch (Exception e1) {
					
					Runnable notificationThread = () -> {
						emailNotificationService.notifyAboutTransfer(sourceAccount, "Something went wrong, please contact to administrator.");
					};
					notificationThread.run();
					log.error("Error while reverting balance. Following are the transaction details: " + transaction.toString(), e1);
					throw new TechnicalException("Something went wrong, please contact to administrator.");
				}

				throw new TechnicalException("Techinical Error occurred, please try after sometime.");
			}

			try {
				Runnable notificationThread = () -> {
					emailNotificationService.notifyAboutTransfer(sourceAccount, "Amount " + transaction.getAmount()
							+ " debitted from your account. Updated balance is " + sourceAccount.getBalance());
					emailNotificationService.notifyAboutTransfer(destinationAccount, "Amount " + transaction.getAmount()
							+ " creditted from your account. Updated balance is " + destinationAccount.getBalance());
				};
				notificationThread.run();
			} catch (Exception ex) {
				log.error("Exception while sending notification message", ex);
			}
		} else {
			throw new InsufficientBalanceException("Unable to transfer the amount due to insuffiecient balance.");
		}
	}
}
