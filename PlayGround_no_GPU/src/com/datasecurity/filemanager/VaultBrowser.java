package com.datasecurity.filemanager;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;

import com.datasecurity.filemanager.Utils.FileHelper;
import com.datasecurity.filemanager.Utils.Log;
import com.datasecurity.filemanager.browser.FileTree;
import com.datasecurity.filemanager.browser.FileTreeListener;
import com.datasecurity.filemanager.browser.FolderNode;
import com.datasecurity.filemanager.dropdrag.MyDragGestureListener;
import com.datasecurity.filemanager.dropdrag.MyTargetListener;
import com.datasecurity.filemanager.encryption.FileEncrypter;
import com.datasecurity.filemanager.encryption.FileEncrypterListener;

/**
 * 
 * Left side: for the encrypted files
 * Right side: for the original files
 *
 * @author wanguihong
 *
 */

public class VaultBrowser extends JPanel implements FileTreeListener,FileEncrypterListener{
	private boolean DEBUG = false;
	private String TAG = "VaultBrowser";
	
	private static final long serialVersionUID = 1L;
	
	private static int LEFT_WIDTH = 350;
	private static int RIGHT_WIDTH = 350;
	private static int WINDOW_HEIGHT = 300;
	private int id;
	private VaultBrowserListener listener;
	private FileTree tree_source;
	private FileTree tree_encrypted;
	
    public VaultBrowser(int _id) {
    	    id = _id;
        setPreferredSize(new Dimension(800, 600));
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setLayout(new BorderLayout());

        //FileList list = new FileList();
        //list.setDoubleBuffered(true);
        //JScrollPane listView = new JScrollPane(list);
        //listView.setPreferredSize(new Dimension(RIGHT_WIDTH, WINDOW_HEIGHT));

        //Encrypted Folder View
        tree_encrypted = new FileTree(VaultManager.entries.get(id).getStorageDir(), null);
        tree_encrypted.setDoubleBuffered(true);
        JScrollPane treeView_encrypted = new JScrollPane(tree_encrypted);
        treeView_encrypted.setPreferredSize(new Dimension(LEFT_WIDTH, WINDOW_HEIGHT));
        tree_encrypted.setListener(this);
        
        //original Folder View
        tree_source = new FileTree(VaultManager.entries.get(id).getSourceDir(), null);
        tree_source.setDoubleBuffered(true);
        JScrollPane treeView_source = new JScrollPane(tree_source);
        treeView_source.setPreferredSize(new Dimension(RIGHT_WIDTH, WINDOW_HEIGHT));
        tree_source.setListener(this);
//        tree_source.setEditable(true);
//        tree_source.setShowsRootHandles(true);
        
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView_encrypted, treeView_source);
 
        pane.setDividerLocation(LEFT_WIDTH);
        pane.setDividerSize(4);

        add(pane);
        
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(tree_encrypted,
                DnDConstants.ACTION_COPY, new MyDragGestureListener()); // 建立拖拽源和事件的联系
        
        new DropTarget(tree_source, new MyTargetListener());
    }

	@Override
	public void mouseDaboutClicked(String filepath) {
		File openFile = new File(filepath);
		Desktop desktop = Desktop.getDesktop();
	    try {
		    desktop.open(openFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean fileDroped(String filepath) {
		File inputFile = new File(filepath);
		
		if(VaultManager.entries.get(id).getPassword() == null) {
			if(listener != null) {
	  	        listener.nullPassword();
	        }
			return false;
		}
        if(filepath.startsWith(VaultManager.entries.get(id).getStorageDir())) {
        	    //decrypt the file
			
			String postfilename = FileHelper.getRelativePath(VaultManager.entries.get(id).getStorageDir(), filepath);
			String str_outputFile = VaultManager.entries.get(id).getSourceDir()+postfilename;
			
		    if(DEBUG) Log.Debug(TAG, "str_outputFile "+str_outputFile);
		    
		    File outputFile = new File(str_outputFile);
		    
			try {
				FileEncrypter encrypter = new FileEncrypter();
				encrypter.setListener(this);
				encrypter.decrypt(encrypter.getCryptKey(VaultManager.entries.get(id).getPassword()), inputFile, outputFile);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {//file comes from outside
			String newPath = VaultManager.entries.get(id).getSourceDir() + "/"+inputFile.getName();
	        FileHelper.copyFile(filepath, newPath);
	        tree_source.dataChanged(new FolderNode(VaultManager.entries.get(id).getSourceDir()));
		}
        
		return true;
	}
	
	public void setListener(VaultBrowserListener listener) {
		this.listener = listener;
	}

	@Override
	public void fileEncryted(File file) {
		String path = VaultManager.entries.get(id).getStorageDir();
		//if(DEBUG) 
			Log.Debug(TAG, "call  dataChanged en " + path);
		
		tree_encrypted.dataChanged(new FolderNode(path));
	}

	@Override
	public void fileDecryted(File file) {
		String path = VaultManager.entries.get(id).getSourceDir();
		//if(DEBUG) 
			Log.Debug(TAG, "call  dataChanged de " + path);
		tree_source.dataChanged(new FolderNode(path));
	}
	
}
