package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.ITableTreeNode

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ResultConfigurationNodeFilter implements ITableTreeFilter {

    List values
    int column

    public ResultConfigurationNodeFilter(List values, int column) {
        this.values = values;
        this.column = column
    }

    boolean acceptNode(ITableTreeNode node) {
        return false
    }


}
