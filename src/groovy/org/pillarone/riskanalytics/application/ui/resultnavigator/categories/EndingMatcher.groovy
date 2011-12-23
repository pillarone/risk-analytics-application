package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class EndingMatcher implements ICategoryResolver {
    static final String NAME = "ByEnding"
    String prefix
    String refCategory = OutputElement.PATH

    EndingMatcher(String prefix, String refCategory) {
        this.refCategory = refCategory
        this.prefix = prefix
    }

    String getName() {
        return NAME
    }

    boolean isResolvable(OutputElement element) {
        if (element.getCategoryValue(refCategory)==null) return false
        int index = ((String)element.getCategoryValue(refCategory)).indexOf(prefix)
        return index>=0 && index+prefix.length() < ((String)element.getCategoryValue(refCategory)).length()-1
    }

    String getResolvedValue(OutputElement element) {
        if (element.getCategoryValue(refCategory)==null) return null
        int index = ((String)element.getCategoryValue(refCategory)).indexOf(prefix)+prefix.length()
        if (index>=0 && index < ((String)element.getCategoryValue(refCategory)).length()-1) {
            return ((String)element.getCategoryValue(refCategory))[index..-1]
        }
        return null
    }

    boolean createTemplatePath(OutputElement element, String category) {
        if (refCategory != OutputElement.PATH) return false
        if (element.templatePath==null) {
            element.templatePath=new String(element.getPath())
        }
        int index = element.templatePath.indexOf(prefix)+prefix.length()
        if (index>=0 && index < element.templatePath.length()-1) {
            String originalString = element.templatePath[index..-1]
            int size = originalString.length()
            StringBuffer buffer = new StringBuffer()
            buffer.append(element.templatePath[0..(index-1)])
            buffer.append("\${${category}}")
            element.templatePath = buffer.toString()
            return true
        }
        return false
    }
}
