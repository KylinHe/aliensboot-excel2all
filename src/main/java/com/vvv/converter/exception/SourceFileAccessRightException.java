package com.vvv.converter.exception;

public class SourceFileAccessRightException extends Exception implements IParameter {

	private static final long serialVersionUID = 1L;
	private String fileName;

	public SourceFileAccessRightException(String message, String fileName) {
		super(message);
		this.fileName = fileName;
	}

	@Override
	public String getParameter() {
		return this.fileName;
	}
}
