package org.pillarone.riskanalytics.application.client;

/*
 * Copyright © 2000-2007 Canoo Engineering AG, Switzerland.
 */

import com.ulcjava.base.client.UIFrame;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class UIMinimalSizeFrame extends UIFrame {
    private java.awt.Dimension fMinimumSize;

    protected void postInitializeState() {
        super.postInitializeState();
        getBasicComponent().addComponentListener(new MinimumSizeHandler());
    }

    public void setMinimumSize(java.awt.Dimension minimumSize) {
        fMinimumSize = minimumSize;
        enforceMinimumSize();
    }

    private void enforceMinimumSize() {
        if (fMinimumSize != null) {
            if (fMinimumSize.width > getBasicComponent().getWidth()
                    || fMinimumSize.height > getBasicComponent().getHeight()) {

                int width = Math.max(fMinimumSize.width,
                        getBasicComponent().getWidth());
                int height = Math.max(fMinimumSize.height,
                        getBasicComponent().getHeight());

                getBasicComponent().setSize(width, height);
            }
        }
    }

    private class MinimumSizeHandler extends ComponentAdapter {
        public void componentResized(ComponentEvent event) {
            enforceMinimumSize();
        }
    }
}

