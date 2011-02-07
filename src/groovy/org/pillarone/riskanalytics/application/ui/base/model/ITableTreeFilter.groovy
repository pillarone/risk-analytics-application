package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.WorkflowParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultStructureTableTreeNode

interface ITableTreeFilter {

    boolean acceptNode(ITableTreeNode node)

}

class NodeNameFilter implements ITableTreeFilter {

    String nodeName

    public NodeNameFilter(nodeName) {
        this.nodeName = nodeName;
    }

    public boolean acceptNode(ITableTreeNode node) {
        node ? internalAcceptNode(node) : false
    }

    boolean internalAcceptNode(ITableTreeNode node) {
        return acceptNode(node.parent)
    }

    boolean internalAcceptNode(ComponentTableTreeNode node) {
        return nodeName ? nodeName == node.displayName || acceptNode(node.parent) : true
    }

    boolean internalAcceptNode(DynamicComposedComponentTableTreeNode node) {
        return nodeName ? nodeName == node.displayName || acceptNode(node.parent) : true
    }

    boolean internalAcceptNode(ResultStructureTableTreeNode node) {
        return nodeName ? nodeName == node.displayName || acceptNode(node.parent) : true
    }

}

class ParameterizationNodeFilter implements ITableTreeFilter {

    public static final int ALL = -1
    public static final int WITHOUT_COMMENTS = 0
    public static final int WITH_COMMENTS = 1
    public String displayValue

    List values
    int column
    boolean allSelected = false

    public ParameterizationNodeFilter(List values, int column) {
        this.values = values;
        this.column = column
    }

    public ParameterizationNodeFilter(List values, int column, boolean all) {
        this(values, column)
        this.@allSelected = all
    }

    public boolean acceptNode(ITableTreeNode node) {
        return node ? internalAcceptNode(node) : false;
    }

    boolean internalAcceptNode(ParameterizationNode node) {
        if (allSelected || !values || values.size() == 0) return true
        return contains(node.values[column])
    }

    boolean internalAcceptNode(WorkflowParameterizationNode node) {
        if (allSelected || !values || values.size() == 0) return true
        return contains(node.values[column])
    }

    boolean internalAcceptNode(ITableTreeNode node) {
        return true
    }

    private boolean contains(String value) {
        boolean found = false
        if (column == ModellingInformationTableTreeModel.NAME) {
            for (String name: values) {
                if (value != null && value.indexOf(name) != -1) {
                    found = true
                    break
                }
            }
        } else if (column == ModellingInformationTableTreeModel.TAGS) {
            for (String tag: values) {
                if (value != null && value.indexOf(tag) != -1) {
                    found = true
                    break
                }
            }
        } else {
            found = values?.contains(value);
        }
        return found
    }

    private boolean contains(Long value) {
        contains(value.intValue())
    }

    private boolean contains(int value) {
        switch (values[0]) {
            case ALL: displayValue = "All"; return true
            case WITHOUT_COMMENTS: displayValue = "Without comments"; return value == 0
            case WITH_COMMENTS: displayValue = "With comments"; return value >= 1
        }
    }

    private boolean contains(Date value) {
        return values.find { it == ModellingInformationTableTreeModel.simpleDateFormat.format(value)} != null
    }

}