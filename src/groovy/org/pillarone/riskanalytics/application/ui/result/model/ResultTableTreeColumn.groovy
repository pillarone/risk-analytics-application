package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.result.view.StochasticValueTableTreeCellRenderer

class ResultTableTreeColumn extends ULCTableTreeColumn {

    IFunction function
    boolean display = true

    public ResultTableTreeColumn(int modelIndex, def tableTree) {
        super(modelIndex)
        cellRenderer = new StochasticValueTableTreeCellRenderer(modelIndex, tableTree)
    }

    public ResultTableTreeColumn(int modelIndex, def tableTree, IFunction function) {
        this(modelIndex, tableTree)
        this.function = function
    }


}