package com.rtransfer.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.rtransfer.api.Transfer;
import com.rtransfer.api.TransferConfirm;
import com.rtransfer.api.TransferStatus;
import com.rtransfer.api.exception.ErrorCode;
import com.rtransfer.internal.config.TransferApplication;
import com.rtransfer.internal.config.TransferApplicationConf;

import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

public class TransferIntegrationTest {
	private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test-config.yml");
	private static final String TMP_FILE = createTempFile();
	private static String TRANSFER_URI_CREATE = "";
	private static String TRANSFER_URI_CONFIRM = "";
	@ClassRule
	public static final DropwizardAppRule<TransferApplicationConf> RULE = create();

	@BeforeClass
	public static void migrateDb() throws Exception {
		RULE.getApplication().run("db", "migrate", CONFIG_PATH);

		TRANSFER_URI_CONFIRM = "http://localhost:" + RULE.getLocalPort() + "/transfer/confirm";
		TRANSFER_URI_CREATE = "http://localhost:" + RULE.getLocalPort() + "/transfer/create";
	}

	private static String createTempFile() {
		try {
			return File.createTempFile("test-example", null).getAbsolutePath();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static DropwizardAppRule<TransferApplicationConf> create() {
		ConfigOverride co = ConfigOverride.config("database.url", "jdbc:h2:" + TMP_FILE);
		DropwizardAppRule<TransferApplicationConf> conf = new DropwizardAppRule<TransferApplicationConf>(
				TransferApplication.class, CONFIG_PATH, co);

		return conf;
	}

	@Test
	public void createAndConfirmTransfer() throws Exception {
		Transfer transfer = new Transfer();
		transfer.setSourceAccountNumber("MMM9876543211MMM9876543211");
		transfer.setTargetAccountNumber("RRR1234567890RRR1234567890");
		transfer.setCurrency("EUR");
		transfer.setReference("TITLE");
		transfer.setAmount(BigDecimal.valueOf(0.11d));
		transfer.setCreatorUserId(2l);

		Entity<Transfer> trn = Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE);
		final Transfer response = RULE.client().target(TRANSFER_URI_CREATE).request().post(trn)
				.readEntity(Transfer.class);

		assertNotNull(response);
		assertEquals(transfer.getAmount(), response.getAmount());

		TransferConfirm confirm = new TransferConfirm();
		confirm.setTransactionId(response.getTransactionId());
		final TransferStatus status = RULE.client().target(TRANSFER_URI_CONFIRM).request()
				.post(Entity.entity(confirm, MediaType.APPLICATION_JSON_TYPE)).readEntity(TransferStatus.class);

		assertNotNull(status);
		assertEquals(status.getTransfer().getStatus(), "SUCCESS");
	}

	@Test
	public void createWith3MissingInputFields() throws Exception {
		Transfer transfer = new Transfer();
		transfer.setAmount(BigDecimal.valueOf(0.11d));
		transfer.setCreatorUserId(2l);

		Entity<Transfer> trn = Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE);
		Response response = RULE.client().target(TRANSFER_URI_CREATE).request().post(trn);

		final int status = response.getStatus();
		assertEquals(422, status);
		Map<String, List<String>> obj = response.readEntity(Map.class);
		assertEquals(3, obj.get("errors").size());
	}

	@Test
	public void createWithZeroAmount() throws Exception {
		Transfer transfer = new Transfer();
		transfer.setAmount(BigDecimal.ZERO);
		transfer.setSourceAccountNumber("MMM9876543211MMM9876543211");
		transfer.setTargetAccountNumber("RRR1234567890RRR1234567890");
		transfer.setCurrency("EUR");
		transfer.setReference("TITLE");
		transfer.setCreatorUserId(2l);

		Entity<Transfer> trn = Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE);
		Response response = RULE.client().target(TRANSFER_URI_CREATE).request().post(trn);

		final int status = response.getStatus();
		assertEquals(422, status);
		Map<String, List<String>> obj = response.readEntity(Map.class);
		assertEquals(1, obj.get("errors").size());
	}

	@Test
	public void createWithTooBigAmount() throws Exception {
		Transfer transfer = new Transfer();
		transfer.setSourceAccountNumber("MMM9876543211MMM9876543211");
		transfer.setTargetAccountNumber("RRR1234567890RRR1234567890");
		transfer.setCurrency("EUR");
		transfer.setReference("TITLE");
		transfer.setAmount(BigDecimal.valueOf(211.11d));
		transfer.setCreatorUserId(2l);

		Entity<Transfer> trn = Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE);
		Response response = RULE.client().target(TRANSFER_URI_CREATE).request().post(trn);

		final int status = response.getStatus();
		assertEquals(200, status);
		Map<String, List<ErrorCode>> obj = response.readEntity(Map.class);
		assertEquals(ErrorCode.VALIDATION_FAILED.name(), obj.get("errors").get(0));
	}
}
