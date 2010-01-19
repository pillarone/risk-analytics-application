package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.parameter.Parameter

import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent

class ParameterObjectParameterTableTreeNode extends ParameterizationTableTreeNode {


    public ParameterObjectParameterTableTreeNode(List parameter) {
        super(parameter);
    }

    public boolean isCellEditable(int column) {
        false
    }

    public void setValueAt(Object value, int column) {

    }

    public Object getExpandedCellValue(int column) {
        ""
    }

    public String getDisplayName() {
        String value = null
        if (!parent instanceof DynamicComposedComponent) {
            Parameter parameter = parameter.find {it -> it != null }
            String parameterType = parameter.type.parameterType
            value = I18NUtils.findParameterTypeDisplayName(parameterType)
        }
        if (value == null) {
            value = super.getDisplayName()
        }
        return value
    }


}