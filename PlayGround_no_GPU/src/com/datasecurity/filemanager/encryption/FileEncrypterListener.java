package com.datasecurity.filemanager.encryption;

import java.io.File;

/**
 * 
 * notify others that there are files encrypted or decrypted
 * 
 * @author wanguihong
 *
 */
public interface FileEncrypterListener
{
	public void fileEncryted(File file);
	public void fileDecryted(File file);
}