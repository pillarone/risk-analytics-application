package com.canoo.ulc.detachabletabbedpane.server;

import com.ulcjava.base.application.event.IEventListener;

public interface ITabListener extends IEventListener{
    public void tabClosing(TabEvent event);
}
