package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.result.view.StochasticValueTableTreeCellRenderer
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class ResultTableTreeColumn extends ULCTableTreeColumn {

    IFunction function
    boolean display = true

    private static Log LOG = LogFactory.getLog(ResultTableTreeColumn)

    public ResultTableTreeColumn(int modelIndex, def tableTree) {
        super(modelIndex)
        cellRenderer = new StochasticValueTableTreeCellRenderer(modelIndex, tableTree)
    }

    public ResultTableTreeColumn(int modelIndex, def tableTree, IFunction function) {
        this(modelIndex, tableTree)
        if (LOG.isDebugEnabled()) {
            LOG.debug "Created column for function ${function.getName(0)} with model index $modelIndex"
        }
        this.function = function
    }


}