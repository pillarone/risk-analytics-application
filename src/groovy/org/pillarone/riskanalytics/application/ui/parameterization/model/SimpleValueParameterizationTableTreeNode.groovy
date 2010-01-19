package org.pillarone.riskanalytics.application.ui.parameterization.model

class SimpleValueParameterizationTableTreeNode extends ParameterizationTableTreeNode {

    public SimpleValueParameterizationTableTreeNode(List parameter) {
        super(parameter);
    }


    public void setValueAt(Object value, int column) {
        parameter.get(column - 1)?.value = value
    }

    public Object getExpandedCellValue(int column) {
        return parameter.get(column - 1)?.businessObject
    }


}
