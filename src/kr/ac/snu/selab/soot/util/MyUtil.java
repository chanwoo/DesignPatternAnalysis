package kr.ac.snu.selab.soot.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MyUtil {
	public static String removeBracket(String aString) {
		String result = aString.replaceAll("<", "{");
		result = result.replaceAll(">", "}");
		return result;
	}

	public static void stringToFile(String aString, String aFilePath) {
		try {
			File outputFile = new File(aFilePath);
			File dir = outputFile.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			PrintWriter writer = new PrintWriter(new FileWriter(aFilePath));
			writer.print(aString);
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static String getPath(String parentPath, String name) {
		return getPath(new File(parentPath), name);
	}

	public static String getPath(File parent, String name) {
		File file = new File(parent, name);
		return file.getAbsolutePath();
	}
}
