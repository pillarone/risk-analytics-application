package com.canoo.ulc.community.renderer.client;

import com.ulcjava.base.client.IRendererComponent;
import com.ulcjava.base.client.UIComponent;
import com.ulcjava.base.client.tabletree.JTableTree;
import com.ulcjava.base.client.tabletree.TableTreeCellRenderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class UIHeaderRenderComponent extends UIComponent implements IRendererComponent, TableTreeCellRenderer {
    public TableCellRenderer getTableCellRenderer() {
        return null;
    }

    public TreeCellRenderer getTreeCellRenderer() {
        return null;
    }

    public ListCellRenderer getListCellRenderer() {
        return null;
    }

    public TableTreeCellRenderer getTableTreeCellRenderer() {
        return this;
    }

    private JButton button;
    private String text;
    private JLabel textLabel;

    @Override
    protected Object createBasicObject(Object[] objects) {
        JPanel pane = new JPanel();
        textLabel = new JLabel();
        button = new JButton();
        button.setText("Test");
        pane.add(textLabel);
        pane.add(button);
        return pane;
    }

    public JPanel getBasicLabel() {
        return (JPanel) getBasicObject();
    }

    public Component getTableTreeCellRendererComponent(JTableTree jTableTree, Object o, boolean b, boolean b1, boolean b2, boolean b3, Object o1, int i, int i1) {
        JPanel panel = getBasicLabel();
        textLabel.setText(o.toString());
        return panel;
    }

    public void setIcon(ImageIcon icon) {
//        this.icon = icon;
//        iconLabel.setIcon(icon);
    }

    public void setText(String text) {
        this.text = text;
        textLabel.setText(text);
    }
}
