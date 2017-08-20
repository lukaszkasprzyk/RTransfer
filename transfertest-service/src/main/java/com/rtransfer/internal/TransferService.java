package com.rtransfer.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.skife.jdbi.v2.sqlobject.Transaction;

import com.rtransfer.api.Account;
import com.rtransfer.api.Transfer;
import com.rtransfer.api.exception.ErrorCode;
import com.rtransfer.api.exception.ValidationException;

public class TransferService {

	public TransfersDAO transferDAO;

	public AccountsDAO accountsDAO;

	private BigDecimal DUMMY_RATE = new BigDecimal(3.33).setScale(2, RoundingMode.DOWN);

	@Transaction
	public Transfer forecast(Transfer transfer) {

		boolean valid = businessValidation(transfer);
		Integer transferId = null;
		if (valid) {
			calculateRate(transfer);
			transferId = transferDAO.insertTransfer(transfer);
			transfer = transferDAO.getTransferById(BigInteger.valueOf(transferId.longValue()));
		} else {
			throw new ValidationException(ErrorCode.VALIDATION_FAILED);
		}
		return transfer;
	}

	private void calculateRate(Transfer transfer) {
		transfer.setFxRate(DUMMY_RATE);
	}

	@Transaction
	public Transfer process(BigInteger id) {
		Transfer storedTransfer = transferDAO.getTransferById(id);
		if (storedTransfer.getStatus().equals("NEW")) {
			if (businessValidation(storedTransfer)) {

				transferDAO.updateTransferStatus(storedTransfer.getTransactionId(), "SUCCESS");
				Account accountToCharge = findSourceAccount(storedTransfer.getCreatorUserId(),
						storedTransfer.getSourceAccountNumber()).get();
				accountsDAO.updateBalance(accountToCharge.getAccountId(), storedTransfer.getAmount().negate());
				Account accountToCredit = accountsDAO.getAccountByNumber(storedTransfer.getTargetAccountNumber());
				accountsDAO.updateBalance(accountToCredit.getAccountId(), storedTransfer.getAmount());
			} else {
				transferDAO.updateTransferStatus(storedTransfer.getTransactionId(), "FAILED");
			}
		} else {
			throw new ValidationException(ErrorCode.ALREADY_PROCESSED);
		}
		return transferDAO.getTransferById(id);
	}

	private boolean businessValidation(Transfer transfer) {
		boolean result = false;
		Optional<Account> sourceAccount = findSourceAccount(transfer.getCreatorUserId(),
				transfer.getSourceAccountNumber());
		if (sourceAccount.isPresent() && transfer.getAmount().compareTo(sourceAccount.get().getBalance()) <= 0) {
			result = checkTargetAccount(transfer);
		}
		return result;
	}

	private boolean checkTargetAccount(Transfer transfer) {
		return accountsDAO.getAccountByNumber(transfer.getTargetAccountNumber()) != null;

	}

	private Optional<Account> findSourceAccount(Long userId, String sourceAccountNumber) {
		List<Account> accounts = accountsDAO.listUserAccounts(userId);
		Optional<Account> sourceAccount = accounts.stream().filter(acc -> {
			return acc.getAccountNumber().equals(sourceAccountNumber);
		}).findAny();
		return sourceAccount;
	}
}
