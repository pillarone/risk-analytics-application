package org.pillarone.riskanalytics.application.ui.resultnavigator.examples

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */

class PodraModelOutputCategories {

    def mappingClosure = {
        lob {
            or {
                enclosedBy(prefix: ['linesOfBusiness:sub'], suffix: [':'])
                conditionedOn (value: 'Aggregate') {
                    matching(toMatch: ["linesOfBusiness:(?!sub)"])
                }
            }
        }
        peril {
            enclosedBy(prefix: ["claimsGenerators:sub"], suffix: [":"])
        }
        reinsuranceContractType {
            enclosedBy(prefix: ["subContracts:sub","reinsuranceContracts:sub"], suffix: [":"])
        }
        accountBasis {
            matching(toMatch: ["Gross", "Ceded", "Net"])
        }
        keyfigure {
            synonymousTo(category : "Field")
        }
    }
}
