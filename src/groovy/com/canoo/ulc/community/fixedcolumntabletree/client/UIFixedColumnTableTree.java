/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.canoo.ulc.community.fixedcolumntabletree.client;

/*
 * Copyright � 2000-2005 Canoo Engineering AG, Switzerland.
 */

import com.ulcjava.base.client.UIScrollPane;
import com.ulcjava.base.client.tabletree.JTableTree;
import com.ulcjava.base.client.tabletree.TableTreeColumn;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class UIFixedColumnTableTree extends UIScrollPane {

    protected void postInitializeState() {
        super.postInitializeState();
        final JTableTree rowHeader = (JTableTree) getBasicScrollPane().getRowHeader().getComponent(0);
        JTableTree viewPort = (JTableTree) getBasicScrollPane().getViewport().getComponent(0);
        rowHeader.addTreeExpansionListener(new MyExpansionListener(viewPort));
        rowHeader.addTreeSelectionListener(new MySelectionListener(viewPort, rowHeader));
        viewPort.addTreeSelectionListener(new MySelectionListener(rowHeader, viewPort));

        Dimension preferredSize = getRowHeaderDimension(rowHeader);
        getBasicScrollPane().getRowHeader().setPreferredSize(preferredSize);

        final PropertyChangeListener rowHeaderSizeAdaptor = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if ("width".equals(evt.getPropertyName())) {
                    Dimension preferredSize = getRowHeaderDimension(rowHeader);
                    getBasicScrollPane().getRowHeader().setPreferredSize(preferredSize);
                }
            }
        };
        for (int i = 0; i < rowHeader.getBasicTable().getColumnModel().getColumnCount(); i++) {
            rowHeader.getBasicTable().getColumnModel().getColumn(i).addPropertyChangeListener(rowHeaderSizeAdaptor);
        }
    }

    private Dimension getRowHeaderDimension(JTableTree rowHeader) {
        int rowHeaderWidth = 0;
        for (int i = 0; i < rowHeader.getColumnCount(); i++) {
            TableTreeColumn basicColumn = rowHeader.getColumnModel().getColumn(i);
            rowHeaderWidth += basicColumn.getPreferredWidth();
        }

        // height is not accounted by scroll pane layout
        Dimension preferredSize = new Dimension(rowHeaderWidth, -1);
        return preferredSize;
    }


    /**
     * Used to expand/collapse the elements of the second table, following
     * the first one.
     */
    private static class MyExpansionListener implements TreeExpansionListener {
        private JTableTree fTableTree;

        public MyExpansionListener(JTableTree source) {
            fTableTree = source;
        }

        public void treeCollapsed(TreeExpansionEvent event) {
            fTableTree.collapsePath(event.getPath());
        }

        public void treeExpanded(TreeExpansionEvent event) {
            fTableTree.expandPath(event.getPath());
        }
    }

    /**
     * Used to synchronize the selections of the two tableTrees
     */
    private class MySelectionListener implements TreeSelectionListener {
        private JTableTree fTarget;
        private JTableTree fSource;

        public MySelectionListener(JTableTree target, JTableTree source) {
            fTarget = target;
            fSource = source;
        }

        public void valueChanged(TreeSelectionEvent event) {
            if (event.isAddedPath()) { //not called during clearing
                if (fSource.getCellSelectionEnabled() || fSource.getColumnSelectionAllowed()) {
                    fTarget.clearSelection();
                    if (fTarget.getColumnCount() > 0) {
                        fTarget.removeColumnSelectionInterval(0, fTarget.getColumnCount() - 1);
                        fTarget.scrollCellToVisible(event.getPath(), 0);
                    }
                } else if (fSource.getRowSelectionAllowed()) {
                    int[] selectedRows = fSource.getSelectedRows();
                    for (int i = 0; i < selectedRows.length; i++) {
                        fTarget.setRowSelectionInterval(selectedRows[i], selectedRows[i]);
                    }
                }
            }
        }
    }


}
