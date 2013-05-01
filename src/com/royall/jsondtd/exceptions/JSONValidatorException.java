package com.royall.jsondtd.exceptions;

public class JSONValidatorException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private String message = "";
	private Exception exception = null;
	
	public JSONValidatorException(){
		super();
	}
	
	public JSONValidatorException(String _message) {
		super(_message);
	}
	
	public JSONValidatorException(Exception _e) {
		super(_e);
	}

	public JSONValidatorException(String _message, Exception _e) {
		super(_e);
		exception = _e;
		message = _message;
	}

	public String getMessage() {
		return message;
	}
	
	public Exception getException() {
		return exception;
	}
}
