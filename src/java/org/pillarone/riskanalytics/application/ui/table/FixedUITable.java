package org.pillarone.riskanalytics.application.ui.table;

import com.ulcjava.base.client.UITable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import java.awt.*;

//
// Workarount for PMO-919: Headings Lost by Undocking Result Window (Tree View)
//

public class FixedUITable extends UITable {

    protected Object createBasicObject(Object[] arguments) {
        return new BasicTable() {
            protected void configureEnclosingScrollPane() {
                Container p = getParent();
                if (p instanceof JViewport) {
                    Container gp = p.getParent();
                    if (gp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) gp;
                        JViewport viewport = scrollPane.getViewport();
                        if (viewport == null || viewport.getView() != this) {
                            return;
                        }
                        // Only set the column header if not already set (i.e., on server side).
                        JViewport columnHeader = scrollPane.getColumnHeader();
                        if (columnHeader == null || columnHeader.getView() == null) {
                            scrollPane.setColumnHeaderView(getTableHeader());
                        }
                        Border border = scrollPane.getBorder();
                        if (border == null || border instanceof UIResource) {
                            scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
                        }
                    }
                }
            }
        };
    }
}
