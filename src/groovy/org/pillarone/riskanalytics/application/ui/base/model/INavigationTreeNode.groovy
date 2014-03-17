package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public interface INavigationTreeNode {

    public String getName()

    public ULCPopupMenu getPopupMenu( ULCTableTree tree)

    public ULCIcon getIcon()

    public Font getFont(String fontName, int fontSize)

    public String getToolTip()

}