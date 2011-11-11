package org.pillarone.riskanalytics.application.ui.pivot.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer

class PreviewTableTreeModel extends AbstractTableTreeModel {

    String[] columnNames

    PreviewNode root

    PreviewTableTreeModel (String[] columnNames, PreviewNode root) {
        this.root = root
        this.columnNames = columnNames
    }

    int getColumnCount() {
        return columnNames.length
    }

    String getColumnName(int i) {
        return columnNames[i]
    }

    Object getValueAt(Object o, int i) {
        return (o as PreviewNode).getValueAt(i)
    }

    Object getRoot() {
        return root
    }

    Object getChild(Object o, int i) {
        return (o as PreviewNode).getChildAt(i)
    }

    int getChildCount(Object o) {
        return (o as PreviewNode).getChildCount()
    }

    boolean isLeaf(Object o) {
        return (o as PreviewNode).isLeaf()
    }

    int getIndexOfChild(Object o, Object o1) {
        return (o as PreviewNode).getIndex(o1)
    }

}
