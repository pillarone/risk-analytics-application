package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCTabbedPane
import com.ulcjava.base.application.event.ISelectionChangedListener
import com.ulcjava.base.application.event.SelectionChangedEvent
import com.ulcjava.base.application.util.ULCIcon

public class EnableTabbedPaneIconListener implements ISelectionChangedListener {
    ULCIcon enabled
    ULCIcon disabled
    int tabIndex

    public EnableTabbedPaneIconListener(int tabIndex, ULCIcon enabled, ULCIcon disabled) {
        this.@enabled = enabled
        this.@disabled = disabled
        this.@tabIndex = tabIndex
    }

    public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {
        ULCTabbedPane tabbedPane = (ULCTabbedPane) selectionChangedEvent.getSource();
        int selection = tabbedPane.getSelectedIndex();
        if (selection == tabIndex) {
            tabbedPane.setIconAt(tabIndex, enabled)
        } else {
            tabbedPane.setIconAt(tabIndex, disabled)
        }
    }
}