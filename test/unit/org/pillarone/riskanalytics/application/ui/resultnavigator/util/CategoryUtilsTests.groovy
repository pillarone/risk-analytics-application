package org.pillarone.riskanalytics.application.ui.resultnavigator.util

import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryUtils
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.EnclosingMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.SingleValueFromListMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

class CategoryUtilsTests extends GroovyTestCase {

    void testParseList() {
        List<String> list = CategoryUtils.parseList("a,b,c,d")
        assertTrue(["a","b","c","d"].equals(list))
    }
}
