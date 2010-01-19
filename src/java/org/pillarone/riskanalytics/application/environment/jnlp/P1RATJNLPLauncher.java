package org.pillarone.riskanalytics.application.environment.jnlp;

import com.ulcjava.base.client.ClientEnvironmentAdapter;
import com.ulcjava.base.trusted.AllPermissionsFileService;
import com.ulcjava.environment.jnlp.client.DefaultJnlpLauncher;
import org.pillarone.riskanalytics.application.environment.shared.UIManagerHelper;

import java.net.MalformedURLException;

public class P1RATJNLPLauncher {
    public static void main(String[] args) throws MalformedURLException {
        UIManagerHelper.setLookAndFeel();
        DefaultJnlpLauncher.main(args);
        ClientEnvironmentAdapter.setFileService(new AllPermissionsFileService());
    }

}
