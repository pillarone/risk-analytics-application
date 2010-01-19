package org.pillarone.riskanalytics.application.client;


import com.ulcjava.base.client.UIToggleButton;


public class UIVerticalToggleButton extends UIToggleButton {

    protected Object createBasicObject(Object[] arguments) {
        return new JVerticalToggleButton();
    }

    public JVerticalToggleButton getBasicVerticalToggleButton() {
        return (JVerticalToggleButton) getBasicObject();
    }

    public void setRotation(int rotation) {
        getBasicVerticalToggleButton().setRotation(rotation);
    }
}
