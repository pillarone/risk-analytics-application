package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.parameter.Parameter

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */

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

