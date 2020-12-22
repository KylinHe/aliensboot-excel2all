package com.vvv.converter.model;

import java.io.File;

import com.vvv.converter.exception.SourceFileAccessRightException;
import com.vvv.converter.exception.SourceFileDoesNotExistsException;

public class SourceFile {
	String fileName;
	File file;

	public SourceFile(String fileName) throws SourceFileAccessRightException, SourceFileDoesNotExistsException {
		this.fileName = fileName;
		this.file = new File(this.fileName);
		
		if (!this.file.exists()) {
			throw new SourceFileDoesNotExistsException("Source file does not exists.", this.fileName);
		} else if (!this.file.canRead()) {
			throw new SourceFileAccessRightException("Cannot read source file. Please check access rights.",
					this.fileName);
		}
	}
	
	public File getFile() {
		return this.file;
	}
	
	
}
