package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
class MapCategoriesBuilder extends BuilderSupport {

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

    protected Object createNode(Object name) {
        ICategoryResolver resolver = null
        try {
            resolver = CategoryResolverFactory.getCategoryMatcher((String) name, null, null)
        } catch (Exception ex) {
            
        }
        // println "New node with name ${name}."
        return new MappingEntry(parent: getCurrent(), name: name, value:  resolver);
    }

    protected Object createNode(Object name, Object value) {
        ICategoryResolver resolver = CategoryResolverFactory.getCategoryMatcher((String) name, null, (List) value)
        // println "New node with name ${name}, value ${value.toString()}; parsed resolver ${resolver?.name}"
        return new MappingEntry(parent: getCurrent(), name: name, value: resolver);
    }

    protected Object createNode(Object name, Map attributes) {
        ICategoryResolver resolver = CategoryResolverFactory.getCategoryMatcher((String) name, attributes, null);
        // println "New node with name ${name}, attributes ${attributes}; parsed resolver ${resolver?.name}"
        return new MappingEntry(parent: getCurrent(), name: name, value: resolver)
    }

    protected Object createNode(Object name, Map attributes, Object value) {
        ICategoryResolver resolver = CategoryResolverFactory.getCategoryMatcher((String) name, attributes, (List) value);
        // println "New node with name ${name}, attributes ${attributes}, value ${value}; parsed resolver ${resolver?.name}"
        return new MappingEntry(parent: getCurrent(), name: name, value: resolver);
    }
}
