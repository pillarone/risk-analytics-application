package com.canoo.ulc.community.jfreechart.server;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotRenderingInfo;

import com.canoo.ulc.community.jfreechart.shared.ULCJFreeChartConstants;
import com.ulcjava.base.application.ULCLabel;
import com.ulcjava.base.application.util.Dimension;
import com.ulcjava.base.application.util.ULCIcon;
import com.ulcjava.base.server.IDispatcher;
import com.ulcjava.base.server.ULCSession;

public class ULCJFreeChart extends ULCLabel {

    /**
     * <code>fChart</code> represents the <code>JFreeChart</code> object.
     */
    private JFreeChart fChart;

    /**
     * <code>fChartSize</code> represents the chart's size.
     */
    private Dimension fChartSize;

    /**
     * <code>fChartRenderingInfo</code> stores all informations about a chart which has been writing to an image.
     */
    private ChartRenderingInfo fChartRenderingInfo;

    /**
     * <code>fChartEntities</code> contains all chart entities, namely item and legend entities.
     */
    private List fChartEntities;

    /**
     * <code>fShowHorizontalTraceLine</code> determines whether the horizontal trace line for zooming issues is shown or not.
     */
    private boolean fShowHorizontalTraceLine;

    /**
     * <code>fShowVetricalTraceLine</code> determines whether the vertical trace line for zooming issues is shown or not.
     */
    private boolean fShowVerticalTraceLine;

    /**
     * Creates an instance of <code>ULCJFreeChartComponent</code>.
     */
    public ULCJFreeChart() {
        setShowHorizontalTraceLine(false);
        setShowVerticalTraceLine(false);
    }

    /*
    * method to access the dispatcher (non-Javadoc)
    * @see com.ulcjava.base.application.ULCProxy#createDispatcher()
    */
    protected IDispatcher createDispatcher() {
        return new UIJFreeChartDispatcher();
    }

    protected class UIJFreeChartDispatcher extends ULCLabelDispatcher {
        public final UlcChartComponentEvent createUlcChartComponentEvent(Integer width, Integer height) {
            return ULCJFreeChart.this.createUlcChartComponentEvent(width, height);
        }

        public final UlcChartEvent createUlcChartEvent(double x, double y, double width, double height) {
            return ULCJFreeChart.this.createUlcChartEvent(x, y, width, height);
        }

        public final UlcChartEvent createUlcChartEvent(Integer chartEntityId) {
            return ULCJFreeChart.this.createUlcChartEvent(chartEntityId);
        }

        public final void processUlcChartComponentEvent(String eventCategory, String listenerMethodName, UlcChartComponentEvent event) {
            ULCJFreeChart.this.processUlcChartComponentEvent(eventCategory, listenerMethodName, event);
        }

        public final void processUlcChartEvent(String eventCategory, String listenerMethodName, UlcChartEvent event) {
            ULCJFreeChart.this.processUlcChartEvent(eventCategory, listenerMethodName, event);
        }

        public Class getUlcChartComponentListenerClass() {
            return IChartComponentListener.class;
        }

        public Class getUlcChartListenerClass() {
            return IChartEventListener.class;
        }
    }

    protected void processUlcChartComponentEvent(String eventCategory, String listenerMethodName, UlcChartComponentEvent event) {
        dispatchEvent(eventCategory, listenerMethodName, event);
    }

    protected UlcChartComponentEvent createUlcChartComponentEvent(Integer widht, Integer height) {
        return new UlcChartComponentEvent(this, widht, height);
    }

    protected UlcChartEvent createUlcChartEvent(double x, double y, double width, double height) {
        return new UlcChartEvent(this, (int) x, (int) y, (int) width, (int) height);
    }

    protected UlcChartEvent createUlcChartEvent(Integer chartEntityId) {
        if (chartEntityId != null) {
            ULCChartEntity entity = (ULCChartEntity) ULCSession.currentSession().getRegistry().find(chartEntityId.intValue());
            return new UlcChartEvent(this, entity);
        }
        return null;
    }

    protected void processUlcChartEvent(String eventCategory, String listenerMethodName, UlcChartEvent event) {
        Rectangle2D rectangle = new Rectangle2D.Double(event.getX(), event.getY(), event.getWidth(), event.getHeight());
        this.zoom(rectangle);
        dispatchEvent(eventCategory, listenerMethodName, event);
    }

    /**
     * Adds a <code>IChartComponentListener</code> to this component.
     *
     * @param listener the <code>IChartComponentListener</code> to add
     * @see <code>IChartComponentListener</code>
     */
    public void addChartComponentListener(IChartComponentListener listener) {
        addListener(ULCJFreeChartConstants.ULC_CHART_COMPONENT_EVENT, listener);
    }

    /**
     * Removes a <code>IChartComponentListener</code> from this component
     *
     * @param listener the <code>IChartComponentListener</code> to remove
     * @see <code>IChartComponentListener</code>
     */
    public void removeChartComponentListener(IChartComponentListener listener) {
        removeListener(ULCJFreeChartConstants.ULC_CHART_COMPONENT_EVENT, listener);
    }

    /**
     * Adds a <code>IChartEventListener</code> to this component.
     *
     * @param listener the <code>IChartEventListener</code> to add
     * @see <code>IChartEventListener</code>
     */
    public void addChartEventListener(IChartEventListener listener) {
        addListener(ULCJFreeChartConstants.ULC_CHART_EVENT, listener);
    }

    /**
     * Removes a <code>IChartEventListener</code> from this component.
     *
     * @param listener the <code>IChartEventListener</code> to remove
     * @see <code>IChartEventListener</code>
     */
    public void removeChartEventListener(IChartEventListener listener) {
        removeListener(ULCJFreeChartConstants.ULC_CHART_EVENT, listener);
    }

    /**
     * @return the chart
     */
    public JFreeChart getChart() {
        return fChart;
    }

    /**
     * @param chart the chart to set
     * @param size  the chart's size to set
     */
    public void setChart(JFreeChart chart, Dimension size) {
        fChartSize = size;
        fChart = chart;
        writeChartAsImage();
    }

    /**
     * @return the chart's size
     */
    public Dimension getChartSize() {
        return fChartSize;
    }

    /**
     * @return the chart rendering info
     */
    public ChartRenderingInfo getChartRenderingInfo() {
        return fChartRenderingInfo;
    }

    /**
     * @return true if horizontal trace line is shown
     */
    public boolean isShowHorizontalTraceLine() {
        return fShowHorizontalTraceLine;
    }

    /**
     * @param showHorizontalTraceLine true if horizontal trace line is to be shown
     */
    public void setShowHorizontalTraceLine(boolean showHorizontalTraceLine) {
        fShowHorizontalTraceLine = setStateUI("showHorizontalTraceLine", fShowHorizontalTraceLine, showHorizontalTraceLine);
    }

    /**
     * @return true if vertical trace line is shown
     */
    public boolean isShowVerticalTraceLine() {
        return fShowVerticalTraceLine;
    }

    /**
     * @param showVerticalTraceLine true if vertical trace line is to be shown
     */
    public void setShowVerticalTraceLine(boolean showVerticalTraceLine) {
        fShowVerticalTraceLine = setStateUI("showVerticalTraceLine", fShowVerticalTraceLine, showVerticalTraceLine);
    }

    public void chartDimensionUpdate(Dimension size) {
        fChartSize = size;
        writeChartAsImage();
    }

    /**
     * Creates a new PNG image from the current chart and sends it to the client.
     */
    private void writeChartAsImage() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            fChartRenderingInfo = new ChartRenderingInfo();
            ChartUtilities.writeChartAsPNG(bos, fChart, fChartSize.getWidth(), fChartSize.getHeight(), fChartRenderingInfo);
        } catch (IOException e) {
            // ignored
        }

        byte[] image = bos.toByteArray();

        upload(); // make sure this component is uploaded before sendUI
        setStateUI("icon", new ULCIcon(image));
        setStateUI("setChartEntities", getAnythingForChartEntities());
        setStateUI("setDataArea", getAnythingForDataArea());
        invokeUI("setChartRepainted");
    }

    private Map getAnythingForChartEntities() {
        Collection chartEntities = fChartRenderingInfo.getEntityCollection().getEntities();
        Map entitiesMap = new HashMap();
        fChartEntities = new ArrayList();

        int i = 0;

        for (Iterator iter = chartEntities.iterator(); iter.hasNext();) {
            ChartEntity element = (ChartEntity) iter.next();
            ULCChartEntity entity = new ULCChartEntity(element);
            fChartEntities.add(entity);
            entitiesMap.put(new Integer(i), entity);
            i++;
        }
        return entitiesMap;
    }

    private Map getAnythingForDataArea() {
        Rectangle2D dataArea = fChartRenderingInfo.getPlotInfo().getDataArea();
        Map dataAreaMap = new HashMap();
        dataAreaMap.put("x", new Double(dataArea.getX()));
        dataAreaMap.put("y", new Double(dataArea.getY()));
        dataAreaMap.put("width", new Double(dataArea.getWidth()));
        dataAreaMap.put("height", new Double(dataArea.getHeight()));
        return dataAreaMap;
    }

    protected String typeString() {
        return "com.canoo.ulc.community.jfreechart.client.UIJFreeChart";
    }

    protected String getPropertyPrefix() {
        return "Label";
    }

    /**
     * Zooms in on the vertical axis/axes.
     * <p/>
     * The new lower and upper bounds are specified as percentages of the current axis range, where 0 percent is the current lower bound and
     * 100 percent is the current upper bound.
     *
     * @param lowerPercent a percentage that determines the new lower bound for the axis (e.g. 0.20 is twenty percent).
     * @param upperPercent a percentage that determines the new upper bound for the axis (e.g. 0.80 is eighty percent).
     */
    public void zoomVerticalAxes(double lowerPercent, double upperPercent, Point2D source) {
        zoomVerticalAxes(lowerPercent, upperPercent, source, true);
    }

    /**
     * Zooms in on the horizontal axis/axes.
     * <p/>
     * The new lower and upper bounds are specified as percentages of the current axis range, where 0 percent is the current lower bound and
     * 100 percent is the current upper bound.
     *
     * @param lowerPercent a percentage that determines the new lower bound for the axis (e.g. 0.20 is twenty percent).
     * @param upperPercent a percentage that determines the new upper bound for the axis (e.g. 0.80 is eighty percent).
     */
    public void zoomHorizontalAxes(double lowerPercent, double upperPercent, Point2D source) {
        zoomHorizontalAxes(lowerPercent, upperPercent, source, true);
    }

    /**
     * Zooms in on a selected area.
     * <p/>
     * If the area is not positive (i.e. width or height less than zero) or <code>zoomRectangle</code> is <b>null</b>, then the chart is
     * zoomed out to its starting zoom state.
     *
     * @param zoomRectangle the selected area (<b>null</b> permitted)
     */
    public void zoom(Rectangle2D zoomRectangle) {
        Rectangle2D dataArea = fChartRenderingInfo.getPlotInfo().getDataArea();
        Point2D source = null;
        double lowerPercentVertical, upperPercentVertical, lowerPercentHorizontal, upperPercentHorizontal, height, width;
        if (zoomRectangle == null) {
            return;
        } else {
//            source = getRectangleCenter(zoomRectangle);
            source = new Point((int) zoomRectangle.getX(), (int) zoomRectangle.getY());

            upperPercentHorizontal = 1 - getPercent(zoomRectangle.getY(), dataArea.getY(), dataArea.getY() + dataArea.getHeight());
            lowerPercentHorizontal = 1 - getPercent(zoomRectangle.getY() + zoomRectangle.getHeight(), dataArea.getY(), dataArea.getY() + dataArea.getHeight());

            lowerPercentVertical = getPercent(zoomRectangle.getX(), dataArea.getX(), dataArea.getX() + dataArea.getWidth());
            upperPercentVertical = getPercent(zoomRectangle.getX() + zoomRectangle.getWidth(), dataArea.getX(), dataArea.getX() + dataArea.getWidth());

            height = zoomRectangle.getHeight();
            width = zoomRectangle.getWidth();

            if (width <= 0) {
                return;
            }
        }
        zoomVerticalAxes(lowerPercentVertical, upperPercentVertical, source, false); // the chart is not immediately redrawn
        zoomHorizontalAxes(lowerPercentHorizontal, upperPercentHorizontal, source, true);
    }

    protected Point2D getRectangleCenter(Rectangle2D rectangle) {
        int x = (int) ((double) rectangle.getX() + ((double) rectangle.getWidth() / 2.0));
        int y = (int) ((double) rectangle.getY() + ((double) rectangle.getHeight() / 2.0));
        return new Point(x, y);
    }

    protected double getPercent(double zoom, double lowerArea, double upperArea) {
        double part = zoom - lowerArea;
        double all = upperArea - lowerArea;
        return part / all;
    }

    /*
    * The parameter <code>writeChartAsImage</code> determines whether the chart should be redrawn and rewrited as image after the zooming
    * process.
    */
    private void zoomHorizontalAxes(double lowerPercent, double upperPercent, Point2D source, boolean writeChartAsImage) {
        Plot plot = fChart.getPlot();

        if(upperPercent<lowerPercent){
            double d = lowerPercent;
            lowerPercent = upperPercent;
            upperPercent = d;
        }

        if (plot instanceof CategoryPlot) {
//            ((CategoryPlot)plot).zoomHorizontalAxes(lowerPercent, upperPercent);
            ((CategoryPlot) plot).zoomRangeAxes(lowerPercent, upperPercent, fChartRenderingInfo.getPlotInfo(), source);
        } else if (plot instanceof XYPlot) {
//            ((XYPlot)plot).zoomHorizontalAxes(lowerPercent, upperPercent);
            ((XYPlot) plot).zoomRangeAxes(lowerPercent, upperPercent, fChartRenderingInfo.getPlotInfo(), source);
        }
        if (writeChartAsImage) {
            writeChartAsImage();
        }
    }

    /*
    * The parameter <code>writeChartAsImage</code> determines whether the chart should be redrawn and rewrited as image after the zooming
    * process.
    */
    private void zoomVerticalAxes(double lowerPercent, double upperPercent, Point2D source, boolean writeChartAsImage) {
        Plot plot = fChart.getPlot();

        if (plot instanceof CategoryPlot) {
            ((CategoryPlot) plot).zoomDomainAxes(lowerPercent, upperPercent, fChartRenderingInfo.getPlotInfo(), source);
        } else if (plot instanceof XYPlot) {
            ((XYPlot) plot).zoomDomainAxes(lowerPercent, upperPercent, fChartRenderingInfo.getPlotInfo(), source);
        }
        if (writeChartAsImage) {
            writeChartAsImage();
        }
    }
}