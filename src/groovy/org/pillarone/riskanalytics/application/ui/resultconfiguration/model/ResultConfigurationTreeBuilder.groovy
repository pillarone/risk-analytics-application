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
        if (node instanceof ResultConfigurationTableTreeNode) {
            node.findCollector()
            return
        }
        for (int i = 0; i < node.childCount; i++) {
            notifyTreeComplete(node.getChildAt(i))
        }
    }

    protected ITableTreeNode buildComponentNode(Component component, String propertyName) {
        ComponentTableTreeNode componentNode = new ComponentTableTreeNode(component, propertyName)
        componentNodes[component] = componentNode

        List componentProperties = TreeBuilderUtil.collectDynamicProperties(componentNode.component, 'out')
        componentProperties.addAll(TreeBuilderUtil.collectDynamicProperties(componentNode.component, 'sub'))
        componentProperties.addAll(TreeBuilderUtil.collectProperties(componentNode.component, 'out'))
        def treeSet = new TreeSet(new ParmComparator(componentProperties))
        boolean rcTableTreeNodeAdded = false
        treeSet.addAll(component.properties.keySet())
        treeSet.each {String k ->
            if (k.startsWith("sub")) {
                componentNode.add(buildComponentNode(component[k], k))
            } else if (k.startsWith("out")) {
                boolean packet = isSingleOrMultiValuePacket(component[k])
                if (packet) {
                    componentNode.add(new ResultConfigurationTableTreeNode(k, item))
                    rcTableTreeNodeAdded = true
                }
            }
        }
        if (!rcTableTreeNodeAdded)
            componentNodes.remove(component)
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
        return ((packetList.getType().newInstance() instanceof MultiValuePacket) || (packetList.getType().newInstance() instanceof SingleValuePacket))
    }
}