package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCComponent

//classes implementing this interface need to be created for all item-"views" (modelling items, batch runs, comparisons etc.)
public interface IUIItem {

    /**
     *  This method returns a String which can be used to represent this item on the ui (window/tab titles etc.)
     * @return
     */
    String createTitle()

    /**
     * Creates a view when the item is opened by the user.
     * Currently a ULCComponent, maybe we can switch to a 'AbstractDetailView'
     * @return
     */
    ULCComponent createDetailView()

    /**
     * Cleanup when an UIItem is closed (remove listeners etc.)
     */
    void close()

    //more common methods to be added


}