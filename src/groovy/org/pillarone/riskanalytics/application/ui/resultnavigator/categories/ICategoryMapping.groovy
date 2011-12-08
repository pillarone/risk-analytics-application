package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
public interface ICategoryMapping {

    List<String> getCategories()

    String getCategoryMember(String category, String path)


}