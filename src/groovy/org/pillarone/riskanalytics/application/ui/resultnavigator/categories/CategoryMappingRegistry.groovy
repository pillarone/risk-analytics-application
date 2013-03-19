package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.core.model.Model
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
        Class modelClass = Thread.currentThread().contextClassLoader.loadClass(run.model)
        Model model = modelClass.newInstance()
        Closure mappingClosure = model.createResultNavigatorMapping()
        if (mappingClosure != null) {
            Map<String, ICategoryResolver> categories = MapCategoriesBuilder.getCategories (mappingClosure)
            CategoryMapping mapping = new CategoryMapping(categories)
            cache[run] = mapping
            return mapping
        } else {
            CategoryMapping mapping = new CategoryMapping()
            cache[run] = mapping
            return mapping
        }
    }
}
