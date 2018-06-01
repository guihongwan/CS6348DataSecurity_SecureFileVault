package com.datasecurity.filemanager.browser;
import javax.swing.JList;

//we don't use it, keep it for future use
public class FileList extends JList {
	private static final long serialVersionUID = 1L;
	
	FileListModel dataModel;

    public FileList() {
        dataModel = new FileListModel();
        setModel(dataModel);
        setCellRenderer(new MyCellRenderer());
    }

    public void fireTreeSelectionChanged(I_fileSystem node) {
        dataModel.setNode(node);
        updateUI();
    }
}
