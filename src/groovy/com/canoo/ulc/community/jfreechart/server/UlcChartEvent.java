package com.canoo.ulc.community.jfreechart.server;

import java.util.EventObject;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;

/**
 * <code>UlcChartEvent</code> represents an event which occurs when clicking on a chart. The <code>UlcChartEvent</code> knows the entity
 * that has been clicked on if there is any.
 * 
 * @author marc.hermann@canoo.com
 * @see <code>ULCChartEntity</code>
 */
public class UlcChartEvent extends EventObject {
    
    /**
     * <code>fEntity</code> is the chart entity that has been clicked on.
     */
    private ULCChartEntity fEntity;
    
    private int fX;
    private int fY;
    private int fWidth;
    private int fHeight;
    
    /**
     * Use this constructor for creating <code>UlcChartEvent</code>s which don't have a chart entity.
     * 
     * @param source the source on which the event occured
     * @param listenerMethodName the action which invoked the event
     */
    public UlcChartEvent(Object source) {
        this(source, -1, -1, -1, -1);
    }
    
    /**
     * Use this constructor for creating <code>UlcChartEvent</code>s which have a chart entity.
     * 
     * @param source the source on which the event occured
     * @param listenerMethodName the action which invoked the event
     * @param entity the <code>ULCChartEntity</code> that has been clicked on
     * @see <code>ULCChartEntity</code>
     */
    public UlcChartEvent(Object source, ULCChartEntity entity) {
        this(source);
        fEntity = entity;
    }
    
    public UlcChartEvent(Object source, int x, int y, int widht, int height) {
        
        super(source);
        
        fX = x;
        fY = y;
        fWidth = widht;
        fHeight = height;
    }
    
    /**
     * @return the chart
     */
    public JFreeChart getChart() {
        return ((ULCJFreeChart)getSource()).getChart();
    }
    
    /**
     * @return the chart entity
     */
    public ChartEntity getChartEntity() {
        return fEntity.getChartEntity();
    }
    
    public int getHeight() {
        return fHeight;
    }
    
    public int getWidth() {
        return fWidth;
    }
    
    public int getX() {
        return fX;
    }
    
    public int getY() {
        return fY;
    }
}
