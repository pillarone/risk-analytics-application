package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.examples.ExamplePodraCategoryMapping
import org.pillarone.riskanalytics.core.output.SimulationRun

/**
 * @author martin.melchior
 */
class CategoryMappingRegistry {

    static Map<SimulationRun, CategoryMapping> cache = [:] // should probably be changed to thread-local

    static CategoryMapping getCategoryMapping(SimulationRun run) {
        if (cache.containsKey(run)) {
            return cache[run]
        }
        if (run.getModel()=="models.podra.PodraModel") { // TODO: load this by some other mechanism
            CategoryMapping mapping = new ExamplePodraCategoryMapping()
            cache[run] = mapping
            return mapping
        } else {
            CategoryMapping mapping = new CategoryMapping()
            cache[run] = mapping
            return mapping
        }
    }
}
