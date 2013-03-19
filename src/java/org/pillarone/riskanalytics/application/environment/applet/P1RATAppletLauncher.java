package org.pillarone.riskanalytics.application.environment.applet;

import com.ulcjava.base.client.ClientEnvironmentAdapter;
import com.ulcjava.base.trusted.AllPermissionsBrowserService;
import com.ulcjava.environment.applet.client.DefaultAppletLauncher;
import org.pillarone.riskanalytics.application.client.AllPermissionsFileExtendedService;
import org.pillarone.riskanalytics.application.environment.shared.UIManagerHelper;

public class P1RATAppletLauncher extends DefaultAppletLauncher {
    public void init() {
        super.init();

        UIManagerHelper.setLookAndFeel();
        ClientEnvironmentAdapter.setFileService(new AllPermissionsFileExtendedService());
        ClientEnvironmentAdapter.setBrowserService(new AllPermissionsBrowserService());
    }
}
