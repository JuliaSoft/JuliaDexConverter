package com.juliasoft.julia.d2jtest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.googlecode.d2j.smali.Baksmali;

public class Smali {

	public static void main(String[] args) {

		if(args.length == 0) {
			System.out.println("Smali <apk>");
		}
		
		File dex = new File(args[0]);
		Path output = null;
		if (!dex.exists()) {
			System.err.println("ERROR: " + dex + " is not exists");
			return;
		}
		if (output == null) {
			output = new File(getBaseName(dex.getName()) + "-out").toPath();
		}
		Baksmali b;
		try {
			b = Baksmali.from(dex);
			System.err.println("baksmali " + dex + " -> " + output);
			b.to(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("done");

	}
	
	public static String getBaseName(String fn) {
        int x = fn.lastIndexOf('.');
        return x >= 0 ? fn.substring(0, x) : fn;
    }

}
