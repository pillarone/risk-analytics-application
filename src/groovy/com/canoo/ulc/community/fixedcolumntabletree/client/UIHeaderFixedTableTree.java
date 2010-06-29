package com.canoo.ulc.community.fixedcolumntabletree.client;

import com.ulcjava.base.client.UITableTree;
import com.ulcjava.base.client.tabletree.JTableTree;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIHeaderFixedTableTree extends UITableTree {

    @Override
    protected Object createBasicObject(Object[] objects) {
        return new BasicHeaderFixedTableTree();
    }

    public class BasicHeaderFixedTableTree extends UITableTree.BasicTableTree {


        protected void configureEnclosingScrollPane() {
            fixConfigureTableEnclosingScrollPane(this);
        }

        protected void fixConfigureTableEnclosingScrollPane(JComponent tableOrTableTree) {
            assert tableOrTableTree instanceof JTable || tableOrTableTree instanceof JTableTree : "Must be a table or a table tree.";
            Container parent = tableOrTableTree.getParent();
            if (parent instanceof JViewport) {
                Container grandParent = parent.getParent();
                if (grandParent instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) grandParent;
                    JViewport viewport = scrollPane.getViewport();
                    if (viewport == null || viewport.getView() != tableOrTableTree) {
                        return;
                    }
                    // Only set the column header if not already set (i.e., on server side).
                    JViewport columnHeader = scrollPane.getColumnHeader();
                    if (columnHeader == null || columnHeader.getView() == null) {
                        final JTableHeader tableHeader;
                        if (tableOrTableTree instanceof JTableTree) {
                            tableHeader = ((JTableTree) tableOrTableTree).getBasicTable().getTableHeader();
                        } else {
                            tableHeader = ((JTable) tableOrTableTree).getTableHeader();
                        }
                        scrollPane.setColumnHeaderView(tableHeader);
                    }
                    Border border = scrollPane.getBorder();
                    if (border == null || border instanceof DefaultTableCellRenderer.UIResource) {
                        Border scrollPaneBorder = UIManager.getBorder("Table.scrollPaneBorder");
                        if (scrollPaneBorder != null) {
                            scrollPane.setBorder(scrollPaneBorder);
                        }
                    }
                }
            }
        }
    }

}
