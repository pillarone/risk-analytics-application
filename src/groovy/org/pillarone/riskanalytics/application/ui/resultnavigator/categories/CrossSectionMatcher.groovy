package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
class CrossSectionMatcher implements ICategoryMatcher {
    ICategoryMatcher matcher1
    ICategoryMatcher matcher2
    int minLength

    CrossSectionMatcher(ICategoryMatcher matcher1, ICategoryMatcher matcher2, int minLength) {
        this.matcher1 = matcher1
        this.matcher2 = matcher2
        this.minLength = minLength
    }

    boolean isMatch(String path) {
        return getCrossSection(path) != null
    }

    String getMatch(String path) {
        return getCrossSection(path)
    }

    private String getCrossSection(String path) {
        String str1 = matcher1.getMatch(path)
        String str2 = matcher2.getMatch(path)
        if (!str1 || !str2) {
            return null
        }
        if (str1.length()>str2.length()) {
            String x = str1
            str1=str2
            str2=x
        }
        int n = str1.length()
        while (n>=minLength) {
            for (int pos = 0; pos < str1.length()-n; pos++) {
                String searchString = str1[pos..pos+n]
                if (str2.indexOf(searchString)>=0) {
                    return searchString
                }
            }
            n--
        }
        return null
    }

    Matcher matcherType() {
        return Matcher.BY_CROSS_SECTION
    }
}
