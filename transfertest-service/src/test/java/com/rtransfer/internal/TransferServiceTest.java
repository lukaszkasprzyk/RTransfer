package com.rtransfer.internal;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.rtransfer.api.Account;
import com.rtransfer.api.Transfer;
import com.rtransfer.api.TransferStatus;
import com.rtransfer.api.exception.ValidationException;

@RunWith(EasyMockRunner.class)
public class TransferServiceTest {

	TransferService service = new TransferService();

	@Mock(type = MockType.STRICT)
	TransfersDAO transferDAO;
	@Mock(type = MockType.STRICT)
	AccountsDAO accountsDAO;

	@Before
	public void setup() {
		EasyMock.reset(transferDAO, accountsDAO);
		service.accountsDAO = accountsDAO;
		service.transferDAO = transferDAO;
	}

	@Test(expected = ValidationException.class)
	public void test_forecast_notExistingSourceAccount() {
		Transfer transfer = new Transfer();
		transfer.setCreatorUserId(1l);
		expect(accountsDAO.listUserAccounts(transfer.getCreatorUserId())).andReturn(Collections.emptyList());
		replay(accountsDAO);

		service.forecast(transfer);
		verify(accountsDAO);
	}

	@Test(expected = ValidationException.class)
	public void test_forecast_notMatchedSourceAccount() {
		Transfer transfer = new Transfer();
		transfer.setSourceAccountNumber("123");
		transfer.setCreatorUserId(1l);
		Account acc = new Account();
		acc.setAccountNumber("321");
		expect(accountsDAO.listUserAccounts(transfer.getCreatorUserId())).andReturn(Arrays.asList());
		replay(accountsDAO);

		service.forecast(transfer);
		verify(accountsDAO);
	}

	@Test(expected = ValidationException.class)
	public void test_forecast_toBigAmount() {
		Transfer transfer = new Transfer();
		transfer.setSourceAccountNumber("123");
		transfer.setAmount(BigDecimal.TEN);
		transfer.setCreatorUserId(1l);
		Account acc = new Account();
		acc.setBalance(BigDecimal.ZERO);
		acc.setAccountNumber("123");
		expect(accountsDAO.listUserAccounts(transfer.getCreatorUserId())).andReturn(Arrays.asList(acc));
		replay(accountsDAO);

		service.forecast(transfer);
		verify(accountsDAO);
	}

	@Test(expected = ValidationException.class)
	public void test_forecast_nonExistingTargetAcc() {
		Transfer transfer = new Transfer();
		transfer.setSourceAccountNumber("123");
		transfer.setTargetAccountNumber("111");
		transfer.setAmount(BigDecimal.TEN);
		transfer.setCreatorUserId(1l);
		Account source = new Account();
		source.setBalance(BigDecimal.TEN);
		source.setAccountNumber("123");

		expect(accountsDAO.listUserAccounts(transfer.getCreatorUserId())).andReturn(Arrays.asList(source));
		expect(accountsDAO.getAccountByNumber(transfer.getTargetAccountNumber())).andReturn(null);
		replay(accountsDAO);

		service.forecast(transfer);
		verify(accountsDAO);
	}

	@Test
	public void test_forecast_success() {
		Transfer transfer = new Transfer();
		transfer.setSourceAccountNumber("123");
		transfer.setTargetAccountNumber("111");
		transfer.setAmount(BigDecimal.TEN);
		transfer.setCreatorUserId(1l);
		Account source = new Account();
		source.setBalance(BigDecimal.TEN);
		source.setAccountNumber("123");

		Account target = new Account();
		target.setBalance(BigDecimal.ZERO);
		target.setAccountNumber("111");
		expect(accountsDAO.listUserAccounts(transfer.getCreatorUserId())).andReturn(Arrays.asList(source));
		expect(accountsDAO.getAccountByNumber(transfer.getTargetAccountNumber())).andReturn(target);
		replay(accountsDAO);

		expect(transferDAO.insertTransfer(transfer)).andReturn(1);
		Transfer expectedNewTransfer = new Transfer();
		expectedNewTransfer.setSourceAccountNumber("123");
		expectedNewTransfer.setTargetAccountNumber("111");
		expectedNewTransfer.setAmount(BigDecimal.TEN);
		expectedNewTransfer.setCreatorUserId(1l);
		expectedNewTransfer.setStatus("NEW");
		expect(transferDAO.getTransferById(BigInteger.valueOf(1l))).andReturn(expectedNewTransfer);
		replay(transferDAO);
		Transfer storedTransfer = service.forecast(transfer);
		verify(accountsDAO, transferDAO);

		assertNotNull(storedTransfer);
		assertEquals(expectedNewTransfer.getStatus(), storedTransfer.getStatus());
		assertEquals(expectedNewTransfer.getAmount(), storedTransfer.getAmount());
	}

	@Test
	public void test_process_NEW() {
		BigInteger TRNID = BigInteger.valueOf(1l);
		Transfer transfer = new Transfer();
		transfer.setSourceAccountNumber("123");
		transfer.setTargetAccountNumber("111");
		transfer.setAmount(BigDecimal.TEN);
		transfer.setCreatorUserId(1l);

		Account source = new Account();
		source.setBalance(BigDecimal.TEN);
		source.setAccountNumber("123");
		source.setAccountId(9999l);
		Account target = new Account();
		target.setBalance(BigDecimal.ZERO);
		target.setAccountNumber("111");
		target.setAccountId(8888l);
		expect(accountsDAO.listUserAccounts(transfer.getCreatorUserId())).andReturn(Arrays.asList(source));
		expect(accountsDAO.getAccountByNumber(transfer.getTargetAccountNumber())).andReturn(target);

		Transfer expectedNewTransfer = new Transfer();
		expectedNewTransfer.setSourceAccountNumber("123");
		expectedNewTransfer.setTargetAccountNumber("111");
		expectedNewTransfer.setAmount(BigDecimal.TEN);
		expectedNewTransfer.setCreatorUserId(1l);
		expectedNewTransfer.setStatus("NEW");
		expectedNewTransfer.setTransactionId(TRNID);
		expect(transferDAO.getTransferById(TRNID)).andReturn(expectedNewTransfer);
		transferDAO.updateTransferStatus(TRNID, "SUCCESS");
		expectLastCall();
		expect(accountsDAO.listUserAccounts(transfer.getCreatorUserId())).andReturn(Arrays.asList(source));
		accountsDAO.increaseBalance(source.getAccountId(), transfer.getAmount().negate());
		expectLastCall();

		expect(accountsDAO.getAccountByNumber(transfer.getTargetAccountNumber())).andReturn(target);
		accountsDAO.increaseBalance(target.getAccountId(), transfer.getAmount());
		expectLastCall();
		Transfer expectedTransfer = new Transfer();
		expectedTransfer.setSourceAccountNumber("123");
		expectedTransfer.setTargetAccountNumber("111");
		expectedTransfer.setAmount(BigDecimal.TEN);
		expectedTransfer.setCreatorUserId(1l);
		expectedTransfer.setStatus("SUCCESS");

		expect(transferDAO.getTransferById(TRNID)).andReturn(expectedTransfer);
		expect(accountsDAO.getAccountByNumber(source.getAccountNumber())).andReturn(source);
		replay(transferDAO, accountsDAO);
		TransferStatus storedTransfer = service.process(TRNID);

		verify(accountsDAO, transferDAO);
		assertNotNull(storedTransfer);
		assertEquals(expectedTransfer.getStatus(), storedTransfer.getTransfer().getStatus());

	}
	
	@Test
	public void test_process_NEW_with_toBigAmount() {
		BigInteger TRNID = BigInteger.valueOf(1l);
		Transfer transfer = new Transfer();
		transfer.setSourceAccountNumber("123");
		transfer.setTargetAccountNumber("111");
		transfer.setAmount(BigDecimal.TEN);
		transfer.setCreatorUserId(1l);

		Account source = new Account();
		source.setBalance(BigDecimal.ZERO);
		source.setAccountNumber("123");
		source.setAccountId(9999l);
		Account target = new Account();
		target.setBalance(BigDecimal.ZERO);
		target.setAccountNumber("111");
		target.setAccountId(8888l);
		expect(accountsDAO.listUserAccounts(transfer.getCreatorUserId())).andReturn(Arrays.asList(source));

		Transfer expectedNewTransfer = new Transfer();
		expectedNewTransfer.setSourceAccountNumber("123");
		expectedNewTransfer.setTargetAccountNumber("111");
		expectedNewTransfer.setAmount(BigDecimal.TEN);
		expectedNewTransfer.setCreatorUserId(1l);
		expectedNewTransfer.setStatus("NEW");
		expectedNewTransfer.setTransactionId(TRNID);
		expect(transferDAO.getTransferById(TRNID)).andReturn(expectedNewTransfer);
		transferDAO.updateTransferStatus(TRNID, "FAILED");
		expectLastCall();
		
		Transfer expectedTransfer = new Transfer();
		expectedTransfer.setSourceAccountNumber("123");
		expectedTransfer.setTargetAccountNumber("111");
		expectedTransfer.setAmount(BigDecimal.TEN);
		expectedTransfer.setCreatorUserId(1l);
		expectedTransfer.setStatus("FAILED");

		expect(transferDAO.getTransferById(TRNID)).andReturn(expectedTransfer);
		expect(accountsDAO.getAccountByNumber(source.getAccountNumber())).andReturn(source);
		replay(transferDAO, accountsDAO);
		TransferStatus storedTransfer = service.process(TRNID);

		verify(accountsDAO, transferDAO);
		assertNotNull(storedTransfer);
		assertEquals(expectedTransfer.getStatus(), storedTransfer.getTransfer().getStatus());

	}

	@Test(expected = ValidationException.class)
	public void test_process_SUCCESS() {
		BigInteger TRNID = BigInteger.valueOf(1l);

		Transfer expectedNewTransfer = new Transfer();
		expectedNewTransfer.setSourceAccountNumber("123");
		expectedNewTransfer.setTargetAccountNumber("111");
		expectedNewTransfer.setAmount(BigDecimal.TEN);
		expectedNewTransfer.setCreatorUserId(1l);
		expectedNewTransfer.setStatus("SUCCESS");
		expectedNewTransfer.setTransactionId(TRNID);
		expect(transferDAO.getTransferById(TRNID)).andReturn(expectedNewTransfer);
		replay(transferDAO);
		service.process(TRNID);

	}
}
