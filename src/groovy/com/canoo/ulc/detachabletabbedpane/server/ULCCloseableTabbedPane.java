package com.canoo.ulc.detachabletabbedpane.server;

import java.util.HashMap;
import java.util.Map;

import com.ulcjava.base.application.ULCTabbedPane;
import com.ulcjava.base.server.IDispatcher;

/**
 * Class inheriting the ULCTabbedPane behaviour, adding the closeable functionality.
 * <p/>
 * The default is that every added tab is closeable. Set the specific value to false, if you need a non closeable tab. If all tabs are non
 * closeable, use the ULCTabbedPane instead.
 * <p/>
 * You can add a ITabListener class to your tabbed pane if you wish to catch a closing tab. For each tab you can specify if its closing will
 * fire a TabEvent or the tab will be closed directly (without roundtrip!)
 * <p/>
 * The Lazy loading behaviour of ULCTabbedPane is maintained. As in ULCTabbedPane you need to specify it explicitely with the call
 * ULCTabbedPane.setPreloadAt(ix, false);
 * <p/>
 * Everytime a selected tab is closed and a new one is selected, a SelectionChangedEvent is fired and can be catched through a
 * ISelectionChangedListener listener.
 * <p/>
 * If a non selected tab is closed, the current selected one will maintain its selection, independently of the eventual new positioning in
 * the tab list. No SelectionChangedEvent will be fired in such a case. When the last tab is closed, the SelectionChangedEvent will be
 * fired, delivering the index -1.
 * <p/>
 * Due to the new closeable icon and behaviour, and due to the fact that the current Java versions do not allow to specify a generic
 * component as tab (this will change with Java 6) the tabbed had to be redrawn. This can eventually cause some side effect when changing
 * the L&F. You maybe need to readapt some values in the UICloseableTabbedPane.UIManager class.
 */

public class ULCCloseableTabbedPane extends ULCTabbedPane {
    
    public final static int FIRE_EVENT_ON_CLOSING_TAB = 0; // default
    public final static int CLOSE_TAB_ON_CLOSING_TAB = 1;
    
    private Map nonCloseableTabMap = null;
    private Map defaultCloseTabOperationMap = null;
    
    protected void uploadStateUI() {
        super.uploadStateUI();
        setStateUI("closeableTabMap", null, nonCloseableTabMap);
        setStateUI("defaultCloseTabOperationMap", null, defaultCloseTabOperationMap);
    }
    
    public void setCloseableTab(int ix, boolean isClosable) {
        
        if (isUploaded()) {
            setCompositeStateUI("closeableTab", new Object[] {new Integer(ix), new Boolean(isClosable)});
        }
        if (nonCloseableTabMap == null) {
            nonCloseableTabMap = new HashMap();
        }
        nonCloseableTabMap.put(new Integer(ix), new Boolean(isClosable));
    }
    
    public void setDefaultCloseTabOperation(int ix, int closeDefault) {
        
        if (isUploaded()) {
            setCompositeStateUI("defaultCloseTabOperation", new Object[] {new Integer(ix), new Integer(closeDefault)});
        }
        if (defaultCloseTabOperationMap == null) {
            defaultCloseTabOperationMap = new HashMap();
        }
        defaultCloseTabOperationMap.put(new Integer(ix), new Integer(closeDefault));
        upload(); // ensure data is at the client
    }
    
    public boolean isCloseable(int ix) {
        if (nonCloseableTabMap != null) {
            Boolean isCloseable = (Boolean)nonCloseableTabMap.get(new Integer(ix));
            if (isCloseable != null) {
                return isCloseable.booleanValue();
            }
        }
        return true;
    }
    
    public int getDefaultCloseTabOperation(int ix) {
        if (defaultCloseTabOperationMap != null) {
            Integer closeDefaultOperation = (Integer)defaultCloseTabOperationMap.get(new Integer(ix));
            if (closeDefaultOperation != null) {
                return closeDefaultOperation.intValue();
            }
        }
        
        return FIRE_EVENT_ON_CLOSING_TAB;
    }
    
    /**
     * Method to be to programmatically close a closeable tab. If the tab was selected, a new one will be selected and a
     * SelectionChangedEvent event will be triggered.
     */
    

    public void closeCloseableTab(int index) {
        if (isCloseable(index)) {
            removeTabAt(index);
        }
    }
    
    public void addTabListener(ITabListener listener) {
        addListener(TabEvent.EVENT_CATEGORY, listener);
    }
    
    public void removeTabListener(ITabListener listener) {
        removeListener(TabEvent.EVENT_CATEGORY, listener);
    }
    
    
    // event handling methods
    
    protected IDispatcher createDispatcher() {
        return new ULCClosableTabbedPaneDispatcher();
    }
    
    protected class ULCClosableTabbedPaneDispatcher extends ULCTabbedPaneDispatcher {
        
        public TabEvent createTabEvent(int tabIx, int nextSelectedIx) {
            return ULCCloseableTabbedPane.this.createTabEvent(tabIx, nextSelectedIx);
        }
        
        public Class getTabListenerClass() {
            return ULCCloseableTabbedPane.this.getTabListenerClass();
        }
        
        public void processTabEvent(String eventCategory, String listenerMethodName, TabEvent tabEvent) {
            ULCCloseableTabbedPane.this.processTabEvent(eventCategory, listenerMethodName, tabEvent);
        }
    }
    
    protected TabEvent createTabEvent(int tabIx, int nextSelectedIx) {
        return new TabEvent(this, tabIx, nextSelectedIx);
    }
    
    protected Class getTabListenerClass() {
        return ITabListener.class;
    }
    
    protected void processTabEvent(String eventCategory, String listenerMethodName, TabEvent tabEvent) {
        dispatchEvent(eventCategory, listenerMethodName, tabEvent);
    }
    
    protected String typeString() {
        return "com.canoo.ulc.detachabletabbedpane.client.UICloseableTabbedPane";
    }
    
}
