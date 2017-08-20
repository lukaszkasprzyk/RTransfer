package com.rtransfer.internal;

import org.junit.After;
import org.junit.BeforeClass;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class BaseDBTest {
	static Handle dbHandler;

	@BeforeClass
	public static void setup() throws LiquibaseException {
		//DeleteDbFiles.execute("./", "target/test_database", true);
		DBI dbi = new DBI("jdbc:h2:./target/test_database");
		dbHandler = dbi.open();
		Liquibase migrator = new Liquibase("migrations-tests.xml", new ClassLoaderResourceAccessor(),
				new JdbcConnection(dbHandler.getConnection()));

		migrator.update("");
	}

	@After
	public void setupAfter() {
		dbHandler.rollback();
	}
}
