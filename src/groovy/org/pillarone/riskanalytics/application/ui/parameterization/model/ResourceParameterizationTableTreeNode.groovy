package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.ResourceDAO
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ResourceParameterHolder

class ResourceParameterizationTableTreeNode extends AbstractMultiValueParameterizationTableTreeNode {

    private Map<String, ResourceParameterHolder.NameVersionPair> lookupMap = [:]

    ResourceParameterizationTableTreeNode(String path, ParametrizedItem item) {
        super(path, item)
    }

    @Override
    protected List initValues() {
        ResourceParameterHolder parameter = parametrizedItem.getParameterHoldersForAllPeriods(parameterPath)[0]
        List<ResourceParameterHolder.NameVersionPair> allValues = ResourceDAO.findAllByResourceClassName(parameter.resourceClass.name).collect { new ResourceParameterHolder.NameVersionPair(it.name, it.itemVersion)}

        allValues.each { lookupMap.put(it.toString(), it)}
        return lookupMap.keySet().sort()
    }

    @Override
    void setValueAt(Object o, int i) {
        int period = i - 1
        LOG.debug("Setting value to node @ ${parameterPath} P${period}")
        parametrizedItem.updateParameterValue(parameterPath, period, lookupMap[o])
    }

    @Override
    Object doGetExpandedCellValue(int column) {
        ResourceParameterHolder holder = parametrizedItem.getParameterHolder(parameterPath, column - 1)
        if (holder != null) {
            return new ResourceParameterHolder.NameVersionPair(holder.name, holder.version).toString()
        }
        return null
    }
}
