package com.vvv.converter.utils;

import java.io.File;

public class FileUtils {

	private FileUtils() {
		// Private Constructor
	}

	public static boolean isExists(File file) {
		return file.exists();
	}

	public static boolean isExists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}
	
	public static void delete(String fileName) {
		File file = new File(fileName);
		if(file.exists()) {
			file.delete();
		}
	}
}
