package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.TreeBuilder
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.packets.MultiValuePacket
import org.pillarone.riskanalytics.core.packets.Packet
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.packets.SingleValuePacket
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class ResultTreeBuilder extends TreeBuilder {

    ConfigObject resultDescriptorConfigObject
    Map resultDescriptorNodes = [:]


    public ResultTreeBuilder(Model model, String structureFilename) {
        super(model, structureFilename)
    }

    public ResultTreeBuilder(Model model, ModelStructure structure, Simulation simulation) {
        super(model, structure, simulation)
    }

    private List getSubComponentCount(ComponentTableTreeNode node) {
        List paths = ResultAccessor.getPaths(item?.simulationRun)
        String path = "${node.path}"

        def subComponents = paths.findAll {String it ->
            int start = it.indexOf(path) + path.length() + 1
            it.contains(path) && it.indexOf(":", start) > 0
        }.collect {String s ->
            int start = s.indexOf(path) + path.length() + 1
            s.substring(start, s.indexOf(":", start))
        }
        return subComponents.unique().toList()
    }

    protected ITableTreeNode buildComponentNode(Component component, String nodeName) {
        ComponentTableTreeNode componentNode = new ComponentTableTreeNode(component, nodeName)
        componentNodes[component] = componentNode

        if (component instanceof DynamicComposedComponent) {
            List componentsToAdd = getSubComponentCount(componentNode)
            DynamicComposedComponent composedComponent = component
            composedComponent.clear()
            componentsToAdd.each {
                Component subComponent = composedComponent.createDefaultSubComponent()
                subComponent.name = it
                composedComponent.addSubComponent(subComponent)
            }
        }

//        List sortedPropertyKeys = collectProperties(component, 'out')
//        sortedPropertyKeys.addAll(collectProperties(component, 'sub'))
        List sortedPropertyKeys = collectProperties(component, 'sub')
        sortedPropertyKeys.addAll(collectProperties(component, 'out'))
        sortedPropertyKeys.each {String key ->
            if (key.startsWith("sub")) {
                def subComponent = component[key]
                componentNode.add(buildComponentNode(subComponent, key))
            } else if (key.startsWith("out")) {
                SimpleTableTreeNode channelNode = new SimpleTableTreeNode(key)
                componentNode.add(channelNode)
                addValueNodes(channelNode, component[key], component)
            }
        }
        return componentNode
    }

    void addValueNodes(SimpleTableTreeNode channelNode, PacketList value, Component component) {
        def valueTypeInstance = value.type.newInstance()
        List valueNodes = createValueNodes(valueTypeInstance)
        valueNodes.each {
            channelNode.add(it)
        }
    }


    List createValueNodes(Packet valueTypeInstance) {
        []
    }

    private static final Map primitiveToWrapper = [
            (Double.TYPE): Double,
            (Integer.TYPE): Integer
    ]

    List createValueNodes(MultiValuePacket valueTypeInstance) {
        List propertyList = []
        for (String fieldName: valueTypeInstance.getFieldNames()) {
            propertyList.add(new ResultTableTreeNode(fieldName))
        }
        propertyList
    }

    List createValueNodes(SingleValuePacket valueTypeInstance) {
        [new ResultTableTreeNode(valueTypeInstance.valueLabel, Packet.class)]
    }


    void applyResultPaths() {
        traverseTree(root, ResultAccessor.getPaths(item?.simulationRun), root.name)
    }

    private def traverseTree(SimpleTableTreeNode node, List resultPaths, String pathPrefix) {
        List nodesToRemove = []
        node.childCount.times {
            SimpleTableTreeNode child = node.getChildAt(it)
            String prefix = pathPrefix + ":${child.name}"
            if (resultPaths.any {path -> path.startsWith("$prefix:") || path.equals(prefix) }) {
                traverseTree(child, resultPaths, prefix)
            } else {
                if (!(child instanceof ResultTableTreeNode)) {
                    nodesToRemove << child
                }
            }
        }
        nodesToRemove.each {node.remove(it)}
    }
}
