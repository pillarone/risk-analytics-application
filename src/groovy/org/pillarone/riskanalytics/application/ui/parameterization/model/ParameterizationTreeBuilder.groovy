package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.DynamicComposedComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

import java.lang.reflect.Field
import org.pillarone.riskanalytics.application.ui.util.ComponentUtils

class ParameterizationTreeBuilder {

    ConfigObject structure
    Model model
    ITableTreeNode root
    Map componentNodes = [:]
    Parameterization item



    def ParameterizationTreeBuilder() {
    }

    public ParameterizationTreeBuilder(Model model, ModelStructure modelStructure, Parameterization parameterization) {
        init(model, modelStructure, parameterization)
        createComponentNodes()
    }

    protected void createComponentNodes() {
        model.injectComponentNames()
        model.properties.each {key, value ->
            if (value instanceof Component) {
                createDynamicSubComponents(value, key)
                buildComponentNode(key, value)
            }
        }
        buildTree()
        buildComponentNodeHierachy()
    }

    private void createDynamicSubComponents(Component component, String componentPath) {
        if (component instanceof DynamicComposedComponent) {
            def parameters = getItemsParameters(componentPath).collect {ParameterHolder param ->
                param.path.substring(componentPath.length() + 1, param.path.indexOf(":", componentPath.length() + 1))
            }.unique()
            component.clear()
            for (String name in parameters) {
                Component subComponent = component.createDefaultSubComponent()
                subComponent.name = name
                component.addSubComponent(subComponent)
            }
        }

        component.properties.each {String key, val ->
            if (key.startsWith("sub")) {
                createDynamicSubComponents(val, "$componentPath:$key".toString())
            }
        }
    }

    protected void init(Model model, ModelStructure modelStructure, Parameterization parameterization) {
        structure = modelStructure.data
        this.@model = model
        this.@item = parameterization
        root = new SimpleTableTreeNode(model.getName())
    }

    private void buildComponentNodeHierachy() {
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
        }
        List sortedPropertyKeys = TreeBuilderUtil.collectProperties(model)
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


     ITableTreeNode buildComponentNode(String propertyName, Component component) {
        if (!component.hasParameters()) {
            if (!(component instanceof DynamicComposedComponent)) {
                return null
            }
        }

        ComponentTableTreeNode componentNode = createComponentNode(propertyName, component)

        def propertyKeys = TreeBuilderUtil.collectProperties(component, 'sub')
        propertyKeys.each {key ->
            if (key.startsWith("sub")) {
                def subComponent = component[key]
                if (subComponent.name == null) {
                    subComponent.name = key

                }
                def node = buildComponentNode(key, subComponent)
                if (node)
                    componentNode.add(node)
            }
        }
        return componentNode
    }


    protected void buildTree() {
        model.properties.each {k, v ->
            if (v instanceof Component) {
                addParameterValueNodes(componentNodes[v])
            }
        }
    }

    protected void addParameterValueNodes(ComponentTableTreeNode componentNode) {
        if (componentNode == null) {
            return
        }

        def props = new TreeMap(new ParmComparator(TreeBuilderUtil.collectProperties(componentNode.component, 'parm')))
        props.putAll(componentNode.component.properties)
        for (Map.Entry<String, Object> entry in props){
            if (entry.key.startsWith('sub')) {
                addParameterValueNodes(componentNodes[entry.value])
            } else if (entry.key.startsWith('parm')) {
                String path = ComponentUtils.removeModelFromPath("${componentNode.path}:${entry.key}".toString(), model)
                if (item.hasParameterAtPath(path)) {
                    componentNode.add(ParameterizationNodeFactory.getNode(path, item, model))
                } else {
                    throw new IllegalArgumentException("No parameter found at $path")
                }
            }
        }
    }

    private String buildParameterPath(ComponentTableTreeNode node) {
        StringBuffer buffer = new StringBuffer(node.name)
        ITableTreeNode currentNode = node.parent
        while (currentNode != null && currentNode instanceof ComponentTableTreeNode) {
            buffer.insert(0, currentNode.name + ":")
            currentNode = currentNode.parent
        }
        return buffer.toString()
    }

    //TODO: is this sufficient? probably not enough for nested dynamic components

    public void removeParameterFromNodes(SimpleTableTreeNode node) {
        node.childCount.times {
            def subNode = node.getChildAt(it)
            if (subNode instanceof ParameterizationTableTreeNode) {
                subNode.parametrizedItem.getParameterHoldersForAllPeriods(subNode.parameterPath).each {
                    item.removeParameter(it)
                }
                return
            }
            removeParameterFromNodes(subNode)
        }

    }

    void createNewComponentNode(SimpleTableTreeNode parent, ComponentTableTreeNode newComponentNode) {
        parent.add(newComponentNode)
        addParameterValueNodes(newComponentNode)
    }

    public int getPeriodCount() {
        return item.periodCount
    }

    protected ComponentTableTreeNode createComponentNode(String propertyName, Component component) {
        ComponentTableTreeNode componentNode = new ComponentTableTreeNode(component, propertyName)
        componentNodes[component] = componentNode

        return componentNode
    }

    protected ComponentTableTreeNode createComponentNode(String propertyName, DynamicComposedComponent component) {
        ComponentTableTreeNode componentNode = new DynamicComposedComponentTableTreeNode(component, propertyName)
        componentNodes[component] = componentNode

        return componentNode
    }

    protected List getItemsParameters(String componentPath) {
        List parameters = item.parameters.findAll {ParameterHolder p ->
            p.path.startsWith(componentPath)
        }
        return parameters
    }

}

class CompareParameterizationTreeBuilder extends ParameterizationTreeBuilder {

    List<Parameterization> parameterizations
    int minPeriod = -1



    public CompareParameterizationTreeBuilder(model, modelStructure, parameterization, parameterizations) {
        init(model, modelStructure, parameterization)
        this.parameterizations = parameterizations;
        minPeriod = ParameterizationUtilities.getMinPeriod(parameterizations)

        createComponentNodes()
    }



    protected void addParameterValueNodes(ComponentTableTreeNode componentNode) {
        if (componentNode == null) {
            return
        }

        def props = new TreeMap(new ParmComparator(TreeBuilderUtil.collectProperties(componentNode.component, 'parm')))
        props.putAll(componentNode.component.properties)
        props.each {name, value ->
            if (name.startsWith('sub')) {
                addParameterValueNodes(componentNodes[value])
            } else if (name.startsWith('parm')) {
                String path = ComponentUtils.removeModelFromPath("${componentNode.path}:$name".toString(), model)
                if (parameterizations.any {it.hasParameterAtPath(path) }) {
                    componentNode.add(ParameterizationNodeFactory.getCompareParameterizationTableTreeNode(path, parameterizations, model, parameterizations.size()))
                }
            }
        }
    }

    protected List getItemsParameters(String componentPath) {
        List resultList = []
        parameterizations.each {Parameterization parameterization ->
            List temp = parameterization.getParameters().findAll {ParameterHolder p ->
                p.path.startsWith(componentPath)
            }.toList()
            temp.each {
                if (!resultList.contains(it))
                    resultList << it
            }
        }
        return resultList
    }


}

class ParmComparator implements Comparator {

    private List declaredFields

    public ParmComparator(List declaredFields) {
        this.declaredFields = declaredFields
    }

    public int compare(Object o1, Object o2) {
        return declaredFields.indexOf(o1) < declaredFields.indexOf(o2) ? -1 : 1
    }
}

class TreeBuilderUtil {

    protected static List collectProperties(Model model) {
        return model.sortedProperties
    }

    public static List collectProperties(def component, String key) {
        return getSortedProperties(component, key)
    }

    public static List collectDynamicProperties(def component, String key) {
        return getSortedProperties(component, key)
    }

    private static List getSortedProperties(def component, String key) {
        List sortedProps = []

        Class currentClass = component.class
        while (currentClass.name != Object.name) {
            List superProps = []
            currentClass.declaredFields.each {Field field ->
                if (field.name.startsWith(key)) {
                    superProps << field.name
                }
            }
            sortedProps.addAll(0, superProps)
            currentClass = currentClass.superclass
        }
        return sortedProps
    }

    protected static List collectProperties(DynamicComposedComponent component, String key) {
        return new TreeSet(component.properties.keySet()).toList()
    }
}
