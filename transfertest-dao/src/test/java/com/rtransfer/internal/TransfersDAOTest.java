package com.rtransfer.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import com.rtransfer.api.Transfer;

public class TransfersDAOTest extends BaseDBTest {

	TransfersDAO transfersDAO;

	@Before
	public void setupDAO() {
		transfersDAO = dbHandler.attach(TransfersDAO.class);
	}

	@Test
	public void insertTransferTest() {
		Transfer t = new Transfer();
		t.setAmount(BigDecimal.TEN);
		t.setCreatorUserId(11l);
		t.setCurrency("EUR");
		t.setFxRate(BigDecimal.valueOf(3.31));
		t.setReference("TITLE");
		t.setSourceAccountNumber("X");
		t.setTargetAccountNumber("XX");
		Integer reference = transfersDAO.insertTransfer(t);

		Transfer dbTransfer = transfersDAO.getTransferById(BigInteger.valueOf(reference.longValue()));
		assertNotNull(reference);
		assertNotNull(dbTransfer);
		assertEquals(t.getAmount(), dbTransfer.getAmount());
		assertEquals("NEW", dbTransfer.getStatus());
		assertEquals(reference.intValue(), dbTransfer.getTransactionId().intValue());
	}

	@Test
	public void updateTransferTest() {

	}
}
