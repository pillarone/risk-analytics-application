package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ResourceParameterHolder
import org.pillarone.riskanalytics.core.ResourceDAO


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
        ParameterHolder holder = parameter[i-1]
        holder.value = lookupMap[o]
    }

    @Override
    Object getExpandedCellValue(int column) {
        ResourceParameterHolder holder = parameter[column-1]
        if (holder != null) {
            return new ResourceParameterHolder.NameVersionPair(holder.name, holder.version).toString()
        }
        return null
    }
}
