package com.rtransfer.internal.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.db.DataSourceFactory;

public class TransferApplicationConf extends io.dropwizard.Configuration {

	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();
	
	@Valid
	@NotNull
	String h2ConsolePort;
	
	@Valid
	@NotNull
	boolean h2ConsoleWebStart = false;

	@JsonProperty("h2ConsoleWebStart")
	public boolean isH2ConsoleWebStart() {
		return h2ConsoleWebStart;
	}
	@JsonProperty("h2ConsoleWebStart")
	public void setH2ConsoleWebStart(boolean h2ConsoleWebStart) {
		this.h2ConsoleWebStart = h2ConsoleWebStart;
	}
	@JsonProperty("h2ConsolePort")
	public String getH2ConsolePort() {
		return h2ConsolePort;
	}
	@JsonProperty("h2ConsolePort")
	public void setH2ConsolePort(String h2ConsolePort) {
		this.h2ConsolePort = h2ConsolePort;
	}

	@JsonProperty("database")
	public void setDataSourceFactory(DataSourceFactory factory) {
		this.database = factory;
	}

	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory() {
		return database;
	}

}