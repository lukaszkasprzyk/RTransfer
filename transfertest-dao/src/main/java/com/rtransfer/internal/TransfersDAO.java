package com.rtransfer.internal;

import java.math.BigInteger;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.rtransfer.api.Transfer;
import com.rtransfer.mapper.TransferMapper;

@RegisterMapper(TransferMapper.class)
public interface TransfersDAO {

	static final String INSERT_TRANSFER = "insert into transfers "
			+ "(amount,sourceaccountnumber,targetaccountnumber,fxrate,creatoruserid,currency,reference) "
			+ "values (:t.amount,:t.sourceAccountNumber,:t.targetAccountNumber,:t.fxRate,:t.creatorUserId,:t.currency,:t.reference)";
	static final String GET_TRANSFER_BY_ID = "select * from transfers where transactionid =:id";

	static final String UPDATE_TRANSFER_STATUS = "update transfers set status=:status where transactionid =:id";

	@SqlUpdate(INSERT_TRANSFER)
	@GetGeneratedKeys
	Integer insertTransfer(@BindBean("t") Transfer t);

	@SqlUpdate(UPDATE_TRANSFER_STATUS)
	void updateTransferStatus(@Bind("id") BigInteger id, @Bind("status") String status);

	@SqlQuery(GET_TRANSFER_BY_ID)
	Transfer getTransferById(@Bind("id") BigInteger id);
}
