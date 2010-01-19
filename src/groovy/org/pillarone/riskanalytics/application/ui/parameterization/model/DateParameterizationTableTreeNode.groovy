package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameter.Parameter
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.parameter.DateParameterHolder

class DateParameterizationTableTreeNode extends ParameterizationTableTreeNode {
    private Locale locale

    public DateParameterizationTableTreeNode(List parameter) {
        super(parameter);
        locale = UIUtils.getClientLocale()
    }


    public void setValueAt(Object value, int column) {
        parameter.get(column - 1)?.value = new DateTime(value)
    }

    public Object getExpandedCellValue(int column) {
        return parameter.get(column - 1)?.businessObject?.toDate()
    }


}
