package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.resolver.EnclosingMatchResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.WildCardPath

/**
 * @author martin.melchior
 */
class WildCardPathTest extends GroovyTestCase {

    CategoryMapping getTestCategoryMapping() {
        CategoryMapping mapping = new CategoryMapping()
        mapping.addCategory("lob", new EnclosingMatchResolver(":lines:",":",OutputElement.PATH))
        mapping.addCategory("contracts", new EnclosingMatchResolver(":reinsurance:",":",OutputElement.PATH))
        mapping.addCategory("perils", new EnclosingMatchResolver(":claims:",":",OutputElement.PATH))
        return mapping
    }

    void testSetWildCardPathStringList() {
        String cat1 = "peril"
        String cat2 = "lob"
        String cat3 = "field"
        String spec = "TestModel:perils:\${${cat1}}:someotherrefs:lobs:\${${cat2}}"

        WildCardPath wcp = new WildCardPath()
        wcp.initialize(spec, [cat1,cat2])
        assertTrue wcp.allWildCards.contains(cat1)
        assertTrue wcp.pathWildCards.contains(cat1)
        assertTrue wcp.allWildCards.contains(cat2)
        assertTrue wcp.pathWildCards.contains(cat2)

        assertEquals([cat1,cat2], wcp.getPathWildCards())
    }

    void testGetSpecificPath() {
        String cat1 = "peril"
        String cat2 = "lob"
        String cat3 = "field"
        String spec = "TestModel:perils:\${${cat1}}:someotherrefs:lobs:\${${cat2}}"

        WildCardPath wcp = new WildCardPath()
        wcp.initialize(spec, [cat1,cat2])
        String path = wcp.getSpecificPath(["peril":"storm", "lob":"Property", "field":"claims"])
        assertEquals "TestModel:perils:storm:someotherrefs:lobs:Property", path

        try {
            path = wcp.getSpecificPath([cat1:"storm", cat3:"claims"])
            fail()
        } catch (Exception ex) {}

        // trivial path
        wcp = new WildCardPath()
        wcp.initialize("mypath", [])
        assertEquals "mypath", wcp.getSpecificPath([:])
    }


    void testGetWildCardValues() {
        String cat1 = "peril"
        String cat2 = "lob"
        String cat3 = "field"
        String spec = "TestModel:perils:\${${cat1}}:someotherrefs:lobs:\${${cat2}}"

        WildCardPath wcp = new WildCardPath()
        wcp.initialize(spec, [cat1,cat2])
        wcp.addPathWildCardValue cat1, "Storm"
        wcp.addPathWildCardValue cat1, "Flood"
        wcp.addPathWildCardValue cat2, "Prop"
        wcp.addPathWildCardValue cat2, "Cas"
        wcp.addPathWildCardValue cat2, "MTPL"

        wcp.addWildCardValue cat3, "claims"
        wcp.addWildCardValue cat3, "premium"
        wcp.addWildCardValue cat3, "expenses"

        assertEquals([cat1,cat2], wcp.getPathWildCards())
        assertEquals([cat1,cat2,cat3], wcp.getAllWildCards())
        assertEquals(["Storm","Flood"], wcp.getWildCardValues(cat1))
        assertEquals(["Prop","Cas","MTPL"], wcp.getWildCardValues(cat2))
        assertEquals(["claims","premium","expenses"], wcp.getWildCardValues(cat3))
    }
}
