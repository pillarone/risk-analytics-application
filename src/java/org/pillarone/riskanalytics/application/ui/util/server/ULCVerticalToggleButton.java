package org.pillarone.riskanalytics.application.ui.util.server;

import com.ulcjava.base.application.IAction;
import com.ulcjava.base.application.ULCToggleButton;

public class ULCVerticalToggleButton extends ULCToggleButton {
    private int fRotation = LEFT;

    public ULCVerticalToggleButton(IAction action) {
        super(action);
    }

    public void setRotation(int rotation) {
        if (rotation != LEFT && rotation != RIGHT) {
            throw new IllegalArgumentException("Only values LEFT or RIGHT are allowed for argument rotation!");
        }
        fRotation = setStateUI("rotation", fRotation, rotation);
    }

    protected void uploadStateUI() {
        super.uploadStateUI();
        setStateUI("rotation", LEFT, fRotation);
    }

    protected String typeString() {
        return "org.pillarone.riskanalytics.application.client.UIVerticalToggleButton";
    }
}
