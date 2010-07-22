package org.pillarone.riskanalytics.application.output.structure

import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.packets.Packet


class DefaultResultStructureBuilder {

    public static ResultStructure create(String name, Class modelClass) {
        Model model = modelClass.newInstance()
        model.init()

        List allPaths = []
        collectPaths(model, allPaths)

        ResultStructure resultStructure = new ResultStructure(name)
        resultStructure.modelClass = modelClass
        for (String path in allPaths) {
            resultStructure.mappings.put(path, path)
        }

        return resultStructure
    }

    private static void collectPaths(Model model, List allPaths) {
        String modelName = model.class.simpleName - "Model"
        model.properties.each { String name, value ->
            if (value instanceof Component) {
                collectPaths(value, "${modelName}:${name}", allPaths)
            }
        }
    }

    private static void collectPaths(Component component, String prefix, List allPaths) {
        component.properties.each { String name, value ->
            if (name.startsWith("out")) {
                collectPaths(value, "${prefix}:${name}", allPaths)
            } else if (name.startsWith("sub")) {
                collectPaths(value, "${prefix}:${name}", allPaths)
            }
        }
    }

    private static void collectPaths(DynamicComposedComponent component, String prefix, List allPaths) {
        component.properties.each { String name, value ->
            if (name.startsWith("out")) {
                collectPaths(value, "${prefix}:${name}", allPaths)
            }
        }
        collectPaths(component.createDefaultSubComponent(), "${prefix}:[%${component.genericSubComponentName}%]", allPaths)
    }

    private static void collectPaths(PacketList packetList, String prefix, List allPaths) {
        Class packetType = packetList.getType()
        Packet packet = packetType.newInstance()
        List allFields = packet.valuesToSave.keySet().toList()
        for (String field in allFields) {
            allPaths << "${prefix}:${field}"
        }
    }
}
