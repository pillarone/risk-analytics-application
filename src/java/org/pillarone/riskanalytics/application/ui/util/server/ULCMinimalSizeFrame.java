package org.pillarone.riskanalytics.application.ui.util.server;

/*
 * Copyright © 2000-2007 Canoo Engineering AG, Switzerland.
 */

import com.ulcjava.base.application.ULCFrame;
import com.ulcjava.base.application.util.Dimension;

public class ULCMinimalSizeFrame extends ULCFrame {
    private Dimension fMinimumSize;

    public ULCMinimalSizeFrame() {
        this(null);
    }

    public ULCMinimalSizeFrame(String title) {
        super(title);
        fMinimumSize = null;
    }

    public Dimension getMinimumSize() {
        return fMinimumSize;
    }

    public void setMinimumSize(Dimension minimumSize) {
        fMinimumSize = (Dimension) setStateUI("setMinimumSize", fMinimumSize, minimumSize);
    }

    protected void uploadStateUI() {
        super.uploadStateUI();
        setStateUI("minimumSize", null, fMinimumSize);
    }


    protected String typeString() {
        return "org.pillarone.riskanalytics.application.client.UIMinimalSizeFrame";
    }
}

    
