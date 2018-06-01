package com.datasecurity.filemanager.encryption;

 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
 
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.datasecurity.filemanager.Utils.FileHelper;
import com.datasecurity.filemanager.Utils.Log;
 
/**
 * A class that encrypts or decrypts a file.
 * 
 * public void encryp()
 * public void decrypt()
 * 
 *
 */
public class FileEncrypter {
	private static final String TAG = "FileEncrypter";
	private static final boolean DEBUG = true;
	
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    
    private FileEncrypterListener listener = null;
 
    public void encrypt(String key, File inputFile, File outputFile)
            throws Exception {
    	    boolean quiet = false;//don't notify the listener
    	    
    	    if(outputFile.exists()) {
    	    	    quiet = true;
    	    }
    	    //create the path for outputfile
    	    FileHelper.makeDir(outputFile);
    	    
    	    //encrypt
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
        if(listener != null && !quiet) {
            listener.fileEncryted(outputFile);
        }
        
        if(DEBUG) Log.Debug(TAG, "encrypted " + outputFile.getName());
    }
 
    public void decrypt(String key, File inputFile, File outputFile)
            throws Exception {
    	
       	FileHelper.makeDir(outputFile);
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
        if(listener != null) {
            listener.fileDecryted(outputFile);
        }
        
        if(DEBUG) Log.Debug(TAG, "decrypted " + outputFile);
    }
    
    public String getCryptKey(String password) {
    	    String encrypt_key="";
        if(password.getBytes().length < 16) {
           	encrypt_key = padRight(password, 16);
        } else {
           	encrypt_key = password.substring(0, 16);
        }
        
        return encrypt_key;
    }
    
    private String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);  
   }
 
    private void doCrypto(int cipherMode, String key, File inputFile,
            File outputFile) throws Exception {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
             
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new Exception("Error encrypting/decrypting file", ex);
        }
    }
    
	public void setListener(FileEncrypterListener listener) {
		this.listener = listener;
	}
}

