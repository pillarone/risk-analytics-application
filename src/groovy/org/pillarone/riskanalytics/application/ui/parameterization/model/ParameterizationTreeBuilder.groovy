package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import java.lang.reflect.Field
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.DynamicComposedComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.components.DynamicMultiPhaseComposedComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory

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
            List parameters = getItemsParameters(componentPath).collect {ParameterHolder param ->
                param.path.substring(componentPath.length() + 1, param.path.indexOf(":", componentPath.length() + 1))
            }.unique().toList()
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


    protected ITableTreeNode buildComponentNode(String propertyName, Component component) {
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
        props.each {name, value ->
            if (name.startsWith('sub')) {
                addParameterValueNodes(componentNodes[value])
            } else if (name.startsWith('parm')) {
                def cNodePath = getPathValue("${componentNode.path}:$name".toString())
                List p = item.getParameters(cNodePath)
                if (!p.empty) {
                    componentNode.add(ParameterizationNodeFactory.getNode(p, model))
                } else {
                    //new dynamic subcomponent
                    List parameters = []
                    periodCount.times {int periodIndex ->
                        if (value instanceof Cloneable) value = value.clone()
                        ParameterHolder holder = ParameterHolderFactory.getHolder("${buildParameterPath(componentNode)}:$name", periodIndex, value)
                        item.addParameter(holder)
                        parameters << holder
                    }
                    componentNode.add(ParameterizationNodeFactory.getNode(parameters, model))
                }
            }
        }
    }

    String getPathValue(String path) {
        if (path && path.startsWith(model.name))
            return path.substring(model.name.length() + 1)
        return path
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
                subNode.parameter.each {
                    item.removeParameter(it)
                }
                return
            }
            removeParameterFromNodes(subNode)
        }

    }

    ComponentTableTreeNode createNewComponentNode(SimpleTableTreeNode parent, Component newComponent) {
        ComponentTableTreeNode newComponentNode = buildComponentNode(newComponent.name, newComponent)
        parent.add(newComponentNode)
        addParameterValueNodes(newComponentNode)
        return newComponentNode
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
        }.toList()
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
                def nodevalue = componentNodes[value]
                addParameterValueNodes(nodevalue)
            } else if (name.startsWith('parm')) {
                Map parametersMap;
                def node
                try {
                    parametersMap = getParametersList(componentNode, name)
                    node = getNode(parametersMap)
                    componentNode.add(node)
                } catch (Exception ex) {}
            }
        }
    }



    private Map getParametersList(componentNode, name) {
        Map parametersMap = [:]
        parameterizations.eachWithIndex {it, int index ->
            List list = it.getParameters("${componentNode.path}:$name".toString())
            parametersMap.put(index, list)
        }
        return parametersMap
    }

    private ITableTreeNode getNode(Map parametersMap) {
        return ParameterizationNodeFactory.getCompareParameterizationTableTreeNode(parametersMap, model, parameterizations.size())
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
