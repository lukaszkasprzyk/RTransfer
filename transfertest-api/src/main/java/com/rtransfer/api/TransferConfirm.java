package com.rtransfer.api;

import java.math.BigInteger;

import javax.validation.constraints.NotNull;

public class TransferConfirm {
	@NotNull
	private BigInteger transactionId;

	public BigInteger getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(BigInteger transactionId) {
		this.transactionId = transactionId;
	}
}
