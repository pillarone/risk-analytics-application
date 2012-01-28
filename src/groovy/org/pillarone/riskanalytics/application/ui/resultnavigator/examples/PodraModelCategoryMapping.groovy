package org.pillarone.riskanalytics.application.ui.resultnavigator.examples

import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMapping
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryResolver
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.resolver.*

/**
 * @author martin.melchior
 */
class PodraModelCategoryMapping extends CategoryMapping {

    PodraModelCategoryMapping() {
        super()
        ICategoryResolver m1 = new EnclosingMatchResolver("linesOfBusiness:sub",":", OutputElement.PATH)
        ICategoryResolver m2 = new ConditionalAssignmentResolver("Aggregate", new WordMatchResolver(["linesOfBusiness:(?!sub)"],OutputElement.PATH))
        matcherMap["lob"] = new OrResolver([m1,m2])
        matcherMap["peril"] = new EnclosingMatchResolver("claimsGenerators:sub",":",OutputElement.PATH)
        matcherMap["reinsuranceContractType"] = new EnclosingMatchResolver(["subContracts:sub","reinsuranceContracts:sub"],[":"],OutputElement.PATH)
        matcherMap["accountBasis"] = new WordMatchResolver(["Gross", "Ceded", "Net"],OutputElement.PATH)
        matcherMap["keyfigure"] = new SynonymToCategoryResolver(OutputElement.FIELD)
    }
}
