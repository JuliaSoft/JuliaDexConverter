package com.juliasoft.julia.d2jtest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.googlecode.d2j.dex.Dex2jar;
import com.googlecode.d2j.reader.BaseDexFileReader;
import com.googlecode.d2j.reader.MultiDexFileReader;
import com.googlecode.dex2jar.tools.BaksmaliBaseDexExceptionHandler;
import com.juliasoft.julia.d2jtest.utils.JarUtils;

public class JuliaConverter {

	static void apk2Jar(String fileNameInput, String fileNameApplication, String fileNameLibrary) {
		String pathTemp = "c:\\tmp1\\";

		String pathTmpFileNameClass = pathTemp + File.separator + "tmp_class.jar";
		String pathTmpFileNameXml = pathTemp + File.separator + "tmp_xml.jar";

		File tmpFileNameClass = new File(pathTmpFileNameClass);
		File tmpFileNameXml = new File(pathTmpFileNameXml);

		Path currentDir = new File(".").toPath();
		String baseName = new File(fileNameInput).toPath().toString();

		BaseDexFileReader reader;
		try {
			reader = MultiDexFileReader.open(Files.readAllBytes(new File(fileNameInput).toPath()));
			BaksmaliBaseDexExceptionHandler handler = new BaksmaliBaseDexExceptionHandler();
			Dex2jar.from(reader).withExceptionHandler(handler).skipDebug(false)
					/* .reUseReg(reuseReg) */.topoLogicalSort()
					/*
					 * .skipDebug(!debugInfo).optimizeSynchronized(this.
					 * optmizeSynchronized).printIR(printIR) .noCode(noCode)
					 */.to(tmpFileNameClass.toPath());

			if (handler.hasException()) {
				Path errorFile = currentDir.resolve(baseName + "-error.zip");
				System.err.println("Detail Error Information in File " + errorFile);
				System.err.println(BaksmaliBaseDexExceptionHandler.REPORT_MESSAGE);
				handler.dump(errorFile, new String[0]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		ResourcesDecoder.from(fileNameInput).setTempDir(pathTemp).to(tmpFileNameXml);

		File tmpFileName = new File(pathTemp + System.currentTimeMillis() % 1000);

		try {

			if (tmpFileName.exists()) {
				FileUtils.deleteDirectory(tmpFileName);
			}
			tmpFileName.mkdirs();

			JarUtils.extractJar(tmpFileNameClass, tmpFileName);
			JarUtils.extractJar(tmpFileNameXml, tmpFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("EXTRACT: DONE!");

		String applicationPackage = getPackageName(tmpFileName + File.separator + "AndroidManifest.xml");

		String folderApplicationPackage = "";
		
		for (String i : applicationPackage.split("\\."))
			folderApplicationPackage += i + File.separator;
		folderApplicationPackage = folderApplicationPackage.substring(0, folderApplicationPackage.length() - 1);

		String[] applicationFiles = { folderApplicationPackage, "res", "AndroidManifest.xml" };

		// move generated file in the outgoing JAR
		try {
			JarUtils.createJar(fileNameApplication, tmpFileName, applicationFiles);

			for (String p : applicationFiles) {
				File f = new File(tmpFileName, p);
				if (f.isDirectory())
					FileUtils.deleteDirectory(f);
				else
					f.delete();
			}

			JarUtils.createJar(fileNameLibrary, tmpFileName);

			FileUtils.deleteDirectory(tmpFileName);

			tmpFileNameClass.delete();
			tmpFileNameXml.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getPackageName(String manifestPath) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			// Load the input XML document, parse it and return an instance of
			// the
			// Document class.
			Document document = builder.parse(new File(manifestPath));

			NodeList nodeList = document.getElementsByTagName("manifest");

			if (nodeList.getLength() != 1)
				throw new Error("AndroidManifest.xml has not <manifest> tag");

			Node manifest = nodeList.item(0);

			NamedNodeMap attributes = manifest.getAttributes();

			return attributes.getNamedItem("package").getNodeValue();

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			throw new Error(e);
		}
	}

}
