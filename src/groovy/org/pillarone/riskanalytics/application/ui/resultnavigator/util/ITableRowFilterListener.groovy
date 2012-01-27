package org.pillarone.riskanalytics.application.ui.resultnavigator.util

import com.ulcjava.base.application.table.TableRowFilter

/**
 * Listener that reacts on changes in the filter set for the OutputElementTable.
 * It can be used to enable or disable GUI fields that depend on the type of filter.
 *
 * @author martin.melchior
 */
public interface ITableRowFilterListener {
    /**
     * Setter for the type of filter.
     * @param filter
     */
    void setFilter(TableRowFilter filter)
}