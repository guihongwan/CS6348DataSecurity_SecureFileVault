package com.datasecurity.filemanager.dropdrag;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import com.datasecurity.filemanager.Utils.Log;
import com.datasecurity.filemanager.browser.FileTree;

public class MyTargetListener implements DropTargetListener {
	private static final String TAG = "MyTargetListener";
	private static final boolean DEBUG = false;
	
    public void dragEnter(DropTargetDragEvent event) { }

    public void dragOver(DropTargetDragEvent event) { }

    public void dropActionChanged(DropTargetDragEvent event) { }

    public void dragExit(DropTargetEvent dte) { }

    public void drop(DropTargetDropEvent event) {
    	    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        Transferable tr = event.getTransferable();
        
        String filepath = "";
        try {
            if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                Object o = tr.getTransferData(DataFlavor.javaFileListFlavor); 
                filepath = o.toString();
                
                if (filepath.startsWith("[")) {  
                    filepath = filepath.substring(1);  
                }  
                if (filepath.endsWith("]")) {  
                    filepath = filepath.substring(0, filepath.length() - 1);  
                }
                
                if(DEBUG) Log.Debug(TAG, "filepath : " + filepath);
            }
            
        } catch (Exception ex) {
           	ex.printStackTrace();
        }
        DropTarget target = (DropTarget) event.getSource();
        
        Component target_component = target.getComponent();
        if(target_component instanceof FileTree) {
        	    FileTree ftree = (FileTree)target_component;
        	    ftree.getListener().fileDroped(filepath);
        }
        event.dropComplete(true);
        //if(DEBUG) Log.Debug(TAG, "target_component : " + target_component);
    }
}