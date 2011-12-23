package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * User: martin.melchior
 */
class EnclosingMatcherTest extends GroovyTestCase {

    void testEnclosingMatcher() {
        ICategoryResolver matcher = new EnclosingMatcher(["linesOfBusiness:sub", "AA"], ["SL", "WXL", "BB"], OutputElement.PATH)
        String path = "podra:someother:path:linesOfBusiness:subMotorWXL:restofthe:path"
        OutputElement element = new OutputElement()
        element.setPath(path)
        element.addCategoryValue(OutputElement.PATH, path)
        assertTrue matcher.isResolvable(element)
        assertEquals "Motor", matcher.getResolvedValue(element)
        assertTrue matcher.createTemplatePath(element, "category")
        assertEquals 'podra:someother:path:linesOfBusiness:sub${category}WXL:restofthe:path', element.getTemplatePath()
        path = "podra:someother:path:AAMotorWXLBB:path"
        element = new OutputElement()
        element.setPath(path)
        element.addCategoryValue(OutputElement.PATH, path)
        assertTrue matcher.isResolvable(element)
        path = "podra:someother:path:AAMotorBB:path"
        element = new OutputElement()
        element.setPath(path)
        element.addCategoryValue(OutputElement.PATH, path)
        assertEquals "Motor", matcher.getResolvedValue(element)
        assertTrue matcher.createTemplatePath(element, "category")
        assertEquals 'podra:someother:path:AA${category}BB:path', element.getTemplatePath()
        path = "podra:someother:path:AAsubMotorCXLBB:path"
        element = new OutputElement()
        element.setPath(path)
        element.addCategoryValue(OutputElement.PATH, path)
        assertTrue matcher.isResolvable(element)
        assertEquals "subMotorCXL", matcher.getResolvedValue(element)
        assertTrue matcher.createTemplatePath(element, "category")
        assertEquals 'podra:someother:path:AA${category}BB:path', element.getTemplatePath()
        path = "podra:someother:path:subMotorCXLBB:path"
        element = new OutputElement()
        element.setPath(path)
        element.addCategoryValue(OutputElement.PATH, path)
        assertFalse matcher.isResolvable(element)
        assertNull matcher.getResolvedValue(element)
        assertFalse matcher.createTemplatePath(element, "category")


        path = "podra:someother:path:linesOfBusiness:subMotor:restofthe:reinsurance:WXL:path"
        element = new OutputElement()
        element.setPath(path)
        element.addCategoryValue(OutputElement.PATH, path)
        matcher = new EnclosingMatcher(["linesOfBusiness:"], [":"], OutputElement.PATH)
        assertTrue matcher.createTemplatePath(element, "lob")
        assertEquals 'podra:someother:path:linesOfBusiness:${lob}:restofthe:reinsurance:WXL:path', element.getTemplatePath()

        matcher = new EnclosingMatcher(["reinsurance:"], [":"], OutputElement.PATH)
        assertTrue matcher.createTemplatePath(element, "contract")
        assertEquals 'podra:someother:path:linesOfBusiness:${lob}:restofthe:reinsurance:${contract}:path', element.getTemplatePath()
    }
}
