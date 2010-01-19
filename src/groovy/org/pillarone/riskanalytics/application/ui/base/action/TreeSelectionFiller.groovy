package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe

class TreeSelectionFiller implements IActionListener {

    ULCTableTree tree
    ITableTreeModel model

    public void actionPerformed(ActionEvent event) {
        ExceptionSafe.protect {

            int startColumn = tree.getSelectedColumn()

            tree.selectedRows.each {int index ->
                def currentNode = tree.getPathForRow(index).lastPathComponent
                def value = model.getValueAt(currentNode, startColumn + 1)

                tree.selectedColumns.each {int colIndex ->
                    if (colIndex > startColumn) {
                        model.setValueAt(value, currentNode, colIndex + 1)
                    }
                }
            }

        }
    }

}