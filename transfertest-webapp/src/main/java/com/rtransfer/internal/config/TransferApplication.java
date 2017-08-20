package com.rtransfer.internal.config;

import org.h2.tools.Server;
import org.skife.jdbi.v2.DBI;

import com.rtransfer.internal.AccountsDAO;
import com.rtransfer.internal.TransfersDAO;
import com.rtransfer.internal.TransferResource;
import com.rtransfer.internal.TransferService;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TransferApplication extends Application<TransferApplicationConf> {
	public static void main(String[] args) throws Exception {
		new TransferApplication().run(args);

	}

	@Override
	public String getName() {
		return "transfer-app";
	}

	@Override
	public void initialize(Bootstrap<TransferApplicationConf> bootstrap) {
		bootstrap.addBundle(new MigrationsBundle<TransferApplicationConf>() {
			public DataSourceFactory getDataSourceFactory(TransferApplicationConf configuration) {
				return configuration.getDataSourceFactory();
			}
		});
	}

	@Override
	public void run(TransferApplicationConf configuration, Environment environment) {

		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "h2");
		TransfersDAO transferDAO = jdbi.onDemand(TransfersDAO.class);
		AccountsDAO accountsDAO = jdbi.onDemand(AccountsDAO.class);
		TransferService service = jdbi.onDemand(TransferService.class);
		service.transferDAO = transferDAO;
		service.accountsDAO = accountsDAO;
		TransferResource transfer = new TransferResource(service);
		environment.jersey().register(transfer);
		environment.jersey().register(service);
		environment.jersey().register(transferDAO);
		environment.jersey().register(accountsDAO);

		environment.jersey().register(new ValidationExceptionProvider());
		if (configuration.isH2ConsoleWebStart())
			try {
				Server.createWebServer("-web", "-webAllowOthers", "-webPort", configuration.getH2ConsolePort()).start();
				Server.openBrowser("http://localhost:8082");

			} catch (Exception e) {
				e.printStackTrace();
			}
	}

}