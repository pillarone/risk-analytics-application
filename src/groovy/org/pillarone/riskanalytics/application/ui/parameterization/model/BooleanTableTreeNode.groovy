package org.pillarone.riskanalytics.application.ui.parameterization.model


class BooleanTableTreeNode extends AbstractMultiValueParameterizationTableTreeNode {

    public BooleanTableTreeNode(List parameters) {
        super(parameters)
    }

    public void setValueAt(Object value, int column) {
//        value = getKeyForValue(value)
        parameter.get(column - 1)?.value = value
    }

    public Object getExpandedCellValue(int column) {
        String value = parameter.get(column - 1)?.businessObject?.toString()
        return Boolean.valueOf(value)
    }

    public List initValues() {
        return [Boolean.TRUE, Boolean.FALSE]
    }
}
