package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import java.util.regex.Pattern
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class SingleValueFromListMatcher implements ICategoryResolver {
    static final String NAME = "BySingleValue"

    String refCategory = OutputElement.PATH
    List<Pattern> patterns
    List<String> toMatch = []

    SingleValueFromListMatcher(List<String> toMatch, String refCategory) {
        this.refCategory = refCategory
        initialize(toMatch)
    }

    void initialize(List<String> toMatch) {
        this.toMatch = toMatch
        patterns = []
        for (String s : toMatch) {
            patterns.add(~s)
        }
    }

    String getName() {
        return NAME
    }

    boolean isResolvable(OutputElement element) {
        if (element.getCategoryValue(refCategory)==null) return false
        boolean isFound = false
        for (Pattern pattern : patterns) {
            java.util.regex.Matcher matcher = element.getCategoryValue(refCategory) =~ pattern
            if (matcher.size()>0) {
                if (isFound) {
                    return false
                }
                isFound = true
            }
        }
        return isFound
    }

    String getResolvedValue(OutputElement element) {
        if (element.getCategoryValue(refCategory)==null) return null
        boolean isFound = false
        String value = null
        for (int i = 0; i < patterns.size(); i++) {
            java.util.regex.Matcher matcher = element.getCategoryValue(refCategory) =~ patterns[i]
            if (matcher.size()>0) {
                if (isFound) {
                    return null
                }
                isFound = true
                value = toMatch[i]
            }
        }
        return value
    }

    boolean createTemplatePath(OutputElement element, String category) {
        try {
            if (refCategory != OutputElement.PATH) return false
            if (element.templatePath==null) {
                element.templatePath=new String(element.getPath())
            }
            String originalString = getResolvedValue(element)
            int size = originalString.length()
            int index = element.templatePath.indexOf(originalString)
            StringBuffer buffer = new StringBuffer()
            buffer.append(element.templatePath[0..(index-1)])
            buffer.append("\${${category}}")
            if (index+size<element.templatePath.size()) {
                buffer.append(element.templatePath[(index+size)..-1])
            }
            element.templatePath = buffer.toString()
            return true
        } catch (Exception ex) {
            return false
        }
    }
}
