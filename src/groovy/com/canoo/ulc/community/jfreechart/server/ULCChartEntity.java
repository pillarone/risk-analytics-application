package com.canoo.ulc.community.jfreechart.server;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.entity.ChartEntity;

import com.ulcjava.base.application.ULCProxy;

/**
 * <code>ULCChartEntity</code> represents an item or legend entity in a chart. This class includes a <code>ChartEntity</code> object and
 * thus provides all functionalities that come along with <code>ChartEntity</code>.
 * 
 * @author marc.hermann@canoo.com
 * @see <code>ChartEntity</code>
 */
public class ULCChartEntity extends ULCProxy {
    
    /**
     * <code>fEntity</code> represents the <code>ChartEntity> object.
     */
    private ChartEntity fEntity;
    
    /**
     * <code>fXCoords</code> stores the x coordinates of an entity.
     */
    private int[] fXCoords;
    
    /**
     * <code>fYCoords</code> stores the y coordinates of an entity.
     */
    private int[] fYCoords;
    
    /**
     * Creates an instance of <code>ULCChartEntity</code> configured with the provided chart entity.
     * 
     * @param entity the chart entity
     */
    public ULCChartEntity(ChartEntity entity) {
        super();
        fEntity = entity;
        initCoords();
    }
    
    /**
     * @return the chart entity
     */
    public ChartEntity getChartEntity() {
        return fEntity;
    }
    
    private void initCoords() {
        if (fEntity.getShapeType().equals("RECT")) {
            initRectCoords();
        } else {
            initPolyCoords();
        }
    }
    
    private void initRectCoords() {
        Rectangle2D rectangle = (Rectangle2D)fEntity.getArea();
        if (rectangle == null) {
            throw new IllegalArgumentException("Null 'rectangle' argument.");
        }
        int x1 = (int)rectangle.getX();
        int y1 = (int)rectangle.getY();
        
        int x2 = x1 + (int)rectangle.getWidth();
        int y2 = y1 + (int)rectangle.getHeight();
        
        // correct rounding errors / ensure that there always is an area
        if (x2 == x1) {
            x2++;
        }
        if (y2 == y1) {
            y2++;
        }
        
        fXCoords = new int[] {x1, x2};
        fYCoords = new int[] {y1, y2};
    }
    
    private void initPolyCoords() {
        Shape shape = fEntity.getArea();
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        
        float[] coords = new float[6];
        PathIterator pi = shape.getPathIterator(null, 1.0);
        
        List xCoords = new ArrayList();
        List yCoords = new ArrayList();
        
        while (!pi.isDone()) {
            pi.currentSegment(coords);
            xCoords.add(new Integer((int)coords[0]));
            yCoords.add(new Integer((int)coords[1]));
            pi.next();
        }
        
        fXCoords = new int[xCoords.size()];
        fYCoords = new int[yCoords.size()];
        
        for (int i = 0; i < fXCoords.length; i++) {
            fXCoords[i] = ((Integer)xCoords.get(i)).intValue();
            fYCoords[i] = ((Integer)yCoords.get(i)).intValue();
        }
    }
    
    protected void uploadStateUI() {
        Map map = new HashMap();
        
        map.put("shapeType", fEntity.getShapeType());
        map.put("toolTipText", fEntity.getToolTipText());
        map.put("xCoords", fXCoords);
        map.put("yCoords", fYCoords);
        
        setStateUI("chartEntity", map);
    }
    
    protected String typeString() {
        return "com.canoo.ulc.community.jfreechart.client.UIChartEntity";
    }
}
