package com.canoo.ulc.community.jfreechart.server;

import java.util.EventListener;

/**
 * <code>IChartEventListener</code> is the listener interface for receiving chart events. The class that is interessed in processing a chart
 * event implements this interface. When the chart event occurs, the implemented interface method (depending on the exact chart event) is
 * invoked.
 * 
 * @author marc.hermann@canoo.com
 * @see <code>UlcChartEvent</code>
 */
public interface IChartEventListener extends EventListener {
    
    /**
     * Invoked when a chart clicked event occurs.
     * 
     * @param event the chart event
     */
    public void chartClicked(UlcChartEvent event);
    
    /**
     * Invoked when a chart entity clicked event occurs.
     * 
     * @param event the chart event
     */
    public void chartEntityClicked(UlcChartEvent event);
    
    /**
     * Invoked when a chart zoomed event occurs.
     * 
     * @param event the chart event
     */
    public void chartZoomed(UlcChartEvent event);
}
