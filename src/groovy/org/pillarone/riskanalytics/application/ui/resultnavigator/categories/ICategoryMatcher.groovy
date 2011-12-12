package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
public interface ICategoryMatcher {
    boolean isMatch(String path)
    String getMatch(String path)
    Matcher matcherType()
}

