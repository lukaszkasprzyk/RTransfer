package com.rtransfer.api.exception;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = -6085202403356607625L;

	private ErrorCode errorCode = null;

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public ValidationException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public ValidationException() {
		this.errorCode = ErrorCode.VALIDATION_FAILED;
	}
}
