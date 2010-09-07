package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.IMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.util.I18NUtils

class SimpleTableTreeNode implements IMutableTableTreeNode {
    boolean expanded = true
    protected String name, cachedDisplayName
    List children
    ITableTreeNode parent
    String commentMessage = ""

    static String PATH_SEPARATOR = '/'

    public SimpleTableTreeNode(String name) {
        children = []
        this.@name = name
    }

    String getName() {
        this.@name
    }

    public ITableTreeNode getChildAt(int index) {
        children[index]
    }

    public int getChildCount() {
        children.size()
    }

    public ITableTreeNode getParent() {
        parent
    }

    public int getIndex(ITableTreeNode child) {
        children.indexOf(child)
    }

    public final Object getValueAt(int i) {
        if (i == 0) {
            return getDisplayName()
        } else {
            return getCellValue(i)
        }
    }

    public String getDisplayName() {
        if (cachedDisplayName != null)
            return cachedDisplayName
        String value = name
        //try to get a displayValue from the parent.component
        String displayNameValue = I18NUtils.findResultParameterDisplayName(this, value)
        if (displayNameValue == null)
            displayNameValue = I18NUtils.findDisplayNameByParentComponent(this, value)
        if (displayNameValue == null)
            displayNameValue = I18NUtils.findDisplayNameByParentComponent(this.parent, value)
        if (displayNameValue == null)
            displayNameValue = I18NUtils.formatDisplayName(value)

        if (displayNameValue != null) {
            cachedDisplayName = displayNameValue
        }
        return displayNameValue

    }

    protected Object getCellValue(int column) {
        if (expanded) {
            return getExpandedCellValue(column)
        }
        return getCollapsedCellValue(column)
    }

    public boolean isLeaf() {
        children.size() == 0
    }

    String getPath() {
        if (parent) {
            return "${parent?.path}:$name"
        } else {
            return name
        }
    }

    String getDisplayPath() {
        if (parent) {
            return "${parent.getDisplayPath()} $PATH_SEPARATOR ${getDisplayName()}"
        } else {
            return getDisplayName()
        }
    }

    List getTreePath() {
        if (parent) {
            return parent.treePath << this
        } else {
            return [this]
        }
    }

    String getShortDisplayPath(List otherNodes) {
        List otherNodesCopy = otherNodes.clone()
        otherNodesCopy.remove(this)
        List otherPaths = otherNodesCopy.treePath
        def lastRelatedNode
        String ret = null
        treePath.eachWithIndex {def myParent, int index ->
            otherPaths.each {List otherPath ->
                if (otherPath[index] == myParent) {
                    lastRelatedNode = myParent
                } else if (!ret && lastRelatedNode) {
                    ret = displayPath[(lastRelatedNode.displayPath.size() + 3)..(displayPath.size() - 1)]
                }
            }
        }
        if (!ret) {
            if (parent && otherNodes.size() == 1 && otherNodes[0] == this) {
                ret = "${parent.getDisplayName()} $PATH_SEPARATOR ${getDisplayName()}"
            } else {
                ret = displayPath
            }
        }
        return ret
    }

    int add(ITableTreeNode child) {
        children << child
        child.parent = this
        return childCount - 1
    }

    int remove(ITableTreeNode child) {
        int index = getIndex(child)
        children.remove(index)
        child.parent = null
        return index
    }

    public void setValueAt(Object o, int i) {
        throw new UnsupportedOperationException("Not implemented on simple node")
    }

    public void insert(IMutableTableTreeNode child, int index) {
        children.add(index, child)
        child.parent = this
    }

    public void remove(int childIndex) {
        remove(getChildAt(childIndex))

    }

    public void setParent(IMutableTableTreeNode parent) {
        this.parent = parent
    }

    public boolean isCellEditable(int i) {
        return false
    }


    Object getExpandedCellValue(int column) {
        ""
    }

    Object getCollapsedCellValue(int column) {
        StringBuffer childValues = new StringBuffer()
        children.each {
            def cellValue = it.getExpandedCellValue(column)
            if (cellValue) {
                childValues << cellValue
                childValues << " "
            }
        }
        return childValues.toString().trim()
    }

    SimpleTableTreeNode getChildByName(String name) {
        return children.find {it.name == name}
    }
}