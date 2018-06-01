package com.datasecurity.filemanager.browser;
import java.awt.Component;
import java.io.File;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;

public class FolderNode implements I_fileSystem {
	private static final boolean DEBUG = false;

    private static FileSystemView fsView;
    private static boolean showHiden = true;
    private File theFile;
    private Vector<File> all = new Vector<File>();
    private Vector<File> folder = new Vector<File>();
    
    /**
    * set that whether apply hiden file.
    * @param show
       */
    public void setShowHiden(boolean show) {
        showHiden = show;
    }

    public Icon getIcon() {
        return fsView.getSystemIcon(theFile);
    }
    
    public File getFile() {
    	    return theFile;
    }

    public String toString() {
        return fsView.getSystemDisplayName(theFile);
    }

    /**
    * create a root node. by default, it should be the DeskTop in window file system.
     * @param s 
       */
    public FolderNode(String s) {
        fsView = FileSystemView.getFileSystemView();
        theFile = new File(s);
        prepareChildren();

    }
    
    private void prepareChildren() {
    	    
        File[] files = fsView.getFiles(theFile, showHiden);

        for (int i = 0; i < files.length; i++) {
          	if (DEBUG) System.out.println("file " +files[i] );
            all.add(files[i]);
            if (files[i].isDirectory()    && !files[i].toString().toLowerCase().endsWith(".lnk")) {
                folder.add(files[i]);
            }
        }
    }

    private FolderNode(File file) {
        theFile = file;
        prepareChildren();
    }

    public FolderNode getChild(char fileType, int index) {
        if (I_fileSystem.DIRECTORY == fileType) {
            return new FolderNode(folder.get(index));
        } else if (I_fileSystem.ALL == fileType) {
            return new FolderNode(all.get(index));
        } else if (I_fileSystem.FILE == fileType) {
            return null;
        } else {
            return null;
        }
    }

    public int getChildCount(char fileType) {
        if (I_fileSystem.DIRECTORY == fileType) {
            return folder.size();
        } else if (I_fileSystem.ALL == fileType) {
            return all.size();
        } else if (I_fileSystem.FILE == fileType) {
            return -1;
        } else {
            return -1;
        }
    }

    public boolean isLeaf(char fileType) {
        if (I_fileSystem.DIRECTORY == fileType) {
            return folder.size() == 0;
        } else if (I_fileSystem.ALL == fileType) {
            return all.size() == 0;
        } else if (I_fileSystem.FILE == fileType) {
            return true;
        } else {
            return true;
        }
    }

    public int getIndexOfChild(char fileType, Object child) {
        if (child instanceof FolderNode) {
            if (I_fileSystem.DIRECTORY == fileType) {
                return folder.indexOf(((FolderNode) child).theFile);
            } else if (I_fileSystem.ALL == fileType) {
                return all.indexOf(((FolderNode) child).theFile);
            } else if (I_fileSystem.FILE == fileType) {
                return -1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
}

class FolderRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 1L;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,    boolean hasFocus) {
        I_fileSystem node = (I_fileSystem) value;
        Icon icon = node.getIcon();

        setLeafIcon(icon);
        setOpenIcon(icon);
        setClosedIcon(icon);

        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }
}