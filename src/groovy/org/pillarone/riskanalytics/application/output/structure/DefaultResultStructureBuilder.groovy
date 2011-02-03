package org.pillarone.riskanalytics.application.output.structure

import org.pillarone.riskanalytics.application.output.structure.item.ResultNode
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.packets.MultiValuePacket
import org.pillarone.riskanalytics.core.packets.Packet
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.packets.SingleValuePacket

class DefaultResultStructureBuilder {

    public static ResultStructure create(String name, Class modelClass) {
        Model model = modelClass.newInstance()
        model.init()

        ResultNode node = new ResultNode(modelClass.simpleName - "Model", null)
        collectPaths(model, node)

        ResultStructure resultStructure = new ResultStructure(name)
        resultStructure.modelClass = modelClass
        resultStructure.rootNode = node
        return resultStructure
    }

    private static void collectPaths(Model model, ResultNode rootNode) {
        model.properties.each { String name, value ->
            if (value instanceof Component) {
                if (hasComponentOutputPath(value)) {
                    ResultNode componentNode = new ResultNode(name, null)
                    rootNode.addChild(componentNode)
                    collectPaths(value, componentNode)
                }

            }
        }
    }

    private static void collectPaths(Component component, ResultNode componentNode) {
        component.properties.each { String name, value ->
            if (name.startsWith("out")) {
                if (hasPacketListValidOutput(value)) {
                    ResultNode outNode = new ResultNode(name, null)
                    componentNode.addChild(outNode)
                    collectPaths(value, outNode)
                }

            } else if (name.startsWith("sub")) {
                if (hasComponentOutputPath(value)) {
                    ResultNode subComponentNode = new ResultNode(name, null)
                    componentNode.addChild(subComponentNode)
                    collectPaths(value, subComponentNode)
                }

            }
        }
    }

    private static void collectPaths(DynamicComposedComponent component, ResultNode componentNode) {
        component.properties.each { String name, value ->
            if (name.startsWith("out")) {
                if (hasPacketListValidOutput(value)) {
                    ResultNode outNode = new ResultNode(name, null)
                    componentNode.addChild(outNode)
                    collectPaths(value, outNode)
                }

            }
        }
        Component defaultSubComponent = component.createDefaultSubComponent()
        if (hasComponentOutputPath(defaultSubComponent)) {
            ResultNode defaultSubComponentNode = new ResultNode("[%${component.genericSubComponentName}%]", null)
            componentNode.addChild(defaultSubComponentNode)
            collectPaths(defaultSubComponent, defaultSubComponentNode)
        }

    }

    private static void collectPaths(PacketList packetList, ResultNode componentNode) {
        Class packetType = packetList.getType()
        Packet packet = packetType.newInstance()
        List allFields = packet.valuesToSave.keySet().toList()
        for (String field in allFields) {
            ResultNode outNode = new ResultNode(field, null)
            componentNode.addChild(outNode)
            outNode.resultPath = outNode.path
        }
    }

    private static boolean hasComponentOutputPath(Component component) {
        boolean result = false
        component.properties.each { String name, value ->
            if (name.startsWith("out")) {
                if (hasPacketListValidOutput(value)) {
                    result = true
                }
            } else if (!result && name.startsWith("sub")) {
                if (hasComponentOutputPath(value)) {
                    result = true
                }
            }
        }
        return result
    }

    private static boolean hasComponentOutputPath(DynamicComposedComponent component) {
        boolean result = false
        component.properties.each { String name, value ->
            if (name.startsWith("out")) {
                if (hasPacketListValidOutput(value)) {
                    result = true
                }
            }
        }
        if (!result) {
            result = hasComponentOutputPath(component.createDefaultSubComponent())
        }
        return result
    }

    private static boolean hasPacketListValidOutput(PacketList packetList) {
        Class packetType = packetList.getType()
        Packet packet = packetType.newInstance()
        if ((packet instanceof SingleValuePacket) || (packet instanceof MultiValuePacket))
            return !packet.valuesToSave.keySet().isEmpty()
        return false
    }

}
