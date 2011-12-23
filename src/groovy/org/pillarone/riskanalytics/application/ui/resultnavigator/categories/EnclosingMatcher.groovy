package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import java.util.regex.Pattern
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class EnclosingMatcher implements ICategoryResolver {
    static final String NAME = "EnclosingMatch"
    List<String> prefixes = []
    List<String> suffixes = []
    String refCategory
    Pattern pattern

    EnclosingMatcher(String prefix, String suffix, String refCategory) {
        this.refCategory = refCategory
        initialize([prefix], [suffix])
    }

    EnclosingMatcher(List<String> prefixes, List<String> suffixes, String refCategory) {
        this.refCategory = refCategory
        initialize(prefixes, suffixes)
    }

    String getName() {
        return NAME
    }

    void initialize(List<String> prefixes, List<String> suffixes) {
        this.prefixes = prefixes
        this.suffixes = suffixes

        String prefix = prefixes[0]
        String str = "($prefix"
        for (int i = 1; i < prefixes.size(); i++) {
            prefix = prefixes[i]
            str += "|$prefix"
        }
        String suffix = suffixes[0]
        str += ")(\\w*)($suffix"
        for (int i = 1; i < suffixes.size(); i++) {
            suffix = suffixes[i]
            str += "|$suffix"
        }
        str += ")"
        pattern = ~str
    }

    boolean isResolvable(OutputElement element) {
        if (element.getCategoryValue(refCategory)==null) return false
        java.util.regex.Matcher matcher = element.getCategoryValue(refCategory) =~ pattern
        return matcher.size()>0
    }

    String getResolvedValue(OutputElement element) {
        if (element.getCategoryValue(refCategory)==null) return null
        java.util.regex.Matcher matcher = element.getCategoryValue(refCategory) =~ pattern
        return matcher.size()>0 ? matcher[0][2] : null
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
        buffer.append(matcher[0][1])
        buffer.append("\${${category}}")
        buffer.append(matcher[0][3])
        buffer.append(element.templatePath[(index+size)..-1])
        element.templatePath = buffer.toString()
        return true
    }
}
