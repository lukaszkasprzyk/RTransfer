package com.rtransfer.internal;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.rtransfer.api.Transfer;
import com.rtransfer.api.TransferConfirm;

@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
public class TransferResource {

	TransferService service;

	public TransferResource(TransferService service) {
		this.service = service;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/create")
	public Transfer create(@Valid Transfer input) {
		return service.forecast(input);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/confirm")
	public Transfer confirm(@Valid TransferConfirm input) {
		return service.process(input.getTransactionId());
	}

	@GET
	@Path("/transfers")
	public List<Transfer> listAll() {
		return null;// dao.listAll();
	}
}
