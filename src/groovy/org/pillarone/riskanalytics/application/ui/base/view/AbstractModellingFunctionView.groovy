package org.pillarone.riskanalytics.application.ui.base.view

import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingTreeView
import org.pillarone.riskanalytics.application.ui.result.view.IFunctionListener

/**
 * Created by IntelliJ IDEA.
 * User: fja
 * Date: 05-Nov-2009
 * Time: 17:30:17
 * To change this template use File | Settings | File Templates.
 */

abstract public class AbstractModellingFunctionView extends AbstractModellingTreeView implements IFunctionListener {

    public AbstractModellingFunctionView(model) {
        super(model);
    }

    public void refreshNodes() {
        removeColumns()
        addColumns()
        nodeChanged()
    }

    public void functionAdded(IFunction function) {
    }

    public void functionRemoved(IFunction function) {
    }

}