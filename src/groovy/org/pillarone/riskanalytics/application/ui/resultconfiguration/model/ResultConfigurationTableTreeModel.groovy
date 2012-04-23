package org.pillarone.riskanalytics.application.ui.resultconfiguration.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.TableTreeValueChangedListener

class ResultConfigurationTableTreeModel extends AbstractTableTreeModel {

    ITableTreeNode root
    int columnCount
    private List valueChangedListeners = []
    Boolean readOnly = false


    protected ResultConfigurationTableTreeModel(ITableTreeNode root, int columnCount) {
        this.root = root
        this.columnCount = columnCount
    }

    void addValueChangedListener(TableTreeValueChangedListener listener) {
        valueChangedListeners.add(listener)
    }

    void removeValueChangedListener(TableTreeValueChangedListener listener) {
        valueChangedListeners.remove(listener)
    }

    void notifyNodeValueChanged(SimpleTableTreeNode node, int column) {
        valueChangedListeners.each {TableTreeValueChangedListener it ->
            it.valueChanged(node, column)
        }
    }

    Object getChild(Object parent, int index) {
        parent.getChildAt(index)
    }

    int getChildCount(Object node) {
        node.childCount
    }

    Object getRoot() {
        root
    }

    boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    Object getValueAt(Object node, int column) {
        node.getValueAt(column)
    }

    int getIndexOfChild(Object parent, Object child) {
        parent.getIndex(child)
    }

    boolean isCellEditable(Object node, int columnIndex) {
        readOnly ? false : columnIndex == 0 ? node.isCellEditable(columnIndex) : node instanceof ResultConfigurationTableTreeNode
    }

    public void setValueAt(Object value, Object node, int column) {
        if (!readOnly) {
            if (getValueAt(node, column) != value) {
                node.setValueAt(value, column)
                notifyNodeValueChanged(node, column)
            }
        }
    }

    public List getLeafsWithValue() {
        return collectLeafsWithValue([], root)
    }

    private List collectLeafsWithValue(List list, ITableTreeNode node) {
        if (node.isLeaf() && node instanceof ResultConfigurationTableTreeNode) {
            boolean hasValue = false
            int i = 1
            while (i < columnCount && !hasValue) {
                if (node.collector) {
                    list << node
                    hasValue = true
                }
                i++
            }
        }

        node.childCount.times {
            collectLeafsWithValue(list, node.getChildAt(it))
        }
        return list
    }
}