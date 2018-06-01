package com.datasecurity.filemanager.browser;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

class FileSystemModel implements TreeModel {
	boolean DEBUG = false;
    I_fileSystem theRoot;
    TreeModelListener mTreeModelListener;
    
    char fileType = I_fileSystem.ALL;

    public FileSystemModel(I_fileSystem fs) {
        theRoot = fs;
    }

    public Object getRoot() {
    	    if(DEBUG) System.out.println("getRoot");
        return theRoot;
    }
    public void setRoot(I_fileSystem root) {
    	if(DEBUG) System.out.println("setRoot");
        this.theRoot = root;
    }

    public Object getChild(Object parent, int index) {
        return ((I_fileSystem) parent).getChild(fileType, index);
    }

    public int getChildCount(Object parent) {
    	if(DEBUG) System.out.println("getChildCount");
        return ((I_fileSystem) parent).getChildCount(fileType);
    }

    public boolean isLeaf(Object node) {
    	//System.out.println("isLeaf");
        return ((I_fileSystem) node).isLeaf(fileType);
    }

    public int getIndexOfChild(Object parent, Object child) {
    	if(DEBUG) 	System.out.println("getIndexOfChild");
        return ((I_fileSystem) parent).getIndexOfChild(fileType, child);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    	if(DEBUG) System.out.println("valueForPathChanged");
    }

    public void addTreeModelListener(TreeModelListener l) {
    	if(DEBUG) 	System.out.println("addTreeModelListener");
       	mTreeModelListener = l;
    }

    public void removeTreeModelListener(TreeModelListener l) {
    	if(DEBUG) System.out.println("removeTreeModelListener");
    }
}