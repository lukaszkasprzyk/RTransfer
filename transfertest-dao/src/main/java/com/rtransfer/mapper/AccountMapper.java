package com.rtransfer.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.rtransfer.api.Account;

public class AccountMapper implements ResultSetMapper<Account> {

	public Account map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		Account acc = new Account();
		acc.setAccountId(r.getLong("accountid"));
		acc.setAccountNumber(r.getString("accountnumber"));
		acc.setBalance(r.getBigDecimal("balance"));
		acc.setUserid(r.getLong("userid"));
		acc.setCurrency(r.getString("currency"));
		return acc;
	}

}
