package com.rtransfer.mapper;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.rtransfer.api.Transfer;

public class TransferMapper implements ResultSetMapper<Transfer> {

	public Transfer map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		Transfer trn = new Transfer();
		trn.setAmount(r.getBigDecimal("amount"));
		trn.setFxRate(r.getBigDecimal("fxrate"));
		trn.setCurrency(r.getString("currency"));
		trn.setTransactionId(BigInteger.valueOf(r.getInt("transactionid")));
		trn.setCreatorUserId(r.getLong("creatoruserid"));
		trn.setReference(r.getString("reference"));
		trn.setSourceAccountNumber(r.getString("sourceaccountnumber"));
		trn.setTargetAccountNumber(r.getString("targetaccountnumber"));
		trn.setStatus(r.getString("status"));
		return trn;
	}

}
