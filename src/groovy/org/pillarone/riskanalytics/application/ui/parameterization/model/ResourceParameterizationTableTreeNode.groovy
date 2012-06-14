package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ResourceParameterHolder
import org.pillarone.riskanalytics.core.ResourceDAO
import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException


class ResourceParameterizationTableTreeNode extends AbstractMultiValueParameterizationTableTreeNode {

    private Map<String, ResourceParameterHolder.NameVersionPair> lookupMap = [:]

    ResourceParameterizationTableTreeNode(List parameter) {
        super(parameter)
    }

    @Override
    protected List initValues() {
        ResourceParameterHolder parameter = parameter.find { it != null }
        List<ResourceParameterHolder.NameVersionPair> allValues = ResourceDAO.findAllByResourceClassName(parameter.resourceClass.name).collect { new ResourceParameterHolder.NameVersionPair(it.name, it.itemVersion)}

        allValues.each { lookupMap.put(it.toString(), it)}
        return lookupMap.keySet().sort()
    }

    @Override
    void setValueAt(Object o, int i) {
        ParameterHolder parameterHolder = parameter.get(i - 1)
        if (parameterHolder != null) {
            LOG.debug("Setting value to node @ ${path} P${i - 1}")
            parameterHolder?.value = lookupMap[o]
        } else {
            throw new RiskAnalyticsInconsistencyException("Trying to set value to ${path} P${i - 1}, but parameter holder is null. ${parameter}")
        }
    }

    @Override
    Object getExpandedCellValue(int column) {
        ResourceParameterHolder holder = parameter[column - 1]
        if (holder != null) {
            return new ResourceParameterHolder.NameVersionPair(holder.name, holder.version).toString()
        }
        return null
    }
}
