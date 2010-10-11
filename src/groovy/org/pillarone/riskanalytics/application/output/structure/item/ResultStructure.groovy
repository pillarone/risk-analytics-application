package org.pillarone.riskanalytics.application.output.structure.item

import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO
import org.pillarone.riskanalytics.application.output.structure.StructureMapping
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class ResultStructure extends ModellingItem {

    VersionNumber versionNumber
    ResultNode rootNode
    String language

    public ResultStructure(String name) {
        super(name);
        versionNumber = new VersionNumber("1")
        language = LocaleResources.getLanguage()
    }

    public ResultStructure(String name, Class modelClass) {
        this(name);
        this.modelClass = modelClass
        this.language = LocaleResources.getLanguage()
    }

    public ResultStructure(String name, Class modelClass, String language) {
        this(name);
        this.modelClass = modelClass
        this.language = language
    }

    protected Object createDao() {
        return new ResultStructureDAO(name: name, modelClassName: modelClass.name, language: language)
    }

    Object getDaoClass() {
        ResultStructureDAO
    }

    protected void mapToDao(Object dao) {
        ResultStructureDAO resultStructureDAO = dao as ResultStructureDAO

        resultStructureDAO.name = name
        resultStructureDAO.modelClassName = modelClass.name
        resultStructureDAO.itemVersion = versionNumber.toString()
        resultStructureDAO.language = language

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
        modelClass = getClass().getClassLoader().loadClass(resultStructureDAO.modelClassName)
        language = resultStructureDAO.language

        Set<StructureMapping> mappings = resultStructureDAO.structureMappings
        StructureMapping root = mappings.find { it.parent == null }
        rootNode = new ResultNode(root.name, root.resultPath)
        loadMappings(mappings, rootNode, root)
    }

    private void loadMappings(Set<StructureMapping> allMappings, ResultNode currentNode, StructureMapping parentMapping) {
        Collection<StructureMapping> children = allMappings.findAll { it.parent == parentMapping }.sort { it.orderWithinLevel }
        for (StructureMapping child in children) {
            ResultNode childNode = new ResultNode(child.name, child.resultPath)
            currentNode.addChild(childNode)
            loadMappings(allMappings, childNode, child)
        }
    }

    protected Object loadFromDB() {
        def criteria = ResultStructureDAO.createCriteria()
        return criteria.get {
            eq("name", name)
            eq("itemVersion", versionNumber.toString())
            eq("language", language)
            if (modelClass != null) {
                eq("modelClassName", modelClass.name)
            }
        }
    }


}
