package org.pillarone.riskanalytics.application.client;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;


public class WindowsTextFieldUI extends com.sun.java.swing.plaf.windows.WindowsTextFieldUI {
    private SelectionHandler selectionHandler;

    public WindowsTextFieldUI() {
        selectionHandler = new SelectionHandler();
    }

    protected void installListeners() {
        selectionHandler.install(getComponent());
    }

    protected void uninstallListeners() {
        selectionHandler.uninstall();
    }

    public static ComponentUI createUI(JComponent component) {
        return new WindowsTextFieldUI();
    }
}
