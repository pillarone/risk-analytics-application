package com.canoo.ulc.community.jfreechart.server;

import java.util.EventObject;

import com.ulcjava.base.application.util.Dimension;

/**
 * <code>UlcChartComponentEvent</code> represents an event which occurs when resizing the chart component.
 * 
 * @author marc.hermann@canoo.com
 */
public class UlcChartComponentEvent extends EventObject {
    
    /**
     * <code>fChartComponentSize</code> represents the new size of the chart component.
     */
    private Dimension fChartComponentSize;
    
    /**
     * Creates an instance of <code>UlcChartComponentEvent</code>.
     * 
     * @param source the source on which the event occured
     * @param eventId the action which invoked the event
     * @param chartComponentSize the new size of the chart component
     */
    public UlcChartComponentEvent(Object source, Integer width, Integer height) {
        super(source);
        fChartComponentSize = new Dimension(width.intValue(), height.intValue());
    }
    
    /**
     * @return the new size of the chart component.
     */
    public Dimension getChartComponentSize() {
        return fChartComponentSize;
    }
}
