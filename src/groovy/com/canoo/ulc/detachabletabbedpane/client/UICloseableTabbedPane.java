package com.canoo.ulc.detachabletabbedpane.client;

import com.ulcjava.base.client.UIIcon;
import com.ulcjava.base.client.UITabbedPane;
import com.ulcjava.base.shared.internal.Anything;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class UICloseableTabbedPane extends UITabbedPane {

    private static final int CLOSE_TAB_ON_CLOSING_TAB = 1;

    protected Object createBasicObject(Object[] args) {
        return new BasicCloseableTabbedPane();
    }

    /**
     * Need to intercept this, because in UITabbedPane.setConstraints the icon is decoded before the title. This causes a call to
     * getIconAtIndex, which causes an error of fontMetrics, because the title is null.
     *
     * @see com.ulcjava.base.client.UITabbedPane#setConstraints(java.awt.Component, java.lang.Object)
     */
    protected void setConstraints(Component basicComponent, Object constraints) {
        Anything a = (Anything) constraints;
        int index = getBasicTabbedPane().indexOfComponent(basicComponent);
        getBasicTabbedPane().setTitleAt(index, (String) a.getObject("title", ""));
        UIIcon uiIcon = (UIIcon) a.getObject("icon");
        getBasicTabbedPane().setIconAt(index, (uiIcon == null ? null : uiIcon.getBasicIcon()));

        super.setConstraints(basicComponent, constraints);
    }

    public class BasicCloseableTabbedPane extends BasicTabbedPane {
        private static final String TAB_EVENT_CATEGORY = "tab";

        private Map titleMap = new HashMap();
        private Map leftIconMap = new HashMap();
        private Map isCloseableMap = new HashMap();
        private Map closeableRendererMap = new HashMap();
        private Map closingDefaultMap = new HashMap();
        private ChangeStateManager changeStateManager;
        private ChangeListener[] disabledChangeListenerRepository;


        public BasicCloseableTabbedPane() {
            changeStateManager = new ChangeStateManager(this);
            addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    changeStateManager.setCurrentVisuallySelected(getSelectedIndex());
                }
            });

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                        triggerClosingTab(getSelectedComponent());
                    }
                }
            });
        }

        public Icon getIconAt(int ix) {
            CloseableTabRenderer tabRenderer = (CloseableTabRenderer) closeableRendererMap.get(getComponentAt(ix));
            if (tabRenderer == null) {
                tabRenderer = new CloseableTabRenderer(getComponentAt(ix), this);
                closeableRendererMap.put(getComponentAt(ix), tabRenderer);
            }

            return tabRenderer;
        }

        public void setTitleAt(int ix, String title) {
            if (ix <= getTabCount()) {
                super.setTitleAt(ix, "");
                titleMap.put(getComponentAt(ix), title);
            }
        }

        public String getCloseableTitle(Component component) {
            return (String) titleMap.get(component);
        }

        public void setIconAt(int ix, Icon leftIcon) {
            if (ix <= getTabCount()) {
                super.setIconAt(ix, null);
                leftIconMap.put(getComponentAt(ix), leftIcon);
            }
        }

        public Icon getLeftIcon(Component component) {
            return (Icon) leftIconMap.get(component);
        }

        public void setCloseableTabMap(Map aCloseableTabMap) {
            Iterator keyIterator = aCloseableTabMap.keySet().iterator();
            while (keyIterator.hasNext()) {
                Integer numberKey = (Integer) keyIterator.next();
                int ix = numberKey.intValue();
                if (ix <= getTabCount()) {
                    isCloseableMap.put(getComponentAt(ix), aCloseableTabMap.get(numberKey));
                }
            }
        }

        public void setCloseableTab(int ix, boolean aIsCloseable) {
            if (ix <= getTabCount()) {
                isCloseableMap.put(getComponentAt(ix), new Boolean(aIsCloseable));
            }
        }

        public void setDefaultCloseTabOperationMap(Map aClosingDefaultMap) {
            Iterator keyIterator = aClosingDefaultMap.keySet().iterator();
            while (keyIterator.hasNext()) {
                Integer numberKey = (Integer) keyIterator.next();
                int ix = numberKey.intValue();
                if (ix <= getTabCount()) {
                    closingDefaultMap.put(getComponentAt(ix), aClosingDefaultMap.get(numberKey));
                }
            }
        }

        public void setDefaultCloseTabOperation(int ix, int closeDefault) {
            if (ix <= getTabCount()) {
                closingDefaultMap.put(getComponentAt(ix), new Integer(closeDefault));
            }
        }

        public boolean isCloseable(Component component) {
            Boolean isCloseable = (Boolean) isCloseableMap.get(component);
            if (isCloseable != null) {
                return isCloseable.booleanValue();
            }
            return true;
        }

        /**
         * Trigger the listener or close directly the tab During this operation, no new value must be set to the ChangeStateManager. This is
         * the reason for setting the isClosingTab variable to true;
         */
        public void triggerClosingTab(Component component) {
            Integer closingDefaultBehaviour = (Integer) closingDefaultMap.get(component);
            int indexOfComponent = indexOfComponent(component);
            changeStateManager.setClosingTabIx(indexOfComponent);
            int nextIndex = changeStateManager.determineFutureNewSelectedIxBeforeClosing();
            setSelectedIndex(nextIndex);
            getBasicTabbedPane().getModel().setSelectedIndex(nextIndex);
            getSession().addDirtyDataOwner(UICloseableTabbedPane.this); // propagate the updatedIndex
            if (closingDefaultBehaviour != null && closingDefaultBehaviour.intValue() == CLOSE_TAB_ON_CLOSING_TAB) {
                performULCCloseTabWithoutTabListener(indexOfComponent);
            } else {
                fireTabClosingULC(indexOfComponent, nextIndex);
            }
        }

        private void performULCCloseTabWithoutTabListener(int indexOfComponent) {
            invokeULC("removeTabAt", new Object[]{new Integer(indexOfComponent)});
        }

        private void eventuallyFireChangeEvent(int nextIndex) {
            if (closingAnAlreadySelectedTab() && getTabCount() > 0 && !isTheMajorTabIndex()) {
                fireSelectionChangedULC();
            }
        }

        private boolean isTheMajorTabIndex() {
            return (getComponentCount() > 0) && (getSelectedIndex() == getComponentCount() - 1);
        }

        private boolean closingAnAlreadySelectedTab() {
            return changeStateManager.getCurrentSelection() == changeStateManager.getPreviousSelection();
        }

        /**
         * Overwrite JTabbedPane API, in order to assure the clean up of the closeable components.
         */
        public void removeTabAt(int index) {
            if (index >= 0 && index < getTabCount()) {
                Component component = (Component) getComponentAt(index);
                if (isCloseable(component)) {
                    changeStateManager.setClosingTabIx(index);
                    closeClosingTab(component);
                    super.removeTabAt(index);
                } else {
                    super.removeTabAt(index);
                }
            }
        }

        protected void closeClosingTab(Component component) {
            if (isCloseable(component)) {
                neutralizeTabActiveArea(component);
                eventuallyFireChangeEvent(getSelectedIndex());
                selectNewComponent(getSelectedIndex());
            }
        }

        protected void fireTabClosingULC(int tabIndex, int nextIndex) {
            fireEventULC(TAB_EVENT_CATEGORY, "tabClosing", new Object[]{new Integer(tabIndex), new Integer(nextIndex)});
        }

        protected void disableChangeListeners() {
            if (disabledChangeListenerRepository == null) {
                disabledChangeListenerRepository = getChangeListeners();
                if (disabledChangeListenerRepository != null) {
                    for (int i = 0; i < disabledChangeListenerRepository.length; i++) {
                        removeChangeListener(disabledChangeListenerRepository[i]);
                    }
                }
            }
        }

        protected void restoreChangeListeners() {
            if (disabledChangeListenerRepository != null && !isMouseInOneActiveCloseableArea()) {
                for (int i = 0; i < disabledChangeListenerRepository.length; i++) {
                    addChangeListener(disabledChangeListenerRepository[i]);
                }
                disabledChangeListenerRepository = null;
            }
        }

        private boolean isMouseInOneActiveCloseableArea() {
            int nrOfTabs = getComponentCount();
            for (int i = 0; i < nrOfTabs; i++) {
                CloseableTabRenderer tabRenderer = (CloseableTabRenderer) getIconAt(i);
                if (tabRenderer != null && tabRenderer.hasMouseOnActiveCloseableArea()) {
                    return true;
                }
            }
            return false;
        }

        private void selectNewComponent(int newSelectedIx) {
            setSelectedIndex(newSelectedIx);
            changeStateManager.setCurrentVisuallySelected(newSelectedIx);
        }

        private void neutralizeTabActiveArea(Component component) {
            CloseableTabRenderer tabRenderer = (CloseableTabRenderer) closeableRendererMap.get(component);
            if (tabRenderer != null)
                tabRenderer.neutralizeTabActiveArea();
        }
    }


    /**
     * Class used to manage the tab selecting behaviour. It keeps track of the previous selected ix, in order to be able to keep the
     * selection when closing an unselected tab (which will only temporarely selected). This class is also used to determine the
     * TabEvent.getNextSelectedTabIndex() information delivered to the user. This information can be used instead of the IChangeListener,
     * which will only fire for manually selected tabs, not for new selected tabs due to a closing tab. This also allows to avoid roundtrip
     * in cases where a closing tab does not need a callback.
     */
    private class ChangeStateManager {
        int currentVisuallySelectedIx = -1;
        int closingTabIx = -1;
        JTabbedPane tabbedPane;

        public ChangeStateManager(JTabbedPane aTabbedPane) {
            tabbedPane = aTabbedPane;
        }

        public void setCurrentVisuallySelected(int ix) {
            currentVisuallySelectedIx = ix;

        }

        public void setClosingTabIx(int ix) {
            closingTabIx = ix;
        }

        public int determineFutureNewSelectedIxBeforeClosing() {
            return determineNewSelectedIx(tabbedPane.getTabCount() - 1);
        }

        public int determineFutureNewSelectedIxAfterClosing() {
            return determineNewSelectedIx(tabbedPane.getTabCount());
        }

        private int determineNewSelectedIx(int tabbedPaneLength) {
            if (closingTabIx < currentVisuallySelectedIx) {
                return currentVisuallySelectedIx - 1;
            }
            return Math.min(currentVisuallySelectedIx, tabbedPaneLength - 1);
        }

        public int getCurrentSelection() {
            return closingTabIx;
        }

        public int getPreviousSelection() {
            return currentVisuallySelectedIx;
        }
    }
}
