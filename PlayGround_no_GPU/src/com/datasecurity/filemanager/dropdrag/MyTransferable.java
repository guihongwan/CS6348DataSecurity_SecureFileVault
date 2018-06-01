package com.datasecurity.filemanager.dropdrag;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


import com.datasecurity.filemanager.browser.FolderNode;

public class MyTransferable implements Transferable {
    private FolderNode node;

    MyTransferable(FolderNode treeNode) {
        this.node = treeNode;
    }

    static DataFlavor flavors[] = { DataFlavor.stringFlavor };

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
         if(node.getFile().isFile()) {
        	     return true;
         }else {
        	     return false;
         }
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {

        return node.getFile().toString();

    }

}
