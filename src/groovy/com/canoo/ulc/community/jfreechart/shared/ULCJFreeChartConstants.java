package com.canoo.ulc.community.jfreechart.shared;

import com.ulcjava.base.application.util.Color;

/**
 * <code>ULCJFreeChartConstants</code> provides some useful constants and methods which are used both by the client and the server.
 * 
 * @author marc.hermann@canoo.com
 */
public final class ULCJFreeChartConstants {
    
    /**
     * <code>ULC_CHART_EVENT</code> represents the event which occurs when clicking on a chart.
     */
    public static final String ULC_CHART_EVENT = "ulcChart";
    
    /**
     * <code>ULC_CHART_COMPONENT_EVENT</code> represents the event which occurs when resizing the chart component.
     */
    public static final String ULC_CHART_COMPONENT_EVENT = "ulcChartComponent";
    
    /**
     * <code>COLORS</code> contains all 56 colors which are provided by Microsoft Excel. The standard colors start at index 40.
     * 
     * @see <code>STANDARD_COLORS_START_INDEX</code>
     */
    public static final Color[] COLORS = new Color[] {new Color(0, 0, 0), new Color(153, 51, 0), new Color(51, 51, 0), new Color(0, 51, 0),
            new Color(0, 51, 102), new Color(0, 0, 128), new Color(51, 51, 153), new Color(51, 51, 51), new Color(128, 0, 0),
            new Color(255, 102, 0), new Color(128, 128, 0), new Color(0, 128, 0), new Color(0, 128, 128), new Color(0, 0, 255),
            new Color(102, 102, 153), new Color(128, 128, 128), new Color(255, 0, 0), new Color(255, 153, 0), new Color(153, 204, 0),
            new Color(51, 153, 102), new Color(51, 204, 204), new Color(51, 102, 255), new Color(128, 0, 128), new Color(150, 150, 150),
            new Color(255, 0, 255), new Color(255, 204, 0), new Color(255, 255, 0), new Color(0, 255, 0), new Color(0, 255, 255),
            new Color(0, 204, 255), new Color(153, 51, 102), new Color(192, 192, 192), new Color(255, 153, 204), new Color(255, 204, 153),
            new Color(255, 255, 153), new Color(204, 255, 204), new Color(204, 255, 255), new Color(153, 204, 255),
            new Color(204, 153, 255), new Color(255, 255, 255), new Color(153, 153, 255), new Color(153, 51, 102),
            new Color(255, 255, 204), new Color(204, 255, 255), new Color(128, 0, 128), new Color(255, 128, 128), new Color(0, 102, 204),
            new Color(204, 204, 255), new Color(0, 0, 128), new Color(255, 0, 255), new Color(255, 255, 0), new Color(0, 255, 255),
            new Color(102, 0, 102), new Color(128, 0, 0), new Color(0, 128, 128), new Color(0, 0, 255)};
    
    /**
     * <code>STANDARD_COLORS_START_INDEX</code> is the start index for the standard colors in the <code>COLORS</code> array.
     * 
     * @see <code>COLORS</code>
     */
    public static final int STANDARD_COLORS_START_INDEX = 40;
    
    /**
     * <code>DEFAULT_BORDER_COLOR</code> is the default color for the border of an entity.
     */
    public static final Color DEFAULT_BORDER_COLOR = Color.black;
    
    /**
     * <code>DEFAULT_BACKGROUND_COLOR</code> is the default color for the chart's background.
     */
    public static final Color DEFAULT_BACKGROUND_COLOR = COLORS[31];
    
    private ULCJFreeChartConstants() {
    }
    
    /**
     * Converts a <code>java.awt.Color</code> in a <code>com.ulcjava.base.application.util.Color</code>.
     * <p>
     * Since JFreeChart uses <code>java.awt.Color</code> but ULC needs <code>com.ulcjava.base.application.util.Color</code>, this methods
     * enables to swap between these two color types.
     * 
     * @param color the <code>java.awt.Color</code> which needs to be converted
     * @return a <code>com.ulcjava.base.application.util.Color</code> which is equal to <code>color</code>.
     */
    public static final Color convertColor(java.awt.Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue());
    }
    
    /**
     * Converts a <code>com.ulcjava.base.application.util.Color</code> in a <code>java.awt.Color</code>.
     * <p>
     * Since JFreeChart uses <code>java.awt.Color</code> but ULC needs <code>com.ulcjava.base.application.util.Color</code>, this methods
     * enables to swap between these two color types.
     * 
     * @param color the <code>com.ulcjava.base.application.util.Color</code> which needs to be converted
     * @return a <code>java.awt.Color</code> which is equal to <code>color</code>.
     */
    public static final java.awt.Color convertColor(Color color) {
        return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
    }
}