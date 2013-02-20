package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ParameterNotFoundException
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareParameterizationTableTreeNode extends ParameterizationTableTreeNode {

    ParameterizationTableTreeNode parameterizationTableTreeNode

    List<ParametrizedItem> itemsToCompare = []
    private int columnCount

    public CompareParameterizationTableTreeNode(String path, List<ParametrizedItem> items, int columnCount, Model model) {
        super(path, items[0])
        this.itemsToCompare = items
        this.parameterizationTableTreeNode = ParameterizationNodeFactory.getNode(path, items.find { !it.getParameterHoldersForAllPeriods(path).empty }, model)
        this.columnCount = columnCount
    }

    boolean isCellEditable(int i) {
        return false
    }

    public void setValueAt(Object o, int i) {
    }

    @Override
    Object getExpandedCellValue(int column) {
        if (itemsToCompare[getParameterizationIndex(column)].hasParameterAtPath(parameterPath, getPeriodIndex(column))) {
            return doGetExpandedCellValue(column)
        }
        return null
    }

    Object doGetExpandedCellValue(int column) {
        parameterizationTableTreeNode.parametrizedItem = itemsToCompare[getParameterizationIndex(column)]
        return parameterizationTableTreeNode.getExpandedCellValue(getPeriodIndex(column) + 1)
    }

    int getParameterizationIndex(int column) {
        if (column == 0)
            return 0
        return (column - 1) % columnCount
    }

    int getPeriodIndex(int column) {
        if (column == 0)
            return 0
        return (column - 1) / columnCount
    }

    ParameterHolder getParameterHolder(int column) {
        try {
            final List<ParameterHolder> periodList = itemsToCompare[getParameterizationIndex(column)].getParameterHoldersForAllPeriods(parameterPath)
            if (periodList == null) {
                return null
            }
            return periodList[getPeriodIndex(column)]
        } catch (ParameterNotFoundException e) {
            //this parameter does not exist in all the compared parameterizations
            return null
        }
    }

    public String getDisplayName() {
        return parameterizationTableTreeNode.getDisplayName()
    }

    public String getToolTip() {
        return parameterizationTableTreeNode.getToolTip()
    }


}
