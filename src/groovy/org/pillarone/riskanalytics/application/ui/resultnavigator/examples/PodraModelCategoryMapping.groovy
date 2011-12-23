package org.pillarone.riskanalytics.application.ui.resultnavigator.examples

import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.*
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class PodraModelCategoryMapping extends CategoryMapping {

    PodraModelCategoryMapping() {
        super()
        ICategoryResolver m1 = new EnclosingMatcher("linesOfBusiness:sub",":", OutputElement.PATH)
        ICategoryResolver m2 = new ConditionalAssignment("Aggregate", new SingleValueFromListMatcher(["linesOfBusiness:(?!sub)"],OutputElement.PATH))
        matcherMap["lob"] = new OrMatcher([m1,m2])
        matcherMap["peril"] = new EnclosingMatcher("claimsGenerators:sub",":",OutputElement.PATH)
        matcherMap["reinsuranceContractType"] = new EnclosingMatcher(["subContracts:sub","reinsuranceContracts:sub"],[":"],OutputElement.PATH)
        matcherMap["accountBasis"] = new SingleValueFromListMatcher(["Gross", "Ceded", "Net"],OutputElement.PATH)
        matcherMap["keyfigure"] = new SynonymToCategory(OutputElement.FIELD)
    }
}
