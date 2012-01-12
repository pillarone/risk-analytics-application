package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class CategoryMappingTest extends GroovyTestCase {

    List<OutputElement> getTestOutputElements() {
        List<OutputElement> elements = []
        // a single wild card --> lob
        elements.add new OutputElement(path : "model:lines:property:premium")
        elements.add new OutputElement(path : "model:lines:property:claims")
        elements.add new OutputElement(path : "model:lines:casualty:premium")
        elements.add new OutputElement(path : "model:lines:casualty:claims")
        // a single wild card --> peril
        elements.add new OutputElement(path : "model:claims:eq:claims")
        elements.add new OutputElement(path : "model:claims:flood:claims")
        // a single wild card --> contracts
        elements.add new OutputElement(path : "model:reinsurance:WXL:claims")
        elements.add new OutputElement(path : "model:reinsurance:CXL:claims")
        // a two wild cards --> peril, lob
        elements.add new OutputElement(path : "model:claims:flood:lines:property:claims")
        elements.add new OutputElement(path : "model:claims:eq:lines:property:claims")
        // a two wild cards --> peril, contracts
        elements.add new OutputElement(path : "model:claims:flood:reinsurance:CXL:claims")
        // a three wild cards --> peril, lob, contracts
        elements.add new OutputElement(path : "model:claims:flood:lines:property:reinsurance:WXL:claims")
        elements.add new OutputElement(path : "model:claims:flood:lines:property:reinsurance:WXL:premium")
        elements.add new OutputElement(path : "model:claims:flood:lines:property:reinsurance:CXL:claims")
        elements.add new OutputElement(path : "model:claims:flood:lines:property:reinsurance:CXL:premium")
        elements.add new OutputElement(path : "model:claims:eq:lines:property:reinsurance:WXL:claims")
        elements.add new OutputElement(path : "model:claims:eq:lines:property:reinsurance:WXL:premium")
        elements.add new OutputElement(path : "model:claims:eq:lines:property:reinsurance:CXL:claims")
        elements.add new OutputElement(path : "model:claims:eq:lines:property:reinsurance:CXL:premium")

        for (OutputElement e : elements) {
            e.addCategoryValue(OutputElement.PATH, e.path)
        }
        return elements
    }

    CategoryMapping getTestCategoryMapping() {
        CategoryMapping mapping = new CategoryMapping()
        mapping.addCategory("lob", new EnclosingMatcher(":lines:",":",OutputElement.PATH))
        mapping.addCategory("contracts", new EnclosingMatcher(":reinsurance:",":",OutputElement.PATH))
        mapping.addCategory("perils", new EnclosingMatcher(":claims:",":",OutputElement.PATH))
        return mapping
    }

    List<OutputElement> getTestOutputElementsWithField() {
        List<OutputElement> elements = []
        // a single wild card --> lob
        elements.add new OutputElement(path : "model:lines:property:premium", field: "AA")
        elements.add new OutputElement(path : "model:lines:property:claims", field: "AA")
        elements.add new OutputElement(path : "model:lines:casualty:premium", field: "BB")
        elements.add new OutputElement(path : "model:lines:casualty:claims", field: "BB")
        // a single wild card --> peril
        elements.add new OutputElement(path : "model:claims:eq:claims", field: "BB")
        elements.add new OutputElement(path : "model:claims:flood:claims", field: "BB")
        // a single wild card --> contracts
        elements.add new OutputElement(path : "model:reinsurance:WXL:claims", field: "CC")
        elements.add new OutputElement(path : "model:reinsurance:CXL:claims", field: "BB")
        // a two wild cards --> peril, lob
        elements.add new OutputElement(path : "model:claims:flood:lines:property:claims", field: "CC")
        elements.add new OutputElement(path : "model:claims:eq:lines:property:claims", field: "CC")
        // a two wild cards --> peril, contracts
        elements.add new OutputElement(path : "model:claims:flood:reinsurance:CXL:claims", field: "AA")
        // a three wild cards --> peril, lob, contracts
        elements.add new OutputElement(path : "model:claims:flood:lines:property:reinsurance:WXL:claims", field: "AA")
        elements.add new OutputElement(path : "model:claims:flood:lines:property:reinsurance:WXL:premium", field: "AA")
        elements.add new OutputElement(path : "model:claims:flood:lines:property:reinsurance:CXL:claims", field: "AA")
        elements.add new OutputElement(path : "model:claims:flood:lines:property:reinsurance:CXL:premium", field: "CC")
        elements.add new OutputElement(path : "model:claims:eq:lines:property:reinsurance:WXL:claims", field: "CC")
        elements.add new OutputElement(path : "model:claims:eq:lines:property:reinsurance:WXL:premium", field: "CC")
        elements.add new OutputElement(path : "model:claims:eq:lines:property:reinsurance:CXL:claims", field: "CC")
        elements.add new OutputElement(path : "model:claims:eq:lines:property:reinsurance:CXL:premium", field: "CC")

        for (OutputElement e : elements) {
            e.addCategoryValue(OutputElement.PATH, e.path)
            e.addCategoryValue(OutputElement.FIELD, e.field)
        }
        return elements
    }

    CategoryMapping getTestCategoryMappingInclField() {
        CategoryMapping mapping = new CategoryMapping()
        mapping.addCategory("lob", new EnclosingMatcher(":lines:",":",OutputElement.PATH))
        mapping.addCategory("contracts", new EnclosingMatcher(":reinsurance:",":",OutputElement.PATH))
        mapping.addCategory("perils", new EnclosingMatcher(":claims:",":",OutputElement.PATH))
        mapping.addCategory("keyfigure", new SynonymToCategory(OutputElement.FIELD))
        return mapping
    }

    public void testCategorize() {
        CategoryMapping mapping = getTestCategoryMapping()
        List<OutputElement> elements = getTestOutputElements()
        mapping.categorize elements
        assertEquals 8, mapping.getWildCardPaths().size()

        String path = 'model:lines:${lob}:premium'
        List<String> wildCards = mapping.wildCardPaths[path].getPathWildCards()
        assertEquals 1, wildCards.size()
        assertTrue wildCards.contains("lob")
        List<String> values = mapping.wildCardPaths[path].getWildCardValues("lob")
        assertEquals 2, values.size()
        assertTrue values.contains("property")
        assertTrue values.contains("casualty")


        path = 'model:claims:${perils}:lines:${lob}:reinsurance:${contracts}:claims'
        wildCards = mapping.wildCardPaths[path].getPathWildCards()
        assertEquals 3, wildCards.size()
        assertTrue wildCards.contains("perils")
        assertTrue wildCards.contains("lob")
        assertTrue wildCards.contains("contracts")
        values = mapping.wildCardPaths[path].getWildCardValues("lob")
        assertEquals 1, values.size()
        assertTrue values.contains("property")
        values = mapping.wildCardPaths[path].getWildCardValues("perils")
        assertEquals 2, values.size()
        assertTrue values.contains("flood")
        assertTrue values.contains("eq")
        values = mapping.wildCardPaths[path].getWildCardValues("contracts")
        assertEquals 2, values.size()
        assertTrue values.contains("CXL")
        assertTrue values.contains("WXL")

        path = 'model:lines:${lob}:premium'
        assertEquals elements[0].wildCardPath, mapping.wildCardPaths[path]

        path = 'model:claims:${perils}:lines:${lob}:reinsurance:${contracts}:claims'
        assertEquals elements[-2].wildCardPath, mapping.wildCardPaths[path]

        for (OutputElement e : elements) {
            assertEquals e.path, e.getWildCardPath().getSpecificPath(e.getCategoryMap())
        }

        assertEquals 'model:lines:AAA:premium', elements[0].getWildCardPath().getSpecificPath(["lob":"AAA"])
    }

    public testCategorizeInclFields() {
        CategoryMapping mapping = getTestCategoryMappingInclField()
        List<OutputElement> elements = getTestOutputElementsWithField()
        mapping.categorize elements
        assertEquals 8, mapping.getWildCardPaths().size()

        String path = 'model:lines:${lob}:premium'
        List<String> wildCards = mapping.wildCardPaths[path].getPathWildCards()
        assertEquals 1, wildCards.size()
        assertTrue wildCards.contains("lob")
        List<String> values = mapping.wildCardPaths[path].getWildCardValues("lob")
        assertEquals 2, values.size()
        assertTrue values.contains("property")
        assertTrue values.contains("casualty")
        wildCards = mapping.wildCardPaths[path].getAllWildCards()
        assertEquals 2, wildCards.size()
        assertTrue wildCards.contains("lob")
        assertTrue wildCards.contains("keyfigure")
        values = mapping.wildCardPaths[path].getWildCardValues("keyfigure")
        assertEquals 2, values.size()
        assertTrue values.contains("AA")
        assertTrue values.contains("BB")

        path = 'model:claims:${perils}:lines:${lob}:reinsurance:${contracts}:claims'
        wildCards = mapping.wildCardPaths[path].getPathWildCards()
        assertEquals 3, wildCards.size()
        assertTrue wildCards.contains("perils")
        assertTrue wildCards.contains("lob")
        assertTrue wildCards.contains("contracts")
        values = mapping.wildCardPaths[path].getWildCardValues("lob")
        assertEquals 1, values.size()
        assertTrue values.contains("property")
        values = mapping.wildCardPaths[path].getWildCardValues("perils")
        assertEquals 2, values.size()
        assertTrue values.contains("flood")
        assertTrue values.contains("eq")
        values = mapping.wildCardPaths[path].getWildCardValues("contracts")
        assertEquals 2, values.size()
        assertTrue values.contains("CXL")
        assertTrue values.contains("WXL")
        wildCards = mapping.wildCardPaths[path].getAllWildCards()
        assertEquals 4, wildCards.size()
        assertTrue wildCards.contains("perils")
        assertTrue wildCards.contains("lob")
        assertTrue wildCards.contains("contracts")
        assertTrue wildCards.contains("keyfigure")
        values = mapping.wildCardPaths[path].getWildCardValues("keyfigure")
        assertEquals 2, values.size()
        assertTrue values.contains("AA")
        assertTrue values.contains("CC")

        path = 'model:lines:${lob}:premium'
        assertEquals elements[0].wildCardPath, mapping.wildCardPaths[path]

        path = 'model:claims:${perils}:lines:${lob}:reinsurance:${contracts}:claims'
        assertEquals elements[-2].wildCardPath, mapping.wildCardPaths[path]

        for (OutputElement e : elements) {
            assertEquals e.path, e.getWildCardPath().getSpecificPath(e.getCategoryMap())
        }

        assertEquals 'model:lines:AAA:premium', elements[0].getWildCardPath().getSpecificPath(["lob":"AAA"])
    }

    public void testWithTrivialMapping() {
        CategoryMapping mapping = new CategoryMapping()
        List<OutputElement> elements = getTestOutputElements()
        mapping.categorize elements
        assertEquals elements.size(), mapping.getWildCardPaths().size()

        // todo fix problems with mapping.getWildCardPaths().get(0).getPathWildCards != null , etc.
    }
}
