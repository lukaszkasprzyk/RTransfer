package com.rtransfer.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.rtransfer.api.Account;

public class AccountsDAOTest extends BaseDBTest {

	AccountsDAO accountsDAO;

	@Before
	public void setupDAO() {
		accountsDAO = dbHandler.attach(AccountsDAO.class);
	}

	@Test
	public void getNonExistingAccount() {
		Account acc = accountsDAO.getAccountByNumber("123");
		assertNull(acc);
	}

	@Test
	public void getExistingAccount() {
		Account acc = accountsDAO.getAccountByNumber("RRR1234567890RRR1234567890");
		assertNotNull(acc);
	}

	@Test
	public void updateBalance() {
		accountsDAO.increaseBalance(1l, BigDecimal.valueOf(133.0d));
		Account acc = accountsDAO.getAccountByNumber("RRR1234567890RRR1234567890");
		assertNotNull(acc);
		assertTrue(233.0d == acc.getBalance().doubleValue());
	}

	@Test
	public void getUserAccounts() {
		List<Account> accounts = accountsDAO.listUserAccounts(1l);
		assertNotNull(accounts);
		assertTrue(2 == accounts.size());
	}

}
