package org.pillarone.riskanalytics.application.ui.util;

import com.ulcjava.base.client.IMessageService;

import javax.swing.*;

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SplashScreenHandler implements IMessageService {
    private SplashScreen fSplashScreen;


    public SplashScreenHandler(SplashScreen splashScreen) {
        fSplashScreen = splashScreen;
    }


    public SplashScreen getSplashScreen() {
        return fSplashScreen;
    }


    public void handleMessage(String msg) {
        if ("hideSplash".equals(msg)) {
            hideSplashScreen();
        }
    }


    public void showSplashScreen() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fSplashScreen.getSplashWindow().setVisible(true);
            }
        });
    }


    public void hideSplashScreen() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fSplashScreen.getSplashWindow().setVisible(false);
                fSplashScreen.getSplashWindow().dispose();
                disposeRootFrame();
            }
        });
    }


    /**
     * This method ensures that the SharedOwnerFrame gets disposed.
     * <p/>
     * Note: using ULC <= 5.2.1, this is needed in order for the application to shutdown properly.
     */
    private void disposeRootFrame() {
        JOptionPane.getRootFrame().dispose();
    }
}

