package com.datasecurity.filemanager.encryption;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * 
 * public static String getNewSalt()
 * public String getEncryptedPassword(String password, String salt)
 * 
 * @author Data Security Group
 *
 */

public class PasswordEncrypter {
	
	/**
	 * 
	 * @return salt
	 */
	public static String getNewSalt() {
        	SecureRandom random=null; // Don't use Random!
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        // NIST recommends minimum 4 bytes. We use 8.
        byte[] salt = new byte[8];       
        random.nextBytes(salt);
        
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 
     * @param password
     * @param salt
     * @return encrypted password
     */
    //Get a encrypted string using PBKDF2 hash algorithm
    public static String getEncryptedPassword(String password, String salt){
        String algorithm = "PBKDF2WithHmacSHA1";
        int derivedKeyLength = 160; // for SHA1
        int iterations = 20000; // NIST specifies 10000
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, iterations, derivedKeyLength);
        
        SecretKeyFactory f=null;
        byte[] encBytes = null;
        
		try {
			f = SecretKeyFactory.getInstance(algorithm);
			encBytes = f.generateSecret(spec).getEncoded();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

        return Base64.getEncoder().encodeToString(encBytes);
    }
}
