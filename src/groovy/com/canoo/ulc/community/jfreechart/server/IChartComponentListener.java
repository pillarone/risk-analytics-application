package com.canoo.ulc.community.jfreechart.server;

import java.util.EventListener;

public interface IChartComponentListener extends EventListener {
    public void chartResized(UlcChartComponentEvent event);
}