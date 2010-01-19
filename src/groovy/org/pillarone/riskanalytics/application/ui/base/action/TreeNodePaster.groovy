package org.pillarone.riskanalytics.application.ui.base.action

import com.canoo.ulc.community.ulcclipboard.server.IClipboardHandler
import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.TableDataParser
import org.pillarone.riskanalytics.application.ui.util.TableTreeMutator
import org.pillarone.riskanalytics.application.ui.util.UIUtils

class TreeNodePaster extends ExceptionSafeAction {
    ULCTableTree tree

    public TreeNodePaster() {
        super("Paste")
        putValue(IAction.SMALL_ICON, UIUtils.getIcon("paste-active.png"));
    }

    public void doActionPerformed(ActionEvent event) {

        ULCClipboard.appyClipboardContent([applyContent: {String content ->
            ExceptionSafe.protect {

                ArrayList data = new TableDataParser().parseTableData(content)

                int startColumn = tree.selectedColumn + 1
                int startRow = tree.selectedRow

                List nodes = []
                for (int row = startRow; row < (startRow + data.size()); row++) {
                    nodes << tree.getPathForRow(row).lastPathComponent
                }
                TableTreeMutator mutator = new TableTreeMutator(tree.model)
                try {
                    mutator.applyChanges(nodes, data, startColumn)
                } catch (IllegalArgumentException e) {
                    if (!e.message == "Attempt to set read-only cell") {
                        throw e
                    }
                }
            }
        }] as IClipboardHandler)

    }
}