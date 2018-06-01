package com.datasecurity.filemanager.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileHelper {
	private static final String TAG = "FileHelper";
	private static boolean DEBUG = false;
	
	/*
	 * remove prepath from fullpath
	 */
	public static String getRelativePath(String prepath, String fullpath) {
		///Users/wanguihong/Documents/workspace_java/PlayGround/SecureFolder/Vault2/Screen Shot 2018-04-09 at 2.41.02 PM.png
        //remove all the stuff before Vault*
        int len_source = prepath.length();
        return fullpath.substring(len_source);
	}
	
	/*
	 * create the directory for file
	 */
	public static void makeDir(File file) {
		String str_file = file.getAbsolutePath();
	    String str_name = file.getName();
	    
	    String str_path = str_file.substring(0,str_file.length()-str_name.length());
	    if(DEBUG ) Log.Debug(TAG, "str_path " + str_path);
	    
	    File path = new File(str_path);
	    if(!path.exists()) path.mkdirs();
	    
	}
	
	public static void setupFolder(String folder) {
		File file = new File(folder);
		if(!file.exists()) {
			file.mkdirs();
		}
	}
	
	
	public static void copyFile(String oldPath, String newPath) {
		if(DEBUG) Log.Debug(TAG, oldPath + " to " + newPath);
        try {  
            int bytesum = 0;  
            int byteread = 0;
            File oldfile = new File(oldPath);  
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath); 
                FileOutputStream fs = new FileOutputStream(newPath);  
                byte[] buffer = new byte[1444];
                while ( (byteread = inStream.read(buffer)) != -1) {  
                    bytesum += byteread; 
                    fs.write(buffer, 0, byteread);  
                } 
                if(DEBUG) System.out.println(bytesum);
                inStream.close();  
                fs.close();  
            }  
        }  
        catch (Exception e) {
            e.printStackTrace();  
        }  
    }  
}
