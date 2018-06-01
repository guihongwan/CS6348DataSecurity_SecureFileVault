package com.datasecurity.filemanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import com.datasecurity.filemanager.Utils.Log;

public class Env {
	private static final String TAG = "Env";
	private static final boolean DEBUG = true;
	
	private static String currDir = System.getProperty("user.dir");
	
	private static String secureFolder = currDir + "/SecureFolder"; // store original files
	private static String confFolder = currDir +"/Conf";// vaults information, later we may be able to store in database
	
	protected static String csvFileName = confFolder + "/metadata.csv";
	protected static int timeout = 1000*60*2;//2 minutes timeout
	
	protected static void build() {
		makeDir();
	}
	
	private static void makeDir() {
		//secureFolder
		File secureDir = new File(secureFolder);
		if(secureDir.exists()) {
			Env.deleteDir(secureDir);
		}
		secureDir.mkdirs();

		removeOtherPermission(secureFolder);
		
		//confFolder
		File confDir = new File(confFolder);
		if(!confDir.exists()) {
			confDir.mkdirs();
		}
		
		removeOtherPermission(confFolder);
	}
	
	private static void removeOtherPermission(String dir) {
		Path path = Paths.get(dir);
		try {
			PosixFileAttributes attr = Files.readAttributes(path, PosixFileAttributes.class);
			Set<PosixFilePermission> permissions= attr.permissions();
			
			permissions.remove(PosixFilePermission.OTHERS_EXECUTE);
			permissions.remove(PosixFilePermission.OTHERS_READ);
			permissions.remove(PosixFilePermission.OTHERS_WRITE);
			permissions.remove(PosixFilePermission.GROUP_EXECUTE);
			permissions.remove(PosixFilePermission.GROUP_READ);
			permissions.remove(PosixFilePermission.GROUP_WRITE);
			
			Files.setPosixFilePermissions(path, permissions);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//when we leave the application, delete the source folder and its files
	public static void destroy() {
		removeSourceDir();
	}
	
	protected static void removeSourceDir() {
		if(DEBUG) Log.Debug(TAG, "removeSourceDir " + secureFolder);
		File secureDir = new File(secureFolder);
		if(secureDir.exists()) {
			deleteDir(secureDir);
		}
	}
	
	protected static String getVaultPath(int id) {
		return Env.secureFolder+'/' + "Vault"+ id;
	}
	
	//delete directories recursively
	protected static void deleteDir(File file) {
		if (file.isDirectory()) {
		    File[] files = file.listFiles();
		    for (File f : files) {
		        deleteDir(f);
		    }
		}
	    file.delete();
	}
}
