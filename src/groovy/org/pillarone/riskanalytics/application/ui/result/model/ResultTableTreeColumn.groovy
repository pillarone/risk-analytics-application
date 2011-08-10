package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.result.view.ResultView
import org.pillarone.riskanalytics.application.ui.result.view.StochasticValueTableTreeCellRenderer

class ResultTableTreeColumn extends ULCTableTreeColumn {

    IFunction function
    boolean display = true

    private static Log LOG = LogFactory.getLog(ResultTableTreeColumn)

    public ResultTableTreeColumn(int modelIndex, def tableTree, CommentAndErrorView commentAndErrorView) {
        super(modelIndex)
        cellRenderer = new StochasticValueTableTreeCellRenderer(modelIndex, tableTree, commentAndErrorView)
    }

    public ResultTableTreeColumn(int modelIndex, ResultView resultView, IFunction function) {
        this(modelIndex, resultView.tree.viewPortTableTree, resultView.commentAndErrorView)
        if (LOG.isDebugEnabled()) {
            LOG.debug "Created column for function ${function.getDisplayName()} with model index $modelIndex"
        }
        this.function = function
    }


}