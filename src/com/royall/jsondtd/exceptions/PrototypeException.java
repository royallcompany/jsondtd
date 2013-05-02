package com.royall.jsondtd.exceptions;

public class PrototypeException extends JSONValidatorException {
	private static final long serialVersionUID = 1L;

	private String message = null;

	private Exception e = null;

	public PrototypeException(String _message) {
		super(_message);
		message = _message;
	}

	public PrototypeException(Exception _e) {
		super(_e);
		e = _e;
	}

	public PrototypeException(String _message, Exception _e) {
		super(_message, _e);
		message = _message;
		e = _e;
	}

	public String getMessage() {
		return message;
	}

	public Exception getException() {
		return e;
	}
}
