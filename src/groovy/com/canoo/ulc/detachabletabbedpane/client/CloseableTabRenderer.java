package com.canoo.ulc.detachabletabbedpane.client;

import com.canoo.ulc.detachabletabbedpane.client.UICloseableTabbedPane.BasicCloseableTabbedPane;
import com.ulcjava.base.client.UITabbedPane;

import javax.swing.*;
import javax.swing.plaf.TabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CloseableTabRenderer implements Icon {

    private final static Icon closeIcon = new ImageIcon(CloseableTabRenderer.class.getResource("/org/pillarone/riskanalytics/application/icons/close-active.png"));
    private final static Icon rolloverIcon = new ImageIcon(CloseableTabRenderer.class.getResource("/org/pillarone/riskanalytics/application/icons/close-inactive.png"));
    private UICloseableTabbedPane.BasicCloseableTabbedPane tabbedPane;
    private Rectangle activeClosableArea = new Rectangle(0, 0, -1, -1);  //empty area needs a negative dimension
    private Rectangle tabArea = new Rectangle(0, 0, -1, -1);
    private Component referringComponent;
    private FontMetrics fontMetrics;
    private int tabHeight;
    private int tabWidth;
    private boolean rollingOver;
    private boolean mouseIsOnTab;


    public CloseableTabRenderer(Component component, UICloseableTabbedPane.BasicCloseableTabbedPane tabbed) {
        referringComponent = component;
        tabbedPane = tabbed;
        tabbedPane.addMouseListener(new ClosableTabMouseListener());
        tabbedPane.addMouseMotionListener(new ClosableTabMouseMotionListener());
        setBounds(tabbed);
    }

    public void neutralizeTabActiveArea() {
        activeClosableArea = new Rectangle(0, 0, -1, -1);
    }

    public int getIconHeight() {
        return tabHeight;
    }

    public int getIconWidth() {
        return tabWidth;
    }

    public void paintIcon(Component component, Graphics g, int x, int y) {
        tabArea.setBounds(x, y, getIconWidth(), getIconHeight());
        translateTabForSpecialLAndF(tabArea);
        Icon leftIcon = tabbedPane.getLeftIcon(referringComponent);
        if (leftIcon != null) {
            leftIcon.paintIcon(component, g, UIManagement.getLeftIconXPosition(x), UIManagement.getIconYPosition(y, leftIcon, tabHeight));
        }
        String closableTitle = tabbedPane.getCloseableTitle(referringComponent);
        if (closableTitle != null) {                             //FIXME: Title might not be null, then string title is drawn then by normal Layout
            g.drawString(closableTitle, UIManagement.getTitleXPosition(x, leftIcon), UIManagement.getTitleYPosition(y, fontMetrics, tabHeight));
        }
        manageCloseActiveArea(component, g, x, y, closableTitle);
    }

    public boolean hasMouseOnActiveCloseableArea() {
        return rollingOver;
    }

    private void manageCloseActiveArea(Component component, Graphics g, int x, int y, String closableTitle) {
        //System.err.println(((TabbedPaneUI)UIManager.getUI(tabbedPane)).getTabBounds(tabbedPane,tabbedPane.indexOfComponent(referringComponent)));
        //Rectangle bounds = ((TabbedPaneUI)UIManager.getUI(tabbedPane)).getTabBounds(tabbedPane,tabbedPane.indexOfComponent(referringComponent));
        //tabArea.setBounds(bounds.x,bounds.y,bounds.width, bounds.height);

        if (isClosable()) {
            x = UIManagement.getCloseIconXPosition(x, tabbedPane.getLeftIcon(referringComponent), fontMetrics, closableTitle);
            y = UIManagement.getIconYPosition(y, closeIcon, tabHeight);

            activeClosableArea.setBounds(x, y, closeIcon.getIconWidth(), closeIcon.getIconHeight());
            translateActiveCloseForSpecialLAndF(tabArea, activeClosableArea, closableTitle, tabbedPane.getLeftIcon(referringComponent), closeIcon);

            if (tabIsSelected() || mouseIsOnTab) {
                if (withRollOverEffect()) {
                    if (rollingOver) {
                        closeIcon.paintIcon(component, g, x, y);
                    } else {
                        rolloverIcon.paintIcon(component, g, x, y);
                    }
                } else {
                    closeIcon.paintIcon(component, g, x, y);
                }
            }
        }
    }

    private void translateActiveCloseForSpecialLAndF(Rectangle tabArea, Rectangle closableArea, String closableTitle, Icon leftIcon, Icon closeIcon) {
        TabbedPaneUI tabbedPaneUI = (TabbedPaneUI) UIManager.getUI(tabbedPane);
        if (tabbedPaneUI.getClass().getName().startsWith("apple.laf.AquaTabbedPaneUI")) {
            if (tabbedPane.getTabPlacement() == UITabbedPane.LEFT) {
                int x = UIManagement.getIconYOffset(closeIcon, tabHeight);
                int y = (int) tabArea.getY() + UIManagement.getCloseIconXRightOffset(fontMetrics, closableTitle, leftIcon, closeIcon);
                int height = (int) closableArea.getWidth();
                int width = (int) closableArea.getHeight();
                closableArea.setBounds(x, y, width, height);
            } else if (tabbedPane.getTabPlacement() == UITabbedPane.RIGHT) {
                int x = UIManagement.getIconYOffset(closeIcon, tabHeight) + (int) tabArea.getX();
                int y = (int) tabArea.getY() + UIManagement.getCloseIconXLeftOffset(leftIcon, fontMetrics, closableTitle);
                int height = (int) closableArea.getWidth();
                int width = (int) closableArea.getHeight();
                closableArea.setBounds(x, y, width, height);
            }
        }
    }

    private void translateTabForSpecialLAndF(Rectangle areaToChange) {
        TabbedPaneUI tabbedPaneUI = (TabbedPaneUI) UIManager.getUI(tabbedPane);
        if (tabbedPaneUI.getClass().getName().startsWith("apple.laf.AquaTabbedPaneUI")) {
            if (tabbedPane.getTabPlacement() == UITabbedPane.LEFT || tabbedPane.getTabPlacement() == UITabbedPane.RIGHT) {
                int x = (int) areaToChange.getX() - (int) areaToChange.getHeight();
                int y = (int) areaToChange.getY();
                int height = (int) areaToChange.getWidth();
                int width = (int) areaToChange.getHeight();
                areaToChange.setBounds(x, y, width, height);
            }
        }

    }

    private boolean withRollOverEffect() {
        return rolloverIcon != null;
    }

    private boolean tabIsSelected() {
        return tabbedPane.getSelectedComponent() == referringComponent;
    }

    private boolean isClosable() {
        return tabbedPane.isCloseable(referringComponent);
    }

    private void setBounds(BasicCloseableTabbedPane tabbed) {
        String closableTitle = tabbed.getCloseableTitle(referringComponent);
        Font font = tabbed.getFont();
        fontMetrics = tabbed.getFontMetrics(font);
        Icon leftIcon = tabbed.getLeftIcon(referringComponent);
        tabWidth = UIManagement.calculateTabWidth(fontMetrics, closableTitle, leftIcon, closeIcon);
        tabHeight = UIManagement.calculateTabHeight(tabbed.getTabPlacement(), fontMetrics, leftIcon, closeIcon);
    }

    public class ClosableTabMouseMotionListener extends MouseMotionAdapter {
        public void mouseMoved(MouseEvent e) {
            if (!tabArea.isEmpty()) {
                if (tabArea.contains(e.getX(), e.getY())) {
                    mouseIsOnTab = true;
                } else {
                    mouseIsOnTab = false;
                }
                if (activeClosableArea.contains(e.getX(), e.getY())) {
                    tabbedPane.disableChangeListeners();
                    rollingOver = true;
                } else {
                    tabbedPane.restoreChangeListeners();
                    rollingOver = false;
                }
            }
            tabbedPane.repaint();
        }
    }

    public class ClosableTabMouseListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            if (!e.isConsumed() && activeClosableArea.contains(e.getX(), e.getY())) {
                tabbedPane.triggerClosingTab(referringComponent);
                e.consume();
            }
        }
    }


    /*
      * Used for UI management of the tabs.
      * If you have problems with some specific L&F, adapt some values here.
      */
    private static class UIManagement {
        static public int getTextIconGap() {
            return UIManager.getInt("TabbedPane.textIconGap");
        }

        static public Insets getTabInsets() {
            return UIManager.getInsets("TabbedPane.tabInsets");
        }

        static public Insets getTabAreaInsets() {
            return UIManager.getInsets("TabbedPane.tabAreaInsets");
        }

        public static int calculateTabHeight(int tabPlacement, FontMetrics metrics, Icon leftIcon, Icon closeIcon) {
            int fontHeight = metrics.getHeight();
            int height = 0;
            height += fontHeight;
            Insets tabInsets = getTabInsets();
            if (leftIcon != null) {
                height = Math.max(height, leftIcon.getIconHeight());
            }
            height = Math.max(height, closeIcon.getIconHeight());
            height += tabInsets.top + tabInsets.bottom + 2;
            return height;
        }

        public static int calculateTabWidth(FontMetrics metrics, String title, Icon leftIcon, Icon closeIcon) {

            Insets tabInsets = getTabInsets();
            int width = tabInsets.left + tabInsets.right;
            if (leftIcon != null) {
                width += leftIcon.getIconWidth() + getTextIconGap();
            }
            if (title != null && metrics != null) {
                width += SwingUtilities.computeStringWidth(metrics, title);
            }

            width += closeIcon.getIconWidth() + getTextIconGap();
            return width;
        }

        public static int getLeftIconXPosition(int x) {
            return x + getLeftIconOffset();
        }

        private static int getLeftIconOffset() {
            return getTabInsets().left / 2;
        }

        public static int getTitleXPosition(int x, Icon leftIcon) {
            return x + getTitleOffset(leftIcon);
        }

        private static int getTitleOffset(Icon leftIcon) {
            if (leftIcon == null) {
                return getTabInsets().left / 2;
            } else {
                return UIManagement.getLeftIconOffset() + leftIcon.getIconWidth() + getTextIconGap();
            }
        }

        public static int getTitleYPosition(int y, FontMetrics fontMetrics, int tabHeight) {
            int offset = (tabHeight - fontMetrics.getHeight()) / 2;
            return y + offset + fontMetrics.getHeight() - fontMetrics.getDescent();
        }

        public static int getIconYPosition(int y, Icon icon, int tabHeight) {
            int offset = getIconYOffset(icon, tabHeight);
            return y + offset;
        }

        private static int getIconYOffset(Icon icon, int tabHeight) {
            int offset = (tabHeight - icon.getIconHeight()) / 2;
            return offset;
        }

        public static int getCloseIconXPosition(int x, Icon leftIcon, FontMetrics metrics, String title) {
            return x + getCloseIconXLeftOffset(leftIcon, metrics, title);
        }

        public static int getCloseIconXLeftOffset(Icon leftIcon, FontMetrics metrics, String title) {
            int width = 0;
            if (leftIcon != null) width += getTitleOffset(leftIcon);
            width += getTabInsets().left / 2 + getTabInsets().right / 2;
            if (title != null && metrics != null) width += SwingUtilities.computeStringWidth(metrics, title);
            width += getTextIconGap();
            return width;
        }

        public static int getCloseIconXRightOffset(FontMetrics metrics, String title, Icon leftIcon, Icon closeIcon) {
            return calculateTabWidth(metrics, title, leftIcon, closeIcon) - getCloseIconXLeftOffset(leftIcon, metrics, title);
        }
    }

}


