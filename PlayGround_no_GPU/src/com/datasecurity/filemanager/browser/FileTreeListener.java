package com.datasecurity.filemanager.browser;

public interface FileTreeListener {
	public void mouseDaboutClicked(String filepath);
	public boolean fileDroped(String filepath);
}
