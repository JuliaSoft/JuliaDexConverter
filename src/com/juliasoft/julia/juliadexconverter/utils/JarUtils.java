package com.juliasoft.julia.juliadexconverter.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;

import com.google.common.io.Files;

public class JarUtils {
	public static void createJar(String outputFile, File fileDir) throws IOException, FileNotFoundException {
		createJar(new File(outputFile), fileDir);
	}

	public static void createJar(File outputFile, File fileDir) throws FileNotFoundException, IOException {

		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

		JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputFile), manifest);

		addAllFiles(fileDir, jarOutputStream, "");

		jarOutputStream.close();
	}

	public static void extractJar(File jarFile, File destDir) throws IOException {
		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> enumEntries = jar.entries();
		while (enumEntries.hasMoreElements()) {
			JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
			File f = new File(destDir + java.io.File.separator + file.getName());

			if (file.isDirectory()) { // if its a directory, create it
				f.mkdirs();
				continue;
			} else {
				FileUtils.touch(f);
			}
			java.io.InputStream is = jar.getInputStream(file); // get the input
																// stream
			java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
			while (is.available() > 0) { // write contents of 'is' to 'fos'
				fos.write(is.read());
			}
			fos.close();
			is.close();
		}
		jar.close();

	}

	private static void addAllFiles(File outDir, JarOutputStream jarOutputStream, String internalPath)
			throws IOException {

		for (File f : outDir.listFiles()) {

			if (f.isDirectory()) {
				addAllFiles(f, jarOutputStream, internalPath + f.getName() + File.separator);
			} else {
				addEntry(f, jarOutputStream, internalPath);
			}
		}

	}

	private static void addEntry(File f, JarOutputStream jarOutputStream, String internalPath) throws IOException {

		JarEntry entry = new JarEntry(internalPath + f.getName());
		jarOutputStream.putNextEntry(entry);
		jarOutputStream.write(Files.toByteArray(f));
		jarOutputStream.closeEntry();
	}

	public static void createJar(String outputFile, File fileDir, final String[] filter) throws IOException {
		
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		
		FileOutputStream fos = new FileOutputStream(outputFile);
		
		JarOutputStream jarOutputStream = new JarOutputStream(fos, manifest);
		addAllFilesFilter(fileDir, jarOutputStream, "", filter);
		jarOutputStream.close();
		fos.close();
	}

	private static void addAllFilesFilter(File outDir, JarOutputStream jarOutputStream, String internalPath,
			final String[] filter) throws IOException {

		final HashMap<String, List<String>> fileFilter = new HashMap<String, List<String>>();

		for (File f : outDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {

				ArrayList<String> nFilter;
				if (fileFilter.get(name) == null) {
					nFilter = new ArrayList<String>();
					fileFilter.put(name, nFilter);
				} else
					nFilter = (ArrayList<String>) fileFilter.get(name);

				for (String f : filter)
					if (f.startsWith(name)) {
						int r = f.indexOf(File.separatorChar);
						if (r >= 0)
							nFilter.add(f.substring(r + 1));
						return true;
					}

				return false;
			}
		})) {

			if (f.isDirectory()) {
				String[] nFilter;

				if (fileFilter.get(f.getName()) != null) {
					ArrayList<String> n = (ArrayList<String>) fileFilter.get(f.getName());
					nFilter = new String[n.size()];
					nFilter = n.toArray(nFilter);
				} else {
					nFilter = new String[0];
				}

				if (nFilter.length == 0)
					addAllFiles(f, jarOutputStream, internalPath + f.getName() + File.separator);
				else
					addAllFilesFilter(f, jarOutputStream, internalPath + f.getName() + File.separator, nFilter);
			} else {
				addEntry(f, jarOutputStream, internalPath);
			}
		}

	}

	private static void addAllFiles(String f, JarOutputStream jarOutputStream, String internalPath) throws IOException {
		addAllFiles(new File(f), jarOutputStream, internalPath);
	}
}
