package com.datasecurity.filemanager.browser;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

//we don't use it, keep it for future use
class MyCellRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 1L;

	public MyCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list,    Object value,    int index,    boolean isSelected,    boolean cellHasFocus) {
        FolderNode node = (FolderNode) value;
        setIcon(node.getIcon());
        setText(value.toString());
        setBackground(isSelected ? Color.BLUE.darker().darker() : Color.WHITE);
        setForeground(isSelected ? Color.WHITE : Color.BLACK);
        return this;
    }
}