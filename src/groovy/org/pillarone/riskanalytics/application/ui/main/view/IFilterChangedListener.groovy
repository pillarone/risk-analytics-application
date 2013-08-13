package org.pillarone.riskanalytics.application.ui.main.view

import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.FilterDefinition


public interface IFilterChangedListener {

    void filterChanged(FilterDefinition filterDefinition)

}