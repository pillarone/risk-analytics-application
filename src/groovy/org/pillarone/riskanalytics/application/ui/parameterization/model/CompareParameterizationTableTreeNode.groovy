package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareParameterizationTableTreeNode extends ParameterizationTableTreeNode {

    ParameterizationTableTreeNode parameterizationTableTreeNode

    Map<Integer, List<ParameterHolder>> parametersMap = [:]
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

    ParameterHolder getParameterHolder(int column) {
        return parametersMap[getParameterizationIndex(column)][getPeriodIndex(column)]
    }


    public static String getNodeName(List parameter) {
        return ParameterizationTableTreeNode.getNodeName(parameter)
    }

    public String getDisplayName() {
        return parameterizationTableTreeNode.getDisplayName()
    }

    public String getToolTip() {
        return parameterizationTableTreeNode.getToolTip()
    }


}
