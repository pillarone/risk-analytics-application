package org.pillarone.riskanalytics.application.ui.resultnavigator.util

import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryUtils

class CategoryUtilsTests extends GroovyTestCase {

    void testParseList() {
        List<String> list = CategoryUtils.parseList("a,b,c,d")
        assertTrue(["a","b","c","d"].equals(list))
    }
}
