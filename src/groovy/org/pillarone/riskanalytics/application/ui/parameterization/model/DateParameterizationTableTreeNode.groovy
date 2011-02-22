package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.joda.time.DateTime

class DateParameterizationTableTreeNode extends ParameterizationTableTreeNode {

    public DateParameterizationTableTreeNode(List parameter) {
        super(parameter);
    }


    public void setValueAt(Object value, int column) {
        parameter.get(column - 1)?.value = new DateTime(value)
    }

    public Object getExpandedCellValue(int column) {
        return parameter.get(column - 1)?.businessObject?.toDate()
    }

}
