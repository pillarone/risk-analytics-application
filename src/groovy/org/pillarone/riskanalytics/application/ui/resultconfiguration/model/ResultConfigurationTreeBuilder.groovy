package org.pillarone.riskanalytics.application.ui.resultconfiguration.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.TreeBuilder
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParmComparator
import org.pillarone.riskanalytics.application.ui.parameterization.model.TreeBuilderUtil
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.PacketCollector
import org.pillarone.riskanalytics.core.packets.MultiValuePacket
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.packets.SingleValuePacket
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.util.GroovyUtils

class ResultConfigurationTreeBuilder extends TreeBuilder {

    Map resultDescriptorNodes = [:]

    public ResultConfigurationTreeBuilder(Model model, ModelStructure structure, ResultConfiguration configuration) {
        super(model, structure, configuration)
        notifyTreeComplete(root)
    }

    protected ITableTreeNode buildComponentNode(Component component) {
        buildComponentNode(component, component.name)
    }

    private void notifyTreeComplete(ITableTreeNode node) {
        for (int i = 0; i < node.childCount; i++) {
            notifyTreeComplete(node.getChildAt(i))
        }
    }

    protected ITableTreeNode buildComponentNode(Component component, String propertyName) {
        if (!hasCollectableOutput(component)) {
            return null
        }
        ComponentTableTreeNode componentNode = new ComponentTableTreeNode(component, model.class, propertyName)
        componentNodes[component] = componentNode

        List componentProperties = TreeBuilderUtil.collectDynamicProperties(componentNode.component, 'out')
        componentProperties.addAll(TreeBuilderUtil.collectDynamicProperties(componentNode.component, 'sub'))
        componentProperties.addAll(TreeBuilderUtil.collectProperties(componentNode.component, 'out'))
        def treeSet = new TreeSet(new ParmComparator(componentProperties))
        treeSet.addAll(component.properties.keySet())
        treeSet.each { String k ->
            if (k.startsWith("sub")) {
                ComponentTableTreeNode node = buildComponentNode(component[k], k)
                if (node != null) {
                    componentNode.add(node)
                }
            } else if (k.startsWith("out")) {
                if (isSingleOrMultiValuePacket(component[k])) {
                    componentNode.add(new ResultConfigurationTableTreeNode(k, item, component[k].type))
                }
            }
        }

        return componentNode
    }

    protected ITableTreeNode buildComponentNode(DynamicComposedComponent dynamicComposedComponent, String propertyName) {
        if (dynamicComposedComponent.allSubComponents().empty) {
            Component component = dynamicComposedComponent.createDefaultSubComponent()
            component.name = "sub${dynamicComposedComponent.genericSubComponentName}"
            dynamicComposedComponent.addSubComponent(component)
        }
        buildComponentNode(dynamicComposedComponent as Component, propertyName)
    }

    Collection<PacketCollector> getCollectors() {
        (item as ResultConfiguration).collectors
    }

    private boolean isSingleOrMultiValuePacket(PacketList packetList) {
        return ((MultiValuePacket.isAssignableFrom(packetList.getType())) || (SingleValuePacket.isAssignableFrom(packetList.getType())))
    }

    private boolean hasCollectableOutput(Component component) {
        boolean result = false
        if (component instanceof DynamicComposedComponent) {
            result = hasCollectableOutput(component.createDefaultSubComponent())
        }

        GroovyUtils.getProperties(component).each { String name, value ->
            if (name.startsWith("out")) {
                if (value instanceof PacketList) {
                    if (isSingleOrMultiValuePacket(value)) {
                        result = true
                    }
                }
            } else if (name.startsWith("sub")) {
                boolean subComponentResult = hasCollectableOutput(value)
                if (subComponentResult) {
                    result = true
                }
            }
        }

        return result
    }
}