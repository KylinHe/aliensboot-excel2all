package com.vvv.converter.model;

import java.io.File;

public class DestinationFile {
	String fileName;
	File file;

	public DestinationFile(String fileName) {
		this.fileName = fileName;
		this.file = new File(this.fileName);
		// TODO Access rights should be checked
		if (this.file.exists()) {
			System.out.println("Old file deleted : " + fileName);
			this.file.delete();
		}
	}

	public File getFile() {
		return this.file;
	}
}
