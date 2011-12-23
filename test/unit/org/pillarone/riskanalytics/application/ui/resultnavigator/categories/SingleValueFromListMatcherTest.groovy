package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * User: martin.melchior
 */
class SingleValueFromListMatcherTest extends GroovyTestCase {

    void testGetWordByUniqueMatch() {
        String path = "podra:someother:path:linesOfBusiness:motor:restofthe:path"
        OutputElement element = new OutputElement()
        element.setPath(path)
        element.addCategoryValue(OutputElement.PATH, path)
        ICategoryResolver matcher = new SingleValueFromListMatcher(["motor","property"], OutputElement.PATH)
        assertTrue matcher.isResolvable(element)
        assertTrue matcher.createTemplatePath(element, "lob")
        assertEquals 'podra:someother:path:linesOfBusiness:${lob}:restofthe:path', element.templatePath

        path = "podra:someother:path:linesOfBusiness:Motor:restofthe:path"
        element = new OutputElement()
        element.setPath(path)
        element.addCategoryValue(OutputElement.PATH, path)
        assertFalse new SingleValueFromListMatcher(["motor","property"], OutputElement.PATH).isResolvable(element)
        assertFalse matcher.createTemplatePath(element, "lob")

        path = "podra:someother:path:linesOfBusiness:motor:restofthe:reinsurance:WXL:path"
        element = new OutputElement()
        element.setPath(path)
        element.addCategoryValue(OutputElement.PATH, path)

        matcher = new SingleValueFromListMatcher(["motor","property"], OutputElement.PATH)
        assertTrue matcher.createTemplatePath(element, "lob")

        matcher = new SingleValueFromListMatcher(["WXL","CXL"], OutputElement.PATH)
        assertTrue matcher.createTemplatePath(element, "contract")

        assertEquals 'podra:someother:path:linesOfBusiness:${lob}:restofthe:reinsurance:${contract}:path', element.templatePath
    }
}
