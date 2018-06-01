package com.datasecurity.filemanager.vault;


import com.datasecurity.filemanager.encryption.PasswordEncrypter;

public class VaultAuthentication {
	
    public static boolean authenticateVault(Vault vault, String inputPass){
    	
        if (vault == null) {
            return false;
        } else {
            String salt = vault.getVaultSalt();
            String calculatedHash = PasswordEncrypter.getEncryptedPassword(inputPass,salt);
            if (calculatedHash.equals(vault.getEncryptedPassword())) {
                return true;
            } else {
                return false;
            }
        }
    }
}
