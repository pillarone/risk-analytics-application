package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNodeFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParmComparator
import org.pillarone.riskanalytics.application.ui.parameterization.model.TreeBuilderUtil
import org.pillarone.riskanalytics.core.components.IResource
import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.pillarone.riskanalytics.core.simulation.item.parameter.ResourceParameterHolder

class ResourceTreeBuilder {

    SimpleTableTreeNode root
    Map componentNodes = [:]
    Resource item

    IResource resource

    public ResourceTreeBuilder(Resource resource) {
        init(resource)
        addParameterValueNodes()
    }


    protected void init(Resource resource) {
        this.item = resource
        this.resource = resource.resourceInstance.resource
        root = new SimpleTableTreeNode(new ResourceParameterHolder.NameVersionPair(resource.name, resource.versionNumber.toString()).toString())
    }

    protected void addParameterValueNodes() {
        def props = new TreeMap(new ParmComparator(TreeBuilderUtil.collectProperties(resource, 'parm')))
        props.putAll(resource.properties)
        props.each {String name, value ->
            if (name.startsWith('parm')) {
                List p = item.getParameters(name)
                if (!p.empty) {
                    root.add(ParameterizationNodeFactory.getNode(p, null))
                }
            }
        }
    }


}

