package org.pillarone.riskanalytics.application.client;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

public class MetalTextFieldUI extends javax.swing.plaf.metal.MetalTextFieldUI {
    private SelectionHandler selectionHandler;

    public MetalTextFieldUI() {
        selectionHandler = new SelectionHandler();
    }

    protected void installListeners() {
        selectionHandler.install(getComponent());
    }

    protected void uninstallListeners() {
        selectionHandler.uninstall();
    }

    public static ComponentUI createUI(JComponent component) {
        return new MetalTextFieldUI();
    }
}
