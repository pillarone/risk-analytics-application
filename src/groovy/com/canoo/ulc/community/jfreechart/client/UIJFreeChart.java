package com.canoo.ulc.community.jfreechart.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;

import com.canoo.ulc.community.jfreechart.shared.ULCJFreeChartConstants;
import com.ulcjava.base.client.IPendingRequestsOwner;
import com.ulcjava.base.client.UILabel;

public class UIJFreeChart extends UILabel {
    
    /**
     * <code>DEFAULT_ZOOM_TRIGGER</code> indicates the minimum pixel width and height that the zooming rectangle must have in order to
     * trigger the zoom event.
     */
    private static final int DEFAULT_ZOOM_TRIGGER = 1;
    
    private List fChartEntities;
    private Rectangle2D fDataArea;
    private Rectangle2D fZoomRectangle;
    private Line2D fHorizontalTraceLine;
    private Line2D fFixedHorizontalTraceLine;
    private Line2D fVerticalTraceLine;
    private Line2D fFixedVerticalTraceLine;
    private boolean fShowHorizontalTraceLine;
    private boolean fShowVerticalTraceLine;
    
    protected Object createBasicObject(Object[] arguments) {
        return new MyBasicLabel();
    }
    
    protected void postInitializeState() {
        super.postInitializeState();
        
        getBasicLabel().setVerticalAlignment(SwingConstants.TOP);
        getBasicLabel().setHorizontalAlignment(SwingConstants.LEFT);
        getBasicLabel().setIconTextGap(0);
        
        MouseEventHandler mouseEventHandler = new MouseEventHandler();
        getBasicLabel().addMouseListener(mouseEventHandler);
        getBasicLabel().addMouseMotionListener(mouseEventHandler);
        
        fZoomRectangle = new Rectangle2D.Double();
    }
    
    /**
     * <code>fSuspendTraceLineDrawing</code> determines that the trace line drawing process is suspended until the new image is completely
     * sent back from the server.
     */
    private boolean fSuspendTraceLineDrawing = false;
    
    private boolean isShowHorizontalTraceLine() {
        return fShowHorizontalTraceLine;
    }
    
    private boolean isShowVerticalTraceLine() {
        return fShowVerticalTraceLine;
    }
    
    private boolean isSuspendTraceLineDrawing() {
        return fSuspendTraceLineDrawing;
    }
    
    public void setChartRepainted() {
        fZoomRectangle = new Rectangle2D.Double();
        fVerticalTraceLine = null;
        fFixedVerticalTraceLine = null;
        fHorizontalTraceLine = null;
        fFixedHorizontalTraceLine = null;
        
        // reactivate the trace line drawing process
        fSuspendTraceLineDrawing = false;
    }
    
    public void setShowVerticalTraceLine(Boolean showVerticalTraceLine) {
        fShowVerticalTraceLine = showVerticalTraceLine.booleanValue();
    }
    
    public void setShowHorizontalTraceLine(Boolean showHorizontalTraceLine) {
        fShowHorizontalTraceLine = showHorizontalTraceLine.booleanValue();
    }
    
    public void setSetDataArea(Map args) {
        fDataArea = new Rectangle2D.Double(((java.lang.Double)args.get("x")).doubleValue(),
                ((java.lang.Double)args.get("y")).doubleValue(), ((java.lang.Double)args.get("width")).doubleValue(),
                ((java.lang.Double)args.get("height")).doubleValue());
    }
    
    public void setSetChartEntities(Map args) {
        fChartEntities = new ArrayList();
        for (int i = 0; i < args.size(); i++) {
            UIChartEntity chartEntity = (UIChartEntity)args.get(new Integer(i));
            fChartEntities.add(chartEntity);
        }
    }
    
    private class MouseEventHandler extends MouseInputAdapter {
        private int fX;
        
        private int fY;
        
        public void mouseMoved(MouseEvent e) {
            ((MyBasicLabel)getBasicLabel()).updateTraceLines(e.getX(), e.getY());
        }
        
        public void mousePressed(MouseEvent e) {
            fX = e.getX();
            fY = e.getY();
        }
        
        public void mouseDragged(MouseEvent e) {
            ((MyBasicLabel)getBasicLabel()).updateZoomRectangle(fX, fY, e.getX(), e.getY());
            ((MyBasicLabel)getBasicLabel()).fixTraceLines();
            ((MyBasicLabel)getBasicLabel()).setDrawZoomRectangle(true);
        }
        
        public void mouseReleased(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            ((MyBasicLabel)getBasicLabel()).setDrawZoomRectangle(false);
            
            if (isZoomEvent(x, y)) {
                Object[] args = new Object[4];
                
                args[0] = new Double(fZoomRectangle.getX());
                args[1] = new Double(fZoomRectangle.getY());
                args[2] = new Double(fZoomRectangle.getWidth());
                args[3] = new Double(fZoomRectangle.getHeight());
                fireEventULC(ULCJFreeChartConstants.ULC_CHART_EVENT, "chartZoomed", args);
                
                // suspend the trace line drawing process
                fSuspendTraceLineDrawing = true;
            } else {
                fFixedHorizontalTraceLine = null;
                fFixedVerticalTraceLine = null;
            }
        }
        
        public void mouseClicked(MouseEvent e) {
            // only execute actions in case of double-click
            if (e.getClickCount() == 2) {
                int x = e.getX();
                int y = e.getY();
                
                UIChartEntity entity = findEntity(x, y);
                
                if (!isZoomEvent(x, y)) {
                    Object[] args = new Object[1];
                    if (entity != null) {
                        args[0] = new Integer(entity.getId());
                    }
                    if (entity != null) {
                        fireEventULC(ULCJFreeChartConstants.ULC_CHART_EVENT, "chartEntityClicked", args);
                    } else {
                        fireEventULC(ULCJFreeChartConstants.ULC_CHART_EVENT, "chartClicked", args);
                    }
                }
            }
        }
        
        private UIChartEntity findEntity(int x, int y) {
            UIChartEntity result = null;
            
            for (Iterator iter = fChartEntities.iterator(); iter.hasNext();) {
                UIChartEntity entity = (UIChartEntity)iter.next();
                
                if (entity.getArea().contains(x, y)) {
                    result = entity;
                }
            }
            return result;
        }
        
        /**
         * Check if it is a zoom event or a non-zoom event. It is a zoom (in) event if the mouse spans a positive rectangle that is at least
         * <code>DEFAULT_ZOOM_TRIGGER</code> high and wide.
         * 
         * @param x the x coordinate
         * @param y the y coordinate
         * @return a <code>boolean</code> value indicating if the chart event is a zoom event or not.
         */
        private boolean isZoomEvent(int x, int y) {
            return !((fZoomRectangle.getWidth() >= 0 && fZoomRectangle.getWidth() < DEFAULT_ZOOM_TRIGGER) || (fZoomRectangle.getHeight() >= 0 && fZoomRectangle
                    .getHeight() < DEFAULT_ZOOM_TRIGGER));
        }
    }
    
    public class MyBasicLabel extends BasicLabel implements IPendingRequestsOwner {
        
        private int fStartX;
        private int fStartY;
        private int fX;
        private int fY;
        private boolean fDrawZoomRectangle;
        
        public void addPendingRequests() {
            if (getIcon() != null) {
                int iconWidth = getIcon().getIconWidth();
                int iconHeight = getIcon().getIconHeight();
                
                int labelWidth = getWidth();
                int labelHeight = getHeight();
                
                if (labelWidth != iconWidth || labelHeight != iconHeight) {
                    Object[] args = new Object[2];
                    args[0] = new Integer(labelWidth);
                    args[1] = new Integer(labelHeight);
                    fireEventULC(ULCJFreeChartConstants.ULC_CHART_COMPONENT_EVENT, "chartResized", args);
                }
            }
        }
        
        public void setDrawZoomRectangle(boolean drawZoomRectangle) {
            fDrawZoomRectangle = drawZoomRectangle;
        }
        
        public void updateTraceLines(int x, int y) {
            fX = x;
            fY = y;
            
            repaint();
        }
        
        public void fixTraceLines() {
            fFixedVerticalTraceLine = new Line2D.Double(Math.max(Math.min(fStartX, (int)fDataArea.getMaxX()), (int)fDataArea.getMinX()),
                    (int)fDataArea.getMinY(), Math.min(Math.max(fStartX, (int)fDataArea.getMinX()), (int)fDataArea.getMaxX()),
                    (int)fDataArea.getMaxY());
            fFixedHorizontalTraceLine = new Line2D.Double((int)fDataArea.getMinX(), Math.max(Math.min(fStartY, (int)fDataArea.getMaxY()),
                    (int)fDataArea.getMinY()), (int)fDataArea.getMaxX(), Math.min(Math.max(fStartY, (int)fDataArea.getMinY()),
                    (int)fDataArea.getMaxY()));
        }
        
        public void updateZoomRectangle(int startX, int startY, int x, int y) {
            fStartX = startX;
            fStartY = startY;
            fX = x;
            fY = y;
            
            repaint();
        }
        
        public void paint(Graphics g) {
            getSession().addPendingRequestsOwner(this);
            super.paint(g);
            
            if (!isSuspendTraceLineDrawing()) {
                if (isShowVerticalTraceLine()) {
                    drawVerticalAxisTrace(g, fX, fY);
                }
                if (isShowHorizontalTraceLine()) {
                    drawHorizontalAxisTrace(g, fX, fY);
                }
                if (fDrawZoomRectangle && (isShowVerticalTraceLine() || isShowHorizontalTraceLine())) {
                    drawZoomRectangle(g, fStartX, fStartY, fX, fY);
                }
            }
        }
        
        private void drawVerticalAxisTrace(Graphics g, int x, int y) {
            g.setColor(Color.blue);
            
            if (isMouseInDataArea(x, y)) {
                if (fVerticalTraceLine != null) {
                    fVerticalTraceLine.setLine(Math.max(x, (int)fDataArea.getMinX()), (int)fDataArea.getMinY(), Math.min(x, (int)fDataArea
                            .getMaxX()), (int)fDataArea.getMaxY());
                } else {
                    fVerticalTraceLine = new Line2D.Double(Math.max(x, (int)fDataArea.getMinX()), (int)fDataArea.getMinY(), Math.min(x,
                            (int)fDataArea.getMaxX()), (int)fDataArea.getMaxY());
                }
                
                ((Graphics2D)g).draw(fVerticalTraceLine);
                
                if (fFixedVerticalTraceLine != null) {
                    ((Graphics2D)g).draw(fFixedVerticalTraceLine);
                }
            }
        }
        
        private void drawHorizontalAxisTrace(Graphics g, int x, int y) {
            g.setColor(Color.blue);
            
            if (isMouseInDataArea(x, y)) {
                if (fHorizontalTraceLine != null) {
                    fHorizontalTraceLine.setLine((int)fDataArea.getMinX(), Math.max(y, (int)fDataArea.getMinY()), (int)fDataArea.getMaxX(),
                            Math.min(y, (int)fDataArea.getMaxY()));
                } else {
                    fHorizontalTraceLine = new Line2D.Double((int)fDataArea.getMinX(), Math.max(y, (int)fDataArea.getMinY()),
                            (int)fDataArea.getMaxX(), Math.min(y, (int)fDataArea.getMaxY()));
                }
                
                ((Graphics2D)g).draw(fHorizontalTraceLine);
                
                if (fFixedHorizontalTraceLine != null) {
                    ((Graphics2D)g).draw(fFixedHorizontalTraceLine);
                }
            }
        }
        
        private void drawZoomRectangle(Graphics g, int startX, int startY, int endX, int endY) {
            Color c = Color.lightGray;
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 255 / 2));
            
            if (isMouseInDataArea(endX, endY)) {
                startX = (int)Math.max(startX, fDataArea.getMinX());
                startY = (int)Math.max(startY, fDataArea.getMinY());
                if (isShowHorizontalTraceLine() && isShowVerticalTraceLine()) {
                    fZoomRectangle.setRect(startX, startY, endX - startX, endY - startY);
                } else if (isShowHorizontalTraceLine()) {
                    fZoomRectangle.setRect(fDataArea.getMinX(), startY, fDataArea.getMaxX() - fDataArea.getMinX(), endY - startY);
                } else if (isShowVerticalTraceLine()) {
                    fZoomRectangle.setRect(startX, fDataArea.getMinY(), endX - startX, fDataArea.getMaxY() - fDataArea.getMinY());
                }
            } else {
                fZoomRectangle.setRect(startX, startY, 0, 0);
                fFixedHorizontalTraceLine = null;
                fFixedVerticalTraceLine = null;
            }
            
            ((Graphics2D)g).fill(fZoomRectangle);
        }
        
        private boolean isMouseInDataArea(int x, int y) {
            return fDataArea.getMinX() < x && x < fDataArea.getMaxX() && fDataArea.getMinY() < y && y < fDataArea.getMaxY();
        }
    }
}