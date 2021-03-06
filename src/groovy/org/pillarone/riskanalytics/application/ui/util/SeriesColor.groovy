package org.pillarone.riskanalytics.application.ui.util

import groovy.transform.CompileStatic

import java.awt.Color

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class SeriesColor {

    public static List<Color> seriesColorList = [new Color(159, 4, 4), new Color(0, 131, 52), new Color(25, 0, 211), new Color(131, 86, 0),
            new Color(127, 194, 47), new Color(135, 95, 190), new Color(86, 91, 0), new Color(0, 93, 131),
            new Color(64, 41, 84), new Color(0, 131, 98), new Color(0, 51, 10), new Color(0, 28, 91), new Color(131, 131, 0),
            new Color(211, 34, 38), new Color(17, 170, 138), new Color(92, 124, 218), new Color(205, 51, 0), new Color(63, 65, 51)
    ];

    int periodCount

    Map<Number, Color> userChangedColors = [:]

    public SeriesColor(int periodCount) {
        this.periodCount = periodCount;
    }

    public Color getColor(int keyFigureIndex, int periodIndex) {
        Color color = (keyFigureIndex >= seriesColorList.size()) ? seriesColorList[keyFigureIndex % seriesColorList.size()] : seriesColorList[keyFigureIndex]
        int index = (keyFigureIndex * periodCount + periodIndex).intValue()
        float red = color.getRed() / 255
        float green = color.getGreen() / 255
        float blue = color.getBlue() / 255
        float alpha = 1.0f - (periodIndex + 1) * (1 / (periodCount + 1))
        return userChangedColors[index] ? userChangedColors[index] :
            new Color(red, green, blue, alpha)
    }

    public Color getColorByParam(int index) {
        Color color = (index >= seriesColorList.size()) ? seriesColorList[index % seriesColorList.size()] : seriesColorList[index]
        return userChangedColors[index] ? userChangedColors[index] : color
    }


    public void changeColor(int keyFigureIndex, int periodIndex, Color newColor) {
        if (keyFigureIndex == -1)
            changeColor(periodIndex, newColor)
        else
            userChangedColors[keyFigureIndex * periodCount + periodIndex] = newColor
    }

    public void changeColor(int index, Color newColor) {
        userChangedColors[index] = newColor
    }


}
