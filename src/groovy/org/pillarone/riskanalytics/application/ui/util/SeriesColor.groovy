package org.pillarone.riskanalytics.application.ui.util

import java.awt.Color

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SeriesColor {

    static def seriesColorList = [
            new Color((int) 0xFF, 00, 00), new Color(00, (int) 0xFF, 00), new Color(00, 00, (int) 0xFF), Color.PINK, Color.ORANGE, Color.MAGENTA,
            Color.CYAN, new Color((int) 0xFF, (int) 0xFF, 00), new Color(184, 134, 11),
            new Color(00, 00, 00), new Color((int) 0x80, 00, 00), new Color(00, (int) 0x80, 00),
            new Color(00, 00, (int) 0x80), new Color((int) 0x80, (int) 0x80, 00), new Color((int) 0x80, 00, (int) 0x80),
            new Color((int) 0x00, (int) 0x80, (int) 0x80), new Color((int) 0x80, (int) 0x80, (int) 0x80), new Color((int) 0xC0, 00, 00),
            new Color(00, (int) 0xC0, 00), new Color(00, 00, (int) 0xC0), new Color((int) 0xC0, (int) 0xC0, 00),
            new Color((int) 0xC0, 00, (int) 0xC0), new Color((int) 00, (int) 0xC0, (int) 0xC0), new Color((int) 0xC0, (int) 0xC0, (int) 0xC0),
            new Color(47, 79, 79), new Color(25, 25, 112), new Color(00, 00, (int) 0x40),
            new Color(106, 90, 205), new Color(0, 0, 205), new Color(124, 252, 0),
            new Color((int) 0x40, (int) 0x40, (int) 0x40), new Color((int) 0x20, 00, 00), new Color(189, 183, 107),
            new Color(00, 00, (int) 0x20), new Color((int) 0x20, (int) 0x20, 00), new Color(238, 221, 130),
            new Color(00, 100, 0xFF), new Color(100, 120, (int) 0x20), new Color(244, 164, 96),
            new Color((int) 0x60, 00, 00), new Color((int) 00, 60, 00), new Color((int) 00, 00, (int) 0x60),
            new Color((int) 0x60, (int) 0x60, 00), new Color((int) 0x60, 00, (int) 0x60), new Color((int) 00, (int) 0x60, (int) 0x60),
            new Color((int) 0x60, (int) 0x60, (int) 0x60), new Color((int) 0xA0, 00, 00), new Color((int) 0x00, (int) 0xA0, 00),
            new Color((int) 00, 00, (int) 0xA0), new Color((int) 0xA0, (int) 0xA0, 00), new Color((int) 0xA0, 00, (int) 0xA0),
            new Color((int) 00, (int) 0xA0, (int) 0xA0), new Color((int) 0xA0, (int) 0xA0, (int) 0xA0), new Color((int) 0xE0, 00, 00),
            new Color((int) 00, (int) 0xE0, 00), new Color((int) 00, 00, (int) 0xE0), new Color((int) 0xE0, (int) 0xE0, 00),
            new Color((int) 0xE0, 00, (int) 0xE0), new Color(00, (int) 0xE0, (int) 0xE0), new Color((int) 0xE0, (int) 0xE0, (int) 0xE0)
    ];

    int periodCount

    def userChangedColors = [:]

    public SeriesColor(int periodCount) {
        this.periodCount = periodCount;
    }

    public Color getColor(int keyFigureIndex, int periodIndex) {
        int index = keyFigureIndex * periodCount + periodIndex
        if (index >= seriesColorList.size() && !userChangedColors[index]) {
            userChangedColors[index] = seriesColorList[index % seriesColorList.size()]
        }
        return userChangedColors[index] ? userChangedColors[index] : seriesColorList[index]
    }

    public void changeColor(int keyFigureIndex, int periodIndex, Color newColor) {
        userChangedColors[keyFigureIndex * periodCount + periodIndex] = newColor
    }

}
