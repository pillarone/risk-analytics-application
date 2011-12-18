package org.pillarone.riskanalytics.application.ui.resultnavigator.util

import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryUtils
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.EnclosingMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.SingleValueFromListMatcher

class CategoryUtilsTests extends GroovyTestCase {

    void testGetWordByMatchedEnclosing() {
        String value = CategoryUtils.getWordByMatchedEnclosing("podra:someother:path:linesOfBusiness:subMotor:restofthe:path", "linesOfBusiness:sub", ":")
        assertEquals "Motor", value
        value = CategoryUtils.getWordByMatchedEnclosing("podra:someother:path:linesOfBusiness:subMotor:restofthe:path", "lineOfBusiness:sub", ":")
        assertNull value

        ICategoryMatcher matcher = new EnclosingMatcher(["linesOfBusiness:sub", "AA"], ["SL", "WXL", "BB"])
        assertTrue matcher.isMatch("podra:someother:path:linesOfBusiness:subMotorWXL:restofthe:path")
        assertEquals "Motor", matcher.getMatch("podra:someother:path:linesOfBusiness:subMotorWXL:restofthe:path")
        assertTrue matcher.isMatch("podra:someother:path:AAMotorWXLBB:path")
        assertEquals "Motor", matcher.getMatch("podra:someother:path:AAMotorBB:path")
        assertTrue matcher.isMatch("podra:someother:path:AAsubMotorCXLBB:path")
        assertEquals "subMotorCXL", matcher.getMatch("podra:someother:path:AAsubMotorCXLBB:path")
        assertFalse matcher.isMatch("podra:someother:path:subMotorCXLBB:path")
        assertNull matcher.getMatch("podra:someother:path:subMotorCXLBB:path")
    }

    void testGetWordByUniqueMatch() {
        String value = CategoryUtils.getWordByUniqueMatch("podra:someother:path:linesOfBusiness:subMotor:restofthe:path", ["Motor"])
        assertEquals "Motor", value
        value = CategoryUtils.getWordByUniqueMatch("podra:someother:path:linesOfBusiness:subMotor:restofthe:path", ["subMotor", "Motor"])
        assertNull value
        value = CategoryUtils.getWordByUniqueMatch("podra:someother:path:linesOfBusiness:subMotor:restofthe:path", ["podra"])
        assertEquals "podra", value
        value = CategoryUtils.getWordByUniqueMatch("podra:someother:path:linesOfBusiness:subMotor:restofthe:path", ["linesOfBusiness"])
        assertEquals "linesOfBusiness", value
        value = CategoryUtils.getWordByUniqueMatch("podra:someother:path:linesOfBusiness:subMotor:restofthe:path", ["Liability"])
        assertNull value
        assertTrue new SingleValueFromListMatcher(["linesOfBusiness:(?!sub)"]).isMatch("podra:someother:path:linesOfBusiness:outMotor:restofthe:path")
        assertFalse new SingleValueFromListMatcher(["linesOfBusiness:(?!sub)"]).isMatch("podra:someother:path:linesOfBusiness:subMotor:restofthe:path")
    }
}
