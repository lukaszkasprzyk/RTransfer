package com.rtransfer.internal;

import java.math.BigDecimal;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.rtransfer.api.Account;
import com.rtransfer.mapper.AccountMapper;

@RegisterMapper(AccountMapper.class)
public interface AccountsDAO {
	public static final String ACCOUNT_BY_NUMBER = "select * from accounts where accountnumber =:accountnumber";
	static final String GET_USER_ACCOUNTS = "select * from accounts where userid =:userid";
	static final String UPDATE_BALANCE = "update accounts set balance =balance+ :balance where accountid =:accountid";

	@SqlUpdate(UPDATE_BALANCE)
	void updateBalance(@Bind("accountid") long id, @Bind("balance") BigDecimal balance);

	@SqlQuery(GET_USER_ACCOUNTS)
	List<Account> listUserAccounts(@Bind("userid") long userid);

	@SqlQuery(ACCOUNT_BY_NUMBER)
	Account getAccountByNumber(@Bind("accountnumber") String accountnumber);

}
