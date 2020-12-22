package com.vvv.converter.exception;

import java.util.Arrays;

public class ArgsNotValidException extends Exception implements IParameter {

	private static final long serialVersionUID = 1L;
	String[] args;

	public ArgsNotValidException(String message, String[] args) {
		super(message);
		this.args = args;
	}

	@Override
	public String getParameter() {
		return Arrays.toString(args);
	}
}
