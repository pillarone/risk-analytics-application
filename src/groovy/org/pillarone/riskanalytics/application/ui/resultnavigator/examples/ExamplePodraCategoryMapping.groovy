package org.pillarone.riskanalytics.application.ui.resultnavigator.examples

import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryUtils
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryMapping
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.AbstractCategoryMapping
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.EnclosingMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.SingleValueFromListMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ConditionalAssignment
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.OrMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.EndingMatcher

/**
 * @author martin.melchior
 */
class ExamplePodraCategoryMapping extends AbstractCategoryMapping {

    ExamplePodraCategoryMapping() {
        super()
        List<String> perils = ["Earthquake", "Flood", "Storm", "Large", "Attritional", "Cat"]
        List<String> contracts = ["QuotaShare", "Wxl", "Cxl", "Sl"]
        ICategoryMatcher m1 = new EnclosingMatcher("linesOfBusiness:sub",":")
        ICategoryMatcher m2 = new EnclosingMatcher(["subContracts:sub"],contracts)
        ICategoryMatcher m3 = new EnclosingMatcher(["claimsGenerators:sub"],perils)
        ICategoryMatcher m41 = new SingleValueFromListMatcher(["linesOfBusiness:(?!sub)"])
        ICategoryMatcher m4 = new ConditionalAssignment("Aggregate", m41)
        matcherMap["Lob"] = new OrMatcher([m1,m2,m3,m4])
        matcherMap["Peril"] = new SingleValueFromListMatcher(perils)
        matcherMap["RIContractType"] = new SingleValueFromListMatcher(contracts)
        matcherMap["AccountBasis"] = new SingleValueFromListMatcher(["Gross", "Ceded", "Net"])
        matcherMap["Keyfigure"] = new EndingMatcher("__")
    }
}
