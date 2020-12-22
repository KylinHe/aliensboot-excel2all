package com.vvv.converter.exception;

public class ArgsNotFoundException extends Exception implements IParameter {

	private static final long serialVersionUID = 1L;

	public ArgsNotFoundException(String message) {
		super(message);
	}

	@Override
	public String getParameter() {
		return "";
	}

}
