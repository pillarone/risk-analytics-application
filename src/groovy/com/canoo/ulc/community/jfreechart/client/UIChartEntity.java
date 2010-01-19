package com.canoo.ulc.community.jfreechart.client;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import com.ulcjava.base.client.UIProxy;

public class UIChartEntity extends UIProxy {
    
    private Shape fArea;
    
    private String fToolTipText;
    
    public UIChartEntity() {
        super();
    }
    
    public Shape getArea() {
        return fArea;
    }
    
    public String getToolTipText() {
        return fToolTipText;
    }
    
    public void setChartEntity(HashMap map) {
        fToolTipText = (String)map.get("toolTipText");
        setArea(map);
    }
    
    private void setArea(Map map) {
        int[] xCoords = (int[])map.get("xCoords");
        int[] yCoords = (int[])map.get("yCoords");
        
        if (map.get("shapeType").equals("RECT")) {
            int width = xCoords[1] - xCoords[0];
            int height = yCoords[1] - yCoords[0];
            fArea = new Rectangle2D.Double(xCoords[0], yCoords[0], width, height);
        } else if (map.get("shapeType").equals("POLY")) {
            int nPoints = xCoords.length;
            fArea = new Polygon(xCoords, yCoords, nPoints);
        }
    }
}
