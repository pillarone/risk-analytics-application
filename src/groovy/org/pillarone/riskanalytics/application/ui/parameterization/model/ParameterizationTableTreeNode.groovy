package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.parameter.Parameter
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

abstract class ParameterizationTableTreeNode extends SimpleTableTreeNode {

    List<ParameterHolder> parameter
    String errorMessage

    public ParameterizationTableTreeNode(List parameter) {
        super(getNodeName(parameter))
        this.parameter = parameter
    }


    boolean isCellEditable(int i) {
        return i == 0 ? false : getExpandedCellValue(i) != null
    }

    abstract public void setValueAt(Object o, int i)

    abstract Object getExpandedCellValue(int column)

    protected static String getNodeName(List parameter) {
        ParameterHolder param = parameter.find {ParameterHolder it -> it != null}
        param.path.substring(param.path.lastIndexOf(':') + 1)
    }

    public String getDisplayName() {
        String value = null
        value = lookUp(value, "")
        if (value == null) {
            value = super.getDisplayName()
        }
        return value
    }

    @Override
    String lookUp(String value, String tooltip) {
        String displayName
        if (parent != null) {
            displayName = findParameterDisplayNameInParentNodes(parent, tooltip)
        }
        return displayName
    }

    private String findParameterDisplayNameInParentNodes(ITableTreeNode node, String toolTip = "") {
        String value = null
        if (node instanceof ComponentTableTreeNode) {
            value = I18NUtils.findParameterDisplayName(node, path.substring(node.path.length() + 1), toolTip)
        } else {
            if (node instanceof ParameterObjectParameterTableTreeNode) {
                value = I18NUtils.findParameterDisplayName(node, path.substring(node.path.length() + 1), toolTip)
            }
            if (value == null && node.parent != null) {
                value = findParameterDisplayNameInParentNodes(node.getParent(), toolTip)
            }
        }
        return value
    }

}

class CompareParameterizationTableTreeNode extends ParameterizationTableTreeNode {

    ParameterizationTableTreeNode parameterizationTableTreeNode

    Map parametersMap = [:]
    int columnsCount

    public CompareParameterizationTableTreeNode(parameter) {
        super(parameter);
    }

    public CompareParameterizationTableTreeNode(parameterizationTableTreeNode, Map parametersMap, int columnsCount) {
        super(parameterizationTableTreeNode.parameter)
        this.parameterizationTableTreeNode = parameterizationTableTreeNode;
        this.parametersMap = parametersMap
        this.columnsCount = columnsCount
    }

    boolean isCellEditable(int i) {
        return false
    }

    public void setValueAt(Object o, int i) {
        parameterizationTableTreeNode.setValueAt(o, i)
    }

    Object getExpandedCellValue(int column) {
        try {
            parameterizationTableTreeNode.parameter = (List) this.parametersMap.get(getParameterizationIndex(column))

            return parameterizationTableTreeNode.getExpandedCellValue(getPeriodIndex(column) + 1)
        } catch (Exception ex) {
            return ""
        }

    }

    protected int getParameterizationIndex(int column) {
        if (column == 0)
            return 0
        return (column - 1) % columnsCount
    }

    protected int getPeriodIndex(int column) {
        if (column == 0)
            return 0
        return (column - 1) / columnsCount
    }


    public static String getNodeName(List parameter) {
        return ParameterizationTableTreeNode.getNodeName(parameter)
    }

    public String getDisplayName() {
        return parameterizationTableTreeNode.getDisplayName()
    }


}

class CompareParameterTableTreeNode extends CompareParameterizationTableTreeNode {

    Map<String, List<Parameter>> parameterEntries = [:]

    public CompareParameterTableTreeNode(parameterizationTableTreeNode, Map<String, List<Parameter>> parameterEntries, int columnsCount) {
        super(parameterizationTableTreeNode.parameter)
        this.parameterizationTableTreeNode = parameterizationTableTreeNode;
        this.parameterEntries = parameterEntries
        this.columnsCount = columnsCount
    }


    Object getExpandedCellValue(int column) {
        try {
            parameterizationTableTreeNode.parameter = this.parameterEntries.get(getParameterizationIndex(column)).get(parameterizationTableTreeNode.name)
            return parameterizationTableTreeNode.getExpandedCellValue(getPeriodIndex(column) + 1)
        } catch (Exception ex) {
            return ""
        }

    }


}
