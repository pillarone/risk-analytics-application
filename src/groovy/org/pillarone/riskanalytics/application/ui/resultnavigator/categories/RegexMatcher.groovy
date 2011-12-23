package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import java.util.regex.Pattern
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class RegexMatcher implements ICategoryResolver {
    static final String NAME = "ByRegex"
    Pattern pattern
    String refCategory = OutputElement.PATH
    int groupDefiningMemberName

    RegexMatcher(String regex, int groupDefiningMemberName, String refCategory) {
        pattern = ~regex
        this.refCategory = refCategory
        this.groupDefiningMemberName = groupDefiningMemberName
    }

    String getName() {
        return NAME
    }

    boolean isResolvable(OutputElement element) {
        if (element.getCategoryValue(refCategory)==null) return false
        java.util.regex.Matcher matcher = element.getCategoryValue(refCategory) =~ pattern
        return matcher.size()>0
    }

    String getResolvedValue(OutputElement element) {
        if (element.getCategoryValue(refCategory)==null) return null
        java.util.regex.Matcher matcher = element.getCategoryValue(refCategory) =~ pattern
        return matcher.size()>0 ? matcher[0][groupDefiningMemberName] : null
    }

    boolean createTemplatePath(OutputElement element, String category) {
        if (refCategory != OutputElement.PATH) return false
        if (element.templatePath==null) {
            element.templatePath=new String(element.getPath())
        }
        java.util.regex.Matcher matcher = element.getTemplatePath() =~ pattern
        if (matcher.size()==0) return false
        String originalString = matcher[0][0]
        int size = originalString.length()
        int index = element.templatePath.indexOf(originalString)
        StringBuffer buffer = new StringBuffer()
        buffer.append(element.templatePath[0..(index-1)])
        for (int i = 1; i < groupDefiningMemberName; i++) {
            buffer.append(matcher[0][i])
        }
        buffer.append("\${${category}}")
        for (int i = groupDefiningMemberName; i <= matcher.groupCount(); i++) {
            buffer.append(matcher[0][i])
        }
        buffer.append(element.templatePath[(index+size)..-1])
        element.templatePath = buffer.toString()
        return true
    }
}
