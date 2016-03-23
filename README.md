This document is aimed to give an overview about the program configuration and to give the basic idea that stands behind.

The sections are structured as follows:

1. Quickly guide to import the project in Eclipse
2. The main purpouse of tool
3. Program structure

# JuliaDexConverter setup

## Requirement:

- (My)Dex2Jar sources
- ApkTool sources
- asm-debug-all-4.1.jar download

## Eclipse settings

1. Julia Dex Converter clone: git clone https://tregua87@bitbucket.org/tregua87/juliadexconverter.git
2. Import project in Eclipse
3. Fix build path:
	1. add all d2j projects (git clone https://tregua87@bitbucket.org/tregua87/dex2jar.git)
	2. add all apktool projects (git clone https://github.com/iBotPeaches/Apktool.git)
	3. add asm-debug-all-4.1.jar as external jar library (search it on Web)
	
# Purpouses

- The first aim is double: I wish transform dex into standard java class files and also transform XML file in a readable format.

Unfortunally I did not able to find a full library which can perform both tasks.
To achieve this I merged two stable tools: Dex2Jar and ApkTool.

**Dex2Jar** performs the code transformation, it reads dex file and, throuth a set of code manipulation, it will re-build relative class files (with some approximations).

**ApkTool** performs the XML transformation: the xml files kept into apk are stored in a binary format, this tool is able to revert them in a readable rapresentation.

- The second aim is to transfer debug information from Dex to Class. 

Even in this case, there are not tools able to perform this operation.
To achieve it I've developed a new code-transformation in Dex2Jar which tries to preserve line numbers. This is just a prototype, but it works fine on my tests. It could be also improved.

- The third aim is to craft 2 jar files. 

One which contains all classes that belong to original application, called "application.jar", and another one which contains all classes used as library, called "library.jar".
This point is important in order to perform better analysis through Julia.
From pratical terms, we'll submit the application.jar file with -si option and library.jar file with -i option.

# How it works

The transformation process is implemented into *JuliaConverter* class, it exposes a public method, namely *apk2Jar()*, which takes as input:

- fileNameInput: started APK
- fileNameApplication: the JAR file that will contain all application classes, AndroidManifest.xml and other resources
- fileNameLibrary: the JAR file that will contain all library classes
	
To correctly use JuliaConverter class, you also need to set these properties:

- pathTemp: a temporary folder
- pathTmpFileNameClass: a file where all class files will be temporarily stored
- pathTmpFileNameXml: a file where all xml files and other resources will be temporarily stored

It is possible to see a full example in class *Main*.

# Program phases:

## 1) Class transformation

The project uses Dex2Jar as a library to translate the APK to JAR. 
This transformation produce a Jar file which contains only class files.

## 2) Resource transformation

The project uses ApkTool as a library in order to produce a Jar file which contains all XMLs (in a readable format) and other resources (images and other kind of files..).

## 3) Extracting class and resource files in a temporary directory

The previous jars are extracted in a temporary folder where they will be handled.

## 4) Retrieve application folder

The tool reads AndroidManifest.xml in order to retrieve the attribute "package" of tag <manifest>.
We assume that all classes stored under this package belong to application.

## 5) Creation application.jar file

Using the information at point 4), the tool creates a new jar file that contains all classes belong to application.

## 6) Creation library.jar file

Using the information at point 4), the tool creates a new jar file that contains all classes used as library.