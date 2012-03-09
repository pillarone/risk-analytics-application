package org.pillarone.riskanalytics.application.ui.resource.model

import org.codehaus.groovy.grails.commons.GrailsClassUtils
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
    Closure structure

    public ResourceTreeBuilder(Resource resource) {
        init(resource)
        if (structure != null) {
            ResourceTreeBuilderClosureDelegate delegate = new ResourceTreeBuilderClosureDelegate(item)
            structure.delegate = delegate
            structure.resolveStrategy = Closure.DELEGATE_ONLY
            structure.call()

            root = delegate.root
        } else {
            addParameterValueNodes()
        }
    }


    protected void init(Resource resource) {
        this.item = resource
        this.resource = resource.resourceInstance.resource
        structure = GrailsClassUtils.getStaticPropertyValue(resource.modelClass, "structure") as Closure
    }

    protected void addParameterValueNodes() {
        root = new SimpleTableTreeNode(new ResourceParameterHolder.NameVersionPair(item.name, item.versionNumber.toString()).toString())
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

