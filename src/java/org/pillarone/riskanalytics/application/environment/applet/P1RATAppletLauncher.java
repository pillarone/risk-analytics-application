package org.pillarone.riskanalytics.application.environment.applet;

import com.ulcjava.base.client.ClientEnvironmentAdapter;
import com.ulcjava.base.trusted.AllPermissionsBrowserService;
import com.ulcjava.environment.applet.client.DefaultAppletLauncher;
import org.pillarone.riskanalytics.application.client.AllPermissionsFileExtendedService;
import org.pillarone.riskanalytics.application.environment.shared.UIManagerHelper;
import org.pillarone.riskanalytics.application.ui.util.SplashScreen;
import org.pillarone.riskanalytics.application.ui.util.SplashScreenHandler;

public class P1RATAppletLauncher extends DefaultAppletLauncher {
    public void init() {
        SplashScreenHandler splashScreenHandler = new SplashScreenHandler(new SplashScreen());
        ClientEnvironmentAdapter.setMessageService(splashScreenHandler);
        splashScreenHandler.showSplashScreen();

        super.init();

        UIManagerHelper.setLookAndFeel();
        ClientEnvironmentAdapter.setFileService(new AllPermissionsFileExtendedService());
        ClientEnvironmentAdapter.setBrowserService(new AllPermissionsBrowserService());
    }
}
