package com.juliasoft.julia.juliadexconverter;

public class Main {

	public static void main(String[] args) {

		System.out.println("START!");

		if (args.length != 3) {
			System.out.println("usage: MyDex2Jar <inputfile> <outpufile>");
			System.out.println("END!");
			System.exit(1);
		}

		String fileNameInput = args[0];
		String fileNameOutput = args[1];
		String fileNameLibrary = args[2];

		JuliaConverter.apk2Jar(fileNameInput, fileNameOutput, fileNameLibrary);

		System.out.println("DONE!");

	}

}
