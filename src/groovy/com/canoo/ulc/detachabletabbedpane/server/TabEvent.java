package com.canoo.ulc.detachabletabbedpane.server;

import java.util.EventObject;


public class TabEvent extends EventObject {
	
	public static final String EVENT_CATEGORY = "tab";
	
	int tabClosingIndex;
	int nextSelectedIx;
	
	public TabEvent(ULCCloseableTabbedPane tabbedPane, int aTabIx, int aNextSelectedIx){
		super(tabbedPane);
		tabClosingIndex = aTabIx;
		nextSelectedIx = aNextSelectedIx;
	}
	
	public ULCCloseableTabbedPane getClosableTabbedPane(){
		return (ULCCloseableTabbedPane)getSource();
	}
	
	public int getTabClosingIndex(){
		return tabClosingIndex;
	}
	
	public int getNextSelectedTabIndex(){
		return nextSelectedIx;
	}
}