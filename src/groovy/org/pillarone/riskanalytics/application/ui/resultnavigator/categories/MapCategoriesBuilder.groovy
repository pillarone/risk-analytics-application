package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * A Builder class that extends from the BuilderSupport class.
 * It allows to read specifications for a category mapping that are arranged in a hierarchical fashion
 * and using suitable domain-specific key-words. These keywords are defined at the highest hierarchy level
 * by category descriptors that can be assigned any value. At lower levels, the user can choose
 * the names of the available implementations of ICategoryResolver. Examples are
 * <ul>
 *     <it><code>or</code></it>
 *     <it><code>and</code></it>
 *     <it><code>enclosedBy(prefix: ['linesOfBusiness:sub'], suffix: [':'])</code></it>
 *     <it><code>conditionedOn(value: 'Aggregate')</code>: needs to be followed by another closure that defines the condition.</it>
 *     <it><code>matching(toMatch: ["linesOfBusiness:(?!sub)"])</code></it>
 *     <it><code>synonymousTo(category : "Field")</code></it>
 * </ul>
 * A convenience method is also given to construct, from the given closure, a Map with category descriptors as key and
 * ICategoryResolver as values.
 *
 * @author martin.melchior
 */
class MapCategoriesBuilder extends BuilderSupport {

    /**
     * Convenience method to construct, from the given closure, a Map with category descriptors as key and
      * ICategoryResolver as values.
     * Throws an exception in case the expressions specified an in the closure are malformed or the key-words
     * are not known.
     * @param mappingClosure
     * @return
     */
    public static Map<String,ICategoryResolver> getCategories(Closure mappingClosure) {
        MappingEntry rootEntry = (MappingEntry) new MapCategoriesBuilder().invokeMethod("categories", mappingClosure)
        Map<String, ICategoryResolver> mapping = [:]
        for (MappingEntry entry : rootEntry.value) {
            String key = entry.name
            if (((ArrayList) entry.value).size() != 1) {
                println "Invalid category specification found: category name ${key}."
                throw new RuntimeException("Invalid category specification found: category name ${key}.")
            }
            Object value = ((MappingEntry) ((ArrayList) entry.value)[0]).value
            if (!(value instanceof ICategoryResolver)) {
                println "Problem in resolving the mapping for category ${key}."
                throw new RuntimeException("Problem in resolving the mapping for category ${key}.")
            }
            mapping[key] = (ICategoryResolver) value
        }
        return mapping
    }

    /**
     * See BuilderSupport.
     * @param parent
     * @param child
     */
    protected void setParent(Object parent, Object child) {
        Object value = ((MappingEntry)parent).value
        if (value instanceof ICategoryResolver) {
            ((ICategoryResolver)value).addChildResolver((ICategoryResolver) ((MappingEntry)child).value)
        } else if (value) {
            ((ArrayList)value) << child
        }
        if (!value) {
            ((MappingEntry)parent).value = [child]
        }
    }

    /**
     * See BuilderSupport.
     * @param name
     * @return
     */
    protected Object createNode(Object name) {
        ICategoryResolver resolver = null
        try {
            resolver = CategoryResolverFactory.getCategoryMatcher((String) name, null)
        } catch (Exception ex) {

        }
        // println "New node with name ${name}."
        return new MappingEntry(parent: getCurrent(), name: name, value:  resolver);
    }

    /**
     * See BuilderSupport.
     * @param name
     * @param value
     * @return
     */
    protected Object createNode(Object name, Object value) {
        ICategoryResolver resolver = CategoryResolverFactory.getCategoryMatcher((String) name, null)
        // println "New node with name ${name}, value ${value.toString()}; parsed resolver ${resolver?.name}"
        return new MappingEntry(parent: getCurrent(), name: name, value: resolver);
    }

    /**
     * See BuilderSupport.
     * @param name
     * @param attributes
     * @return
     */
    protected Object createNode(Object name, Map attributes) {
        ICategoryResolver resolver = CategoryResolverFactory.getCategoryMatcher((String) name, attributes);
        // println "New node with name ${name}, attributes ${attributes}; parsed resolver ${resolver?.name}"
        return new MappingEntry(parent: getCurrent(), name: name, value: resolver)
    }

    /**
     * See BuilderSupport.
     * @param name
     * @param attributes
     * @param value
     * @return
     */
    protected Object createNode(Object name, Map attributes, Object value) {
        ICategoryResolver resolver = CategoryResolverFactory.getCategoryMatcher((String) name, attributes);
        // println "New node with name ${name}, attributes ${attributes}, value ${value}; parsed resolver ${resolver?.name}"
        return new MappingEntry(parent: getCurrent(), name: name, value: resolver);
    }
}


private class MappingEntry {
    String name
    Object value
    MappingEntry parent
}