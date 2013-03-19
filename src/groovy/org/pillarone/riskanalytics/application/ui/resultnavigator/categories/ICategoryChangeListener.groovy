package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
public interface ICategoryChangeListener {

    /**
     * Notifies once a category is added.
     * @param category
     */

    void categoryAdded(String category)

    /**
     * Notifies once a category is removed.
     * @param category
     */
    void categoryRemoved(String category)
}