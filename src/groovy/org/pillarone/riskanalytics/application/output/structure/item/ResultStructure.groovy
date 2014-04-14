package org.pillarone.riskanalytics.application.output.structure.item

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.output.structure.StructureMapping
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class ResultStructure extends ModellingItem {

    ResultNode rootNode

    ResultStructure(String name, Class modelClass) {
        super(name)
        this.modelClass = modelClass
        versionNumber = new VersionNumber("1")
    }

    protected Object createDao() {
        return new ResultStructureDAO(name: name, modelClassName: modelClass.name)
    }

    Object getDaoClass() {
        ResultStructureDAO
    }

    protected void mapToDao(Object dao) {
        ResultStructureDAO resultStructureDAO = dao as ResultStructureDAO

        resultStructureDAO.name = name
        resultStructureDAO.modelClassName = modelClass.name
        resultStructureDAO.itemVersion = versionNumber.toString()

        List<StructureMapping> mappings = []
        createMappings(mappings, rootNode, null, 0)
        for (StructureMapping m in mappings) {
            resultStructureDAO.addToStructureMappings(m)
        }
    }

    private void createMappings(List<StructureMapping> mappings, ResultNode currentNode, StructureMapping parent, int order) {
        StructureMapping currentMapping = new StructureMapping(name: currentNode.name, resultPath: currentNode.resultPath, parent: parent, orderWithinLevel: order)
        mappings.add(currentMapping)
        int newOrder = 0
        for (ResultNode child in currentNode.childNodes) {
            createMappings(mappings, child, currentMapping, newOrder)
            newOrder++
        }
    }

    protected void mapFromDao(Object dao, boolean completeLoad) {
        ResultStructureDAO resultStructureDAO = dao as ResultStructureDAO

        name = resultStructureDAO.name
        modelClass = getClass().classLoader.loadClass(resultStructureDAO.modelClassName)

        Set<StructureMapping> mappings = resultStructureDAO.structureMappings
        StructureMapping root = mappings.find { it.parent == null }
        rootNode = new ResultNode(root.name, root.resultPath)
        Multimap<StructureMapping, StructureMapping> allMappings = ArrayListMultimap.create()
        for (StructureMapping mapping in mappings) {
            if (mapping.parent != null) {
                allMappings.put(mapping.parent, mapping)
            }
        }
        loadMappings(allMappings, rootNode, root)
    }

    private void loadMappings(Multimap<StructureMapping, StructureMapping> allMappings, ResultNode currentNode, StructureMapping parentMapping) {
        Collection<StructureMapping> children = allMappings.get(parentMapping).sort { it.orderWithinLevel }
        for (StructureMapping child in children) {
            ResultNode childNode = new ResultNode(child.name, child.resultPath)
            currentNode.addChild(childNode)
            loadMappings(allMappings, childNode, child)
        }
    }

    protected ResultStructureDAO loadFromDB() {
        def criteria = ResultStructureDAO.createCriteria()
        return criteria.get {
            eq("name", name)
            eq("itemVersion", versionNumber.toString())
            if (modelClass != null) {
                eq("modelClassName", modelClass.name)
            }
        }
    }
}
