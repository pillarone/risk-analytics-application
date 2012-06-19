package org.pillarone.riskanalytics.application.ui.base.action

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.util.UIUtils

import java.text.NumberFormat

class TreeNodeCopier extends ResourceBasedAction {

    static String space = " "

    ULCTableTree rowHeaderTree
    ULCTableTree viewPortTree
    ITableTreeModel model
    List columnOrder
    boolean copyWithPath = false

    public TreeNodeCopier() {
        super("Copy")
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, false));
    }

    public TreeNodeCopier(boolean copyWithPath) {
        super(copyWithPath ? "CopyWithPath" : "Copy")
        if (!copyWithPath) {
            putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, false));
        }
        this.copyWithPath = copyWithPath
    }

    public void doActionPerformed(ActionEvent event) {
        ArrayList nodes = new ArrayList(rowHeaderTree.selectedPaths*.lastPathComponent)
        Collections.sort(nodes, new TreeNodeComparator(model.root))

        StringBuffer content = new StringBuffer()
        int columnCount = model.columnCount

        columnOrder = [0]

        content.append(writeHeader())
        nodes?.each {def node ->
            content.append(writeNode(node, columnCount))
        }
        ULCClipboard.getClipboard().content = content.toString()
    }

    protected String writeHeader() {
        StringBuffer line = new StringBuffer()
        if (copyWithPath)
            line << UIUtils.getText(TreeNodeCopier, "path") + "\t"
        line << rowHeaderTree.getColumnModel().getColumn(0).getHeaderValue()
        line << "\t"

        viewPortTree.getColumnModel().getColumns().each {
            line << it.getHeaderValue() << "\t"
            columnOrder << it.modelIndex
        }

        line.delete(line.size() - 1, line.size())

        line << "\n\n"

        return line.toString()
    }

    protected String writeNode(ITableTreeNode node, int columnCount) {
        StringBuffer line = new StringBuffer()
        appendNode(node, line, columnCount, 0)
        return line.toString()
    }

    protected def appendNode(ITableTreeNode node, StringBuffer line, int columnCount, int currentDepth) {
        List valueStrings = columnOrder.collect {columnIndex ->
            format(model.getValueAt(node, columnIndex))
        }

        if (copyWithPath)
            line.append(node.path.toString() + getNodeName(node, valueStrings) + "\t")

        currentDepth.times {
            line.append(space)
        }
        line.append(valueStrings.join("\t"))
        line.append("\n")

        currentDepth++
        node.childCount.times {
            appendNode(node.getChildAt(it), line, columnCount, currentDepth)
        }
    }

    protected String format(Object o) {
        if (o == null) {
            return ""
        }
        return o.toString()
    }

    protected String format(Number n) {
        if (n == null) {
            return ""
        }
        return copyFormat.format(n)
    }

    protected getCopyFormat() {
        NumberFormat format = NumberFormat.getInstance(ClientContext.getLocale())
        format.setMaximumFractionDigits(10)
        format.groupingUsed = false
        return format
    }

    String getNodeName(def node, List valueStrings) {
        if (!valueStrings || valueStrings.size() < 2 || "".equals(valueStrings.get(1)) || !node.properties.keySet().contains("name"))
            return ""
        return ":" + node.name
    }
}