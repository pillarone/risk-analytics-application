package org.pillarone.riskanalytics.application.ui.resultnavigator.examples

import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMapping
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.EnclosingMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.SingleValueFromListMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ConditionalAssignment
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.OrMatcher
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class ExamplePodraCategoryMapping extends CategoryMapping {

    ExamplePodraCategoryMapping() {
        super()
        List<String> perils = ["Earthquake", "Flood", "Storm", "Large", "Attritional", "Cat"]
        List<String> contracts = ["QuotaShare", "Wxl", "Cxl", "Sl"]
        ICategoryResolver m1 = new EnclosingMatcher("linesOfBusiness:sub",":",OutputElement.PATH)
        ICategoryResolver m2 = new EnclosingMatcher(["subContracts:sub"],contracts, OutputElement.PATH)
        ICategoryResolver m3 = new EnclosingMatcher(["claimsGenerators:sub"],perils, OutputElement.PATH)
        ICategoryResolver m41 = new SingleValueFromListMatcher(["linesOfBusiness:(?!sub)"], OutputElement.PATH)
        ICategoryResolver m4 = new ConditionalAssignment("Aggregate", m41)
        matcherMap["Lob"] = new OrMatcher([m1,m2,m3,m4])
        matcherMap["Peril"] = new SingleValueFromListMatcher(perils, OutputElement.PATH)
        matcherMap["RIContractType"] = new SingleValueFromListMatcher(contracts, OutputElement.PATH)
        matcherMap["AccountBasis"] = new SingleValueFromListMatcher(["Gross", "Ceded", "Net"], OutputElement.PATH)
        //matcherMap["Keyfigure"] = new EndingMatcher("__")
    }
}
