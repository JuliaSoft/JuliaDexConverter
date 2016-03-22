package com.juliasoft.julia.juliadexconverter;

import java.io.File;
import java.io.FilenameFilter;

public class Massive {

	public static void main(String[] args) {

		String folderApk = args[0];

		File[] allApk = new File(folderApk).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".apk");
			}
		});

		int i = 0;
		for (File f : allApk) {

			System.out.println((++i) + ") DOING: " + f.getName());

			String fileNameInput = f.getAbsolutePath();

			int r = fileNameInput.indexOf(".apk");

			if (r <= 0) {
				System.err.println(fileNameInput + " is it an APK?");
				continue;
			}

			String n = fileNameInput.substring(0, r);

			String fileNameAppOutput = n + "-app.jar";
			String fileNameLibOutput = n + "-lib.jar";

			JuliaConverter.apk2Jar(fileNameInput, fileNameAppOutput, fileNameLibOutput);

			System.out.println("DONE!");

		}

	}

}
