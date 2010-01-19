package org.pillarone.riskanalytics.application.client;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.*;

public class SelectionHandler extends MouseAdapter implements HierarchyListener, KeyListener {
    private boolean mouseEvent;
    JTextComponent component;
    JTable table;

    public SelectionHandler() {
    }

    public void mousePressed(MouseEvent event) {
        mouseEvent = true;
    }

    public void mouseReleased(MouseEvent event) {
        mouseEvent = false;
    }

    public void install(JTextComponent component) {
        this.component = component;
        component.addKeyListener(this);
        component.addMouseListener(this);
        component.addHierarchyListener(this);
    }

    public void uninstall() {
        component.removeKeyListener(this);
        component.removeMouseListener(this);
        component.removeHierarchyListener(this);
        component = null;
    }

    public void hierarchyChanged(HierarchyEvent e) {
        if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) > 0) {
            if (e.getChangedParent() instanceof JTable) {
                table = (JTable) e.getChangedParent();
                if (mouseEvent) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            component.selectAll();
                        }
                    });
                } else {
                    component.selectAll();
                    component.requestFocus();
                }
            }
        }
    }

    public void keyTyped(java.awt.event.KeyEvent e) {
    }

    public void keyPressed(java.awt.event.KeyEvent e) {

        if (table != null && KeyEvent.VK_ENTER == e.getKeyCode()) {
            int row = table.getSelectedRow();
            table.getSelectionModel().setSelectionInterval(row + 1, row + 1);
        }
    }

    public void keyReleased(java.awt.event.KeyEvent e) {
        if (KeyEvent.VK_F2 == e.getKeyCode()) {
            component.setCaretPosition(component.getText().length());
        }
    }
}
