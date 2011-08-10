package org.pillarone.riskanalytics.application.ui.parameterization.model



/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
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

    public String getToolTip() {
        return parameterizationTableTreeNode.getToolTip()
    }


}
