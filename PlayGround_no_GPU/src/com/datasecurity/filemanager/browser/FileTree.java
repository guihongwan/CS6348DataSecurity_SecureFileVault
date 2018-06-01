package com.datasecurity.filemanager.browser;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.datasecurity.filemanager.Utils.Log;

public class FileTree extends JTree {
	private static final String TAG = "FileTree";
	private static final boolean DEBUG = true;
	
    static final long serialVersionUID = 0;

    private FileList theList;
    private FileSystemModel dataModel;
    private String path;
    private FileTreeListener listener;

    public FileTree(String _path, FileList list) {
    	    path = _path;
        dataModel = new FileSystemModel(new FolderNode(_path));
        //dataModel.addTreeModelListener(treeModelListener);
        
        setModel(dataModel);
        this.setCellRenderer(new FolderRenderer());
        
        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent tse) {
            	
            	    TreePath tp = tse.getNewLeadSelectionPath();
                Object o = tp.getLastPathComponent();
                FolderNode node = (FolderNode) o;
            	    //System.out.println("TreeSelectionListener"+node.toString());
            }
        });
        addFocusListener( new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				
				FileTree tree = (FileTree)e.getSource();
				//System.out.println("focusGained " + tree.getPath());
			}

			@Override
			public void focusLost(FocusEvent e) {
				FileTree tree = (FileTree)e.getSource();
				//System.out.println("focusLost " + tree.getPath());
			}
        	
        });
        this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
		            FileTree tree = (FileTree)e.getSource();
		            Object o  = tree.getLastSelectedPathComponent();
	                FolderNode node = (FolderNode) o;
	                
	            	    if(listener != null) {
	            	    	    listener.mouseDaboutClicked(node.getFile().toString());
	            	    }
	            	    
		        }
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        this.setSelectionRow(0);
        
        theList = list;
    }

    public void fireValueChanged(TreeSelectionEvent tse) {
    	
        
    }

    public void fireTreeCollapsed(TreePath path) {
        super.fireTreeCollapsed(path);
        
        TreePath curpath = getSelectionPath();
        if (path.isDescendant(curpath)) {
            setSelectionPath(path);
        }
        
    }

    public void fireTreeWillExpand(TreePath path) {
        //System.out.println("Path will expand is " + path);
        
    }

    public void fireTreeWillCollapse(TreePath path) {
        //System.out.println("Path will collapse is " + path);
    }

    public String getPath() {
		return path;
	}

	public void setListener(FileTreeListener listener) {
		this.listener = listener;
	}
	public FileTreeListener getListener() {
		return this.listener;
	}

	class ExpansionListener implements TreeExpansionListener {
        FileTree tree;

        public ExpansionListener(FileTree ft) {
            tree = ft;
        }

        public void treeCollapsed(TreeExpansionEvent tee) {
        }

        public void treeExpanded(TreeExpansionEvent tee) {
        }
    }
	
	public void dataChanged(FolderNode node) {
		if(DEBUG) Log.Debug(TAG, "dataChanged "+node.getFile().toString());
		dataModel = new FileSystemModel(node);
        setModel(dataModel);
        updateUI();
    }

}
