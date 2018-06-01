package com.datasecurity.filemanager;

import java.util.ArrayList;
import java.util.List;

import com.datasecurity.filemanager.Utils.FileHelper;
import com.datasecurity.filemanager.Utils.Log;
import com.datasecurity.filemanager.encryption.PasswordEncrypter;
import com.datasecurity.filemanager.vault.CsvFile;
import com.datasecurity.filemanager.vault.Vault;

/**
 * 
 * Manage all the vaults
 * 
 * createVault()
 * 
 * @author wanguihong
 *
 */
public class VaultManager {
	private static final String TAG = "VaultManager";
	private static final boolean DEBUG = true;
	
	private CsvFile csvFile;//the information of vaults in file
	protected static ArrayList<Vault> entries;//the records of vaults in memory
	
	public VaultManager(String filename) {
		csvFile = new CsvFile(filename);
		entries = csvFile.read();
		
		Vault.setCount(getMaxId());//the id of a new vault start from the count;
		
	}
	
	public int createVault(String pw, String targetDir){
		Vault vault = new Vault();
		
		if(DEBUG) Log.Debug(TAG, "Creating Vault : "+Vault.getCount());
		
		//set vault id
		vault.setId(Vault.getCount());
		
		//set encryption key
		vault.setPassword(pw);
		vault.setVaultSalt(PasswordEncrypter.getNewSalt());
		vault.setEncryptedPassword(PasswordEncrypter.getEncryptedPassword(pw,vault.getVaultSalt()));
		
		String folder = Env.getVaultPath(vault.getId());
		vault.setSourceDir(folder);
		FileHelper.setupFolder(folder);
		vault.setStorageDir(targetDir);
		
		entries.add(vault);
		flush();//write to disc
		return vault.getId();
	}
	
	public void initVaultFolders() {
		for(Vault metadata: entries) {
			String folder = Env.getVaultPath(metadata.getId());
			metadata.setSourceDir(folder);
			FileHelper.setupFolder(folder);
		}
	}

	public void updatePassword(int id, String password) {
		for(Vault metadata: entries) {
			if( metadata.getId() == id) {
				metadata.setPassword(password);
			}
		}
	}
	
	//the password must start with a~z or A~Z and include a~z/A~Z 0~9 !~@
	protected boolean checkPasswordValidity(String password) { //Regular Expressions
        boolean ret = true;
		if(password.length() < 7) return false;
		String reg = "^(?![0-9]+$)(?![^0-9]+$)(?![a-zA-Z]+$)(?![^a-zA-Z]+$)(?![a-zA-Z0-9]+$)[a-zA-Z0-9\\S]+$";
		
		//for test
//		System.out.println("123".matches(reg));//false
//		System.out.println("123@@".matches(reg));//false
//		System.out.println("zzzzzzzzzzz".matches(reg));//false
//		System.out.println("zzzzzzzz@@zzz".matches(reg));//false
//		System.out.println("sss123".matches(reg));//false
//		System.out.println("sgg123@@$@".matches(reg));//true
//		System.out.println("sgg@123".matches(reg));//true
		
		ret = password.matches(reg);

		return ret;
	}
	protected Vault getVaultfromId(int idx) {
		return entries.get(idx);
	}
	
	protected void clearPassword() {
		for(Vault metadata: entries) {
			metadata.setPassword(null);
		}
	}
		
	public void flush() {
		csvFile.write(entries);
	}
	
	private int getMaxId() {
		int max = 0;
		for(Vault metadata: entries) {
			if(max < metadata.getId()) {
				max = metadata.getId();
			}
		}
		return max;
	}
		
	public void printDB() {
		flush();
		List<Vault> list = csvFile.read();
		for(Vault metadata: list) {
			Log.Debug(TAG,  metadata.toString());
		}
		Log.Debug(TAG, "list size: "+list.size());
	}
}
