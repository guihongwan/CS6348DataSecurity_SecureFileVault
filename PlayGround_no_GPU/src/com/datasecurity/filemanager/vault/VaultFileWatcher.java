package com.datasecurity.filemanager.vault;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import com.datasecurity.filemanager.Utils.FileHelper;
import com.datasecurity.filemanager.encryption.FileEncrypter;
 
/**
 * 
 * Monitor the the file change in the vault directory.
 * One Vault, one VaultFileWatcher
 * 
 * ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE
 * 
 * if create/modify files, encrypt them into target directory
 * if delete files, also delete from the target file
 * 
 * FileEncryptedListener to listen to file state
 * 
 * @author Data Security Group
 *
 */

public class VaultFileWatcher extends Thread {
	
    private WatchService watcher=null;
    private final Map<WatchKey, Path> keys;//Directory and all of its sub-directories
    
    private Vault vault = null;
    private boolean IsGPU = false;
    
    private FileEncrypter encrypter;

    /**
     * Creates a WatchService and registers the given directory
     */
    public VaultFileWatcher(Vault _vault, FileEncrypter _encrypter, boolean _IsGPU) {
        try {
        	
        	    this.watcher = FileSystems.getDefault().newWatchService();
        	    this.vault = _vault;
        	    this.IsGPU=_IsGPU;
        	    this.encrypter = _encrypter;
        	    
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        this.keys = new HashMap<WatchKey, Path>();
 
        try {
			walkAndRegisterDirectories(new File(vault.getSourceDir()).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
 
    /**
     * Register the given directory, and all its sub-directories, with the WatchService.
     */
    private void walkAndRegisterDirectories(final Path start_path) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start_path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    /**
     * Register the given directory with the WatchService; This function will be called by FileVisitor
     */
    private void registerDirectory(Path dir) throws IOException
    {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }
 
    /**
     * Process all events for keys queued to the watcher
     */
    @Override
    public void run() {
        for (;;) {
            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
 
            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }
 
            for (WatchEvent<?> event : key.pollEvents()) {
                @SuppressWarnings("rawtypes")
                WatchEvent.Kind kind = event.kind();
 
                // Context for directory entry event is the file name of entry
                @SuppressWarnings("unchecked")
                Path name = ((WatchEvent<Path>)event).context();
                Path child = dir.resolve(name);
 
                // print out event
                System.out.format("[VaultFileWatcher] %s: %s\n", event.kind().name(), child);
                
                
                if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                	    // if directory, then register it and its sub-directories
                    try {
                        if (Files.isDirectory(child)) {
                        	
                            walkAndRegisterDirectories(child);
                            
                        } else {
                        	
                        	   encryptFile(child);
                        	   
                        }
                    } catch (IOException x) {
                        // do something useful
                    }
                }
     
                //ENTRY_DELETE
        	        if (kind == ENTRY_DELETE) {
                    	//TODO
                 }
            }
 
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
    private void encryptFile(Path child) {
    	    //encrypt file
        String password = vault.getPassword();
        if( password == null) {
    	        System.err.println("Please enter password for " + vault.getStorageDir());
    	        return;
        }
        
        String encrypt_key = encrypter.getCryptKey(password);
   
        String filepath = child.toString();
        String subfilepath = FileHelper.getRelativePath(vault.getSourceDir(), filepath);
        
        File inputFile = new File(filepath);
        File encryptedFile = new File(vault.getStorageDir()+"/"+subfilepath);
        
        try {
            if(IsGPU) {
		            //no implementation
            	    encrypter.encrypt(encrypt_key, inputFile, encryptedFile);
            } else {
              	encrypter.encrypt(encrypt_key, inputFile, encryptedFile);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
