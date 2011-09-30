package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel

//classes implementing this interface need to be created for all item-"views" (modelling items, batch runs, comparisons etc.)
public interface IUIItem {

    /**
     *  This method returns a String which can be used to represent this item on the ui (window/tab titles etc.)
     * @return
     */
    public String createTitle()

    /**
     * Creates a view when the item is opened by the user.
     * Currently a ULCComponent, maybe we can switch to a 'AbstractDetailView'
     * @return
     */
    public ULCContainer createDetailView()

    public Object getViewModel()

    /**
     * Cleanup when an UIItem is closed (remove listeners etc.)
     */
    public void close()

    /**
     * @param newName
     */
    public void rename(String newName)

    public void save()

    public boolean remove()


    public boolean isEditable()

    public boolean isLoaded()

    public void unload()

    public Object getItem()

    public String getName()
    public String getNameAndVersion()

    public String getToolTip()

    public ULCIcon getIcon()

    public boolean isVersionable()

    public boolean isChangeable()

    public boolean isChanged()

    public boolean isDeletable()

    public void removeAllModellingItemChangeListener()

    public void addModellingItemChangeListener(IModellingItemChangeListener listener)

    public String getWindowTitle()

}