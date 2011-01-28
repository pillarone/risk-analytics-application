package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.joda.time.DateTime

class DateParameterizationTableTreeNode extends ParameterizationTableTreeNode {

    public DateParameterizationTableTreeNode(List parameter) {
        super(parameter);
    }


    public void setValueAt(Object value, int column) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime((Date) value);
        parameter.get(column - 1)?.value = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), 0,0,0,0)
    }

    public Object getExpandedCellValue(int column) {
        return parameter.get(column - 1)?.businessObject?.toDate()
    }

}
