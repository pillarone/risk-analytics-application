package org.pillarone.riskanalytics.application.ui.resultnavigator.examples

import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMapping
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.resolver.ConditionalAssignmentResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.resolver.EnclosingMatchResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.resolver.OrResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.resolver.WordMatchResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class ExamplePodraCategoryMapping extends CategoryMapping {

    ExamplePodraCategoryMapping() {
        super()
        List<String> perils = ["Earthquake", "Flood", "Storm", "Large", "Attritional", "Cat"]
        List<String> contracts = ["QuotaShare", "Wxl", "Cxl", "Sl"]
        ICategoryResolver m1 = new EnclosingMatchResolver("linesOfBusiness:sub",":",OutputElement.PATH)
        ICategoryResolver m2 = new EnclosingMatchResolver(["subContracts:sub"],contracts, OutputElement.PATH)
        ICategoryResolver m3 = new EnclosingMatchResolver(["claimsGenerators:sub"],perils, OutputElement.PATH)
        ICategoryResolver m41 = new WordMatchResolver(["linesOfBusiness:(?!sub)"], OutputElement.PATH)
        ICategoryResolver m4 = new ConditionalAssignmentResolver("Aggregate", m41)
        matcherMap["Lob"] = new OrResolver([m1,m2,m3,m4])
        matcherMap["Peril"] = new WordMatchResolver(perils, OutputElement.PATH)
        matcherMap["RIContractType"] = new WordMatchResolver(contracts, OutputElement.PATH)
        matcherMap["AccountBasis"] = new WordMatchResolver(["Gross", "Ceded", "Net"], OutputElement.PATH)
        //matcherMap["Keyfigure"] = new EndingMatchResolver("__")
    }
}
