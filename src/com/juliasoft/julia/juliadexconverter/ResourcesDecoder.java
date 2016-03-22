package com.juliasoft.julia.juliadexconverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.FileUtils;

import com.googlecode.d2j.dex.Dex2jar;
import com.juliasoft.julia.juliadexconverter.utils.JarUtils;

import brut.androlib.Androlib;
import brut.androlib.AndrolibException;
import brut.androlib.res.data.ResTable;
import brut.androlib.res.util.ExtFile;
import brut.directory.DirectoryException;

public class ResourcesDecoder {

	private ResourcesDecoder(File input) {
		mApkFile = new ExtFile(input);
		mAndrolib = new Androlib();
	}

	private ResTable mResTable = null;
	private Androlib mAndrolib = null;
	private ExtFile mApkFile = null;

	private String tmpDir = "C:\\tmp1\\";

	public void to(File outputFile) {
		tmpDir += System.currentTimeMillis() % 1000;
		File outDir = new File(tmpDir);

		try {
			if (outDir.exists()) {
				FileUtils.deleteDirectory(outDir);
				if (!outDir.mkdirs()) {
					System.out.println("Impossible create the directory: " + outDir.getPath());
				}
			} else {
				if (!outDir.mkdirs()) {
					System.out.println("Impossible create the directory: " + outDir.getPath());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		try {
			if (!hasManifest() && !hasResources()) {
				System.err.println("Neither manifest nor resources, exit");
				return;
			}

			if (hasManifest())
				mAndrolib.decodeManifestFull(mApkFile, outDir, getResTable());

			if (hasResources())
				mAndrolib.decodeResourcesFull(mApkFile, outDir, getResTable());

		} catch (AndrolibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		// move generated file in the outgoing JAR
		try {

			JarUtils.createJar(outputFile, outDir);

			FileUtils.deleteDirectory(outDir);
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}

	public ResTable getResTable() throws AndrolibException {
		if (mResTable == null) {
			boolean hasResources = hasResources();
			boolean hasManifest = hasManifest();
			if (!(hasManifest || hasResources)) {
				throw new AndrolibException(
						"Apk doesn't contain either AndroidManifest.xml file or resources.arsc file");
			}
			mResTable = mAndrolib.getResTable(mApkFile, hasResources);
		}
		return mResTable;
	}

	public boolean hasManifest() throws AndrolibException {
		try {
			return mApkFile.getDirectory().containsFile("AndroidManifest.xml");
		} catch (DirectoryException ex) {
			throw new AndrolibException(ex);
		}
	}

	public boolean hasResources() throws AndrolibException {
		try {
			return mApkFile.getDirectory().containsFile("resources.arsc");
		} catch (DirectoryException ex) {
			throw new AndrolibException(ex);
		}
	}

	public static ResourcesDecoder from(String fileNameInput) {

		return new ResourcesDecoder(new File(fileNameInput));
	}

	public void to(String tmpFileNameXml) {
		to(new File(tmpFileNameXml));
	}

	public ResourcesDecoder setTempDir(String t) {

		this.tmpDir = t;

		return this;
	}

}