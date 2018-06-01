package com.datasecurity.filemanager.dropdrag;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.datasecurity.filemanager.Utils.Log;
import com.datasecurity.filemanager.browser.FolderNode;

public class MyDragGestureListener implements DragGestureListener {
	private static final String TAG = "MyDragGestureListener";
	private static final boolean DEBUG = false;
	
	@Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        //store data in Transferable and call startDrag() to initiate
        JTree tree = (JTree) dge.getComponent();
        TreePath path = tree.getSelectionPath();
        
        if (path != null) {
            FolderNode selection = (FolderNode) path.getLastPathComponent();
            if(selection.getFile().isDirectory()) return;
            
            MyTransferable dragAndDropTransferable = new MyTransferable(selection);
            
            dge.startDrag(DragSource.DefaultCopyDrop, dragAndDropTransferable,
                    new MySourceListener());
            
            if(DEBUG) Log.Debug(TAG, selection.getFile().toString());
            
        }
    }

}