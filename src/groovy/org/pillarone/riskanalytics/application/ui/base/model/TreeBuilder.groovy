package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import java.lang.reflect.Field
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.StructureInformationInjector
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

abstract class TreeBuilder {

    protected String structureFilename
    protected ConfigObject structure
    protected Model model
    ITableTreeNode root
    protected Map componentNodes = [:]
    ModellingItem item

    //Required for RTB, because it cannot use the constructors below
    TreeBuilder() {
    }

    TreeBuilder(Model model, String structureFilename) {
        this.@structureFilename = structureFilename
        this.@model = model
        structure = new StructureInformationInjector(structureFilename, model).configObject
        root = new SimpleTableTreeNode(model.getClass().getSimpleName() - "Model")
        createComponentNodes()

    }

    TreeBuilder(Model model, ModelStructure modelStructure, ModellingItem item = null) {
        this.@model = model
        this.@item = item
        structure = modelStructure.data
        root = new SimpleTableTreeNode(model.getClass().getSimpleName() - "Model")
        createComponentNodes()

    }

    private def buildComponentNodeHierachy() {
        if (!structure.isEmpty()) {
            structure.company.each {line, value ->
                SimpleTableTreeNode lineNode = new SimpleTableTreeNode(line)
                structure.company[line].components.each {component, propertyValue ->

                    ComponentTableTreeNode componentNode = componentNodes[model[component]]
                    if (componentNode != null) {
                        lineNode.add(componentNode)
                    }
                }
                if (!lineNode.leaf) {
                    root.add(lineNode)
                }
            }
            List sortedPropertyKeys = collectProperties(model)
            for (String prop in sortedPropertyKeys) {
                def value = model[prop]
                if (value instanceof Component) {
                    def node = componentNodes[value]
                    if (node) {
                        if (node.parent == null) {
                            root.add(node)
                        }
                    }
                }
            }
        }
    }


    protected void createComponentNodes() {
        model.init()
        model.injectComponentNames()
        model.properties.each {propertyName, property ->
            if (property instanceof Component) {
                ITableTreeNode iTableTreeNode = buildComponentNode(property, propertyName)
                if (iTableTreeNode != null) {
                    String displayName = I18NUtils.getPropertyDisplayName(model, propertyName)
                    if (displayName != null)
                        iTableTreeNode.cachedDisplayName = displayName
                }

            }
        }
        buildComponentNodeHierachy()
    }


    protected abstract ITableTreeNode buildComponentNode(Component component, String nodeName)

    protected List collectProperties(Model model) {
        List sortedProps = []

        model.class.declaredFields.each {Field field ->
            sortedProps << field.name
        }
        sortedProps
    }

    protected List collectProperties(Component component, String key) {
        List sortedProps = []

        Class currentClass = component.class
        while (currentClass.name != Object.name) {
            List superProps = []
            currentClass.declaredFields.each {Field field ->
                if (field.name.startsWith(key) && field.name != 'outChannels') {
                    superProps << field.name
                }
            }
            sortedProps.addAll(0, superProps)
            currentClass = currentClass.superclass
        }
        sortedProps
    }


    protected List collectProperties(DynamicComposedComponent component, String key) {
        def result = new TreeSet()
        for (String s in component.properties.keySet()) {
            if (s.startsWith(key)) {
                result << s
            }
        }
        return result.toList()
    }


}
