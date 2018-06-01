package com.datasecurity.filemanager.vault;

public class Vault {
	private static int count = 0;
	private int id;
	private String password = null;// will not store in disc
	private String encryptedPassword;
	private String vaultSalt;
	private String sourceDir;//will not store in disc
	private String storageDir;// where the encrypted files stored 
	
	public Vault() {
		setCount(getCount() + 1);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public static int getCount() {
		return count;
	}
	public static void setCount(int count) {
		Vault.count = count;
	}
	public String getStorageDir() {
		return storageDir;
	}
	public void setStorageDir(String storageDir) {
		this.storageDir = storageDir;
	}
	public String getVaultSalt() {
		return vaultSalt;
	}
	public void setVaultSalt(String vaultSalt) {
		this.vaultSalt = vaultSalt;
	}
	public String getEncryptedPassword() {
		return encryptedPassword;
	}
	public void setEncryptedPassword(String encyPassword) {
		encryptedPassword = encyPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
   
}
