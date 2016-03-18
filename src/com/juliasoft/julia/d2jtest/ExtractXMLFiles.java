package com.juliasoft.julia.d2jtest;

import java.io.File;

import javax.print.attribute.HashPrintJobAttributeSet;

import brut.androlib.Androlib;
import brut.androlib.AndrolibException;
import brut.androlib.res.data.ResTable;
import brut.androlib.res.util.ExtFile;
import brut.directory.DirectoryException;

public class ExtractXMLFiles {

	private static ResTable mResTable = null;
	private static Androlib mAndrolib = null;
	private static ExtFile mApkFile = null;

	public static void main(String[] args) {

		System.out.println("ExtractXMLFiles: start");

		mApkFile = new ExtFile(args[0]);

		mAndrolib = new Androlib();
		File outDir = new File(args[1]);

		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		try {
			if (hasManifest())
				mAndrolib.decodeManifestFull(mApkFile, outDir, getResTable());

			if (hasResources())
				mAndrolib.decodeResourcesFull(mApkFile, outDir, getResTable());

		} catch (AndrolibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("ExtractXMLFiles: end");
	}

	public static ResTable getResTable() throws AndrolibException {
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

	public static boolean hasManifest() throws AndrolibException {
		try {
			return mApkFile.getDirectory().containsFile("AndroidManifest.xml");
		} catch (DirectoryException ex) {
			throw new AndrolibException(ex);
		}
	}

	public static boolean hasResources() throws AndrolibException {
		try {
			return mApkFile.getDirectory().containsFile("resources.arsc");
		} catch (DirectoryException ex) {
			throw new AndrolibException(ex);
		}
	}

}
