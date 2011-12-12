package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.examples.ExamplePodraCategoryMapping
import org.pillarone.riskanalytics.core.output.SimulationRun

/**
 * @author martin.melchior
 */
class CategoryMappingRegistry {

    static AbstractCategoryMapping getCategoryMapping(SimulationRun run) {
        if (run.getModel()=="models.podra.PodraModel") { // TODO: load this by some other mechanism, consider also caching ...
            return new ExamplePodraCategoryMapping()
        }
        return null
    }
}
