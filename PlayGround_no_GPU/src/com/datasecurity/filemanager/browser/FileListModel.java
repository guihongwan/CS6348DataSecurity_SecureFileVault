package com.datasecurity.filemanager.browser;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

//we don't use it, keep it for future use
class FileListModel implements ListModel {
	    FileList theList;
	    I_fileSystem node;
	    char fileType = I_fileSystem.ALL;
	    
	    public void setNode(I_fileSystem node) {
	        this.node = node;
	    }

	    public Object getElementAt(int index) {
	        if (node != null) {
	            return ((I_fileSystem) node).getChild(fileType, index);
	        } else {
	            return null;
	        }
	    }

	    public int getSize() {
	        if (node != null) {
	            return ((I_fileSystem) node).getChildCount(fileType);
	        } else {
	            return 0;
	        }
	    }


		@Override
		public void addListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
			
		}
}
