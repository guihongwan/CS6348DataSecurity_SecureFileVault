package com.datasecurity.filemanager.browser;
import javax.swing.Icon;

interface I_fileSystem {
	
    final public static char DIRECTORY = 'D';
    final public static char FILE = 'F';
    final public static char ALL = 'A';

    public Icon getIcon();
    public I_fileSystem getChild(char fileType, int index);
    public int getChildCount(char fileType);
    public boolean isLeaf(char fileType);
    public int getIndexOfChild(char fileType, Object child);
    
}