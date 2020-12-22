package com.vvv.converter.utils;

import com.vvv.converter.exception.ArgsNotFoundException;
import com.vvv.converter.exception.ArgsNotValidException;
import com.vvv.converter.exception.DestinationFileAccessRightException;
import com.vvv.converter.exception.SourceFileAccessRightException;
import com.vvv.converter.exception.SourceFileDoesNotExistsException;
import com.vvv.converter.model.DestinationFile;
import com.vvv.converter.model.SourceFile;

public class ArgsParser {

	String[] args;
	SourceFile sourceFile;
	DestinationFile destinationFile;

	public ArgsParser(String[] args) throws ArgsNotFoundException, ArgsNotValidException,
			SourceFileAccessRightException, SourceFileDoesNotExistsException {
		this.args = args;
		parse();
	}

	private void parse() throws ArgsNotFoundException, ArgsNotValidException, SourceFileAccessRightException,
			SourceFileDoesNotExistsException {
		if (args == null) {
			throw new ArgsNotFoundException("Args not found!");
		} else if (args.length != 2) {
			throw new ArgsNotValidException("2 file parameter should be fed!", this.args);
		} else {
			this.sourceFile = new SourceFile(args[0]);
			this.destinationFile = new DestinationFile(args[1]);
		}

	}

	public SourceFile getSourceFile() {
		return this.sourceFile;
	}

	public DestinationFile getDestinationFile() {
		return this.destinationFile;
	}
}
