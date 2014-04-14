package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener

//classes implementing this interface need to be created for all item-"views" (modelling items, batch runs, comparisons etc.)
interface IUIItem {

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
    ULCContainer createDetailView()

    Object getViewModel()

    /**
     * Cleanup when an UIItem is closed (remove listeners etc.)
     */
    void close()

    /**
     * @param newName
     */
    void rename(String newName)

    void save()

    boolean remove()


    boolean isEditable()

    boolean isLoaded()

    void unload()

    Object getItem()

    String getName()

    String getNameAndVersion()

    String getToolTip()

    ULCIcon getIcon()

    boolean isVersionable()

    boolean isChangeable()

    boolean isChanged()

    boolean isDeletable()

    void removeAllModellingItemChangeListener()

    void addModellingItemChangeListener(IModellingItemChangeListener listener)

    String getWindowTitle()

}