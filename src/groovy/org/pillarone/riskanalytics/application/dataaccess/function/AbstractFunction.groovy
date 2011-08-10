package org.pillarone.riskanalytics.application.dataaccess.function

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.SimulationRun

abstract class AbstractFunction implements IFunction {

    private String displayName

    protected Log LOG = LogFactory.getLog(AbstractFunction)

    String getDisplayName() {
        if (displayName == null) {
            try {
                final ResourceBundle bundle = LocaleResources.getBundle("org.pillarone.riskanalytics.application.actionResources")
                displayName = bundle.getString(getName() + ".name")
            } catch (MissingResourceException e) {
                displayName = getName()
                LOG.error("Resource not found for function ${getName()}")
            }
        }

        return displayName
    }

    //TODO: remove this method?
    def evaluate(SimulationRun simulationRun, int periodIndex, SimpleTableTreeNode node) {
        return evaluate(new SimulationRunHolder(simulationRun), periodIndex, node)
    }
}
