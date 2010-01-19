package org.pillarone.riskanalytics.application.ui.result.view

import org.pillarone.riskanalytics.application.dataaccess.function.IFunction

interface IFunctionListener {

    void functionAdded(IFunction function)

    void functionRemoved(IFunction function)

    void refreshNodes()

}