package com.datasecurity.filemanager.vault;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.datasecurity.filemanager.Utils.Log;

/**
 * 
 * @author wanguihong April/10/2018
 *
 */
public class CsvFile {
	private static final boolean DEBUG = false;
	private static final String TAG = "CsvFile";
	
	private final String COMMA_DELIMITER = ",";
    private final String NEW_LINE_SEPARATOR = "\n";
    
    private static final int DATA_ID = 0;
	private static final int DATA_VAULTSALT = 1;
	private static final int DATA_ENCYPTED_PASSWORD = 2;
	private static final int DATA_STORAGE_DIR = 3;
	
    //private final String FILE_HEADER = "id,vaultSalt,encryptedPassword,storageDir";
    
    private final String filename;

    public CsvFile(String filename) {
    	    this.filename = filename;
    }

	
	public void write(List<Vault> metadatas) {
		FileWriter fileWriter = null;
		try {
			fileWriter  = new FileWriter(filename);
			//fileWriter.append(NEW_LINE_SEPARATOR);
			for (Vault metadata : metadatas) {
				fileWriter.append(String.valueOf(metadata.getId()));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(metadata.getVaultSalt()));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(metadata.getEncryptedPassword()));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(metadata.getStorageDir()));
				fileWriter.append(COMMA_DELIMITER);
				
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			    try {
					fileWriter.flush();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	public ArrayList<Vault> read() {
		ArrayList<Vault> metadatas = new ArrayList<Vault>();
		File mFile = new File(this.filename);
		if(!mFile.exists()) return metadatas;
		BufferedReader fileReader = null;
		try {
		    fileReader = new BufferedReader(new FileReader(filename));
			//fileReader.readLine();//skip the header
			String line = "";
			while((line = fileReader.readLine()) != null) {
				if(DEBUG) Log.Debug(TAG, line);
				String[] tokens = line.split(this.COMMA_DELIMITER);
				if(tokens.length > 0) {
					Vault metadata = new Vault();
					metadata.setId(Integer.parseInt(tokens[DATA_ID]));
					metadata.setVaultSalt(tokens[DATA_VAULTSALT]);
					metadata.setEncryptedPassword(tokens[DATA_ENCYPTED_PASSWORD]);
					metadata.setStorageDir(tokens[DATA_STORAGE_DIR]);
				
					metadatas.add(metadata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return metadatas;
	}
}
