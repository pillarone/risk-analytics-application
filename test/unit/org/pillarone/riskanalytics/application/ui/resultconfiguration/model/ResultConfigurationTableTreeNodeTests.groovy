package org.pillarone.riskanalytics.application.ui.resultconfiguration.model

import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy
import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.core.output.PacketCollector
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.packets.Packet

class ResultConfigurationTableTreeNodeTests extends GroovyTestCase {

    private PacketCollector collector

    void setUp() {
        LocaleResources.setTestMode()
        //Boot strap is not executed for unit tests
        CollectingModeFactory.registerStrategy(new SingleValueCollectingModeStrategy())
        CollectingModeFactory.registerStrategy(new AggregatedCollectingModeStrategy())
    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testFindCollector() {
        ResultConfigurationTableTreeNode node = initConfigNode()
        assertSame collector, node.collector
    }

    void testChangeValue() {
        ResultConfigurationTableTreeNode node = initConfigNode()
        node.setValueAt("Aggregated", 1)

        assertEquals AggregatedCollectingModeStrategy.IDENTIFIER, collector.mode.identifier

        assertEquals "Aggregated", node.getValueAt(1)
    }

    void testRemoveCollector() {
        ResultConfigurationTableTreeNode node = initConfigNode()
        node.setValueAt("Not collected", 1)

        assertEquals 0, node.configuration.collectors.size()
        assertNull node.collector
    }

    void testAddNewCollector() {
        ResultConfigurationTableTreeNode node = initConfigNode()
        node.setValueAt("Not collected", 1)

        assertEquals 0, node.configuration.collectors.size()
        assertNull node.collector

        node.setValueAt("Single", 1)

        assertEquals 1, node.configuration.collectors.size()
        assertNotNull node.collector
        assertEquals SingleValueCollectingModeStrategy.IDENTIFIER, node.collector.mode.identifier
    }

    private ResultConfigurationTableTreeNode initConfigNode() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        ComponentTableTreeNode component = new ComponentTableTreeNode(null, "component")
        root.add(component)

        ResultConfiguration configuration = new ResultConfiguration("config")

        collector = new PacketCollector(path: "root:component:outValue", mode: CollectingModeFactory.getStrategy(SingleValueCollectingModeStrategy.IDENTIFIER))
        configuration.collectors << collector

        ResultConfigurationTableTreeNode node = new ResultConfigurationTableTreeNode("outValue", configuration, Packet)
        component.add(node)

        return node
    }


}
