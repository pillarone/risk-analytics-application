package org.pillarone.riskanalytics.application.ui

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.AbstractApplication
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.main.action.ExitAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.P1RATMainView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.ulc.server.ULCMinimalSizeFrame
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry

class P1RATApplication extends AbstractApplication {

    ULCMinimalSizeFrame mainFrame = new ULCMinimalSizeFrame("Risk Analytics")
    P1RATModel p1RATModel
    public static boolean CLOSE_WINDOW = false

    public void start() {
        ClientContext.sendMessage("hideSplash");
        if (UserContext.isStandAlone()) {
            try {
                UserContext.setUserTimeZone(TimeZone.getTimeZone(System.getProperty("user.timezone")))
            } catch (Exception e) {
                UserContext.setUserTimeZone(TimeZone.getTimeZone("UTC"))
            }
        }

        initMainView()
    }

    public void initMainView() {
        //init p1ratModel after login
        p1RATModel = new P1RATModel()
        mainFrame.defaultCloseOperation = ULCFrame.DO_NOTHING_ON_CLOSE
        mainFrame.size = new Dimension(1000, 750)
        mainFrame.minimumSize = new Dimension(800, 600)

        //If argument is null, the window is centered on the screen.
        mainFrame.locationRelativeTo = null
        mainFrame.setIconImage(UIUtils.getIcon("application.png"))
        ULCClipboard.install()
        P1RATMainView mainView = new P1RATMainView(p1RATModel)
        mainFrame.contentPane.add(mainView.content)
        mainFrame.menuBar = mainView.menuBar
        UIUtils.setRootPane(mainFrame)
        mainFrame.visible = true
        mainFrame.toFront()
        mainFrame.addWindowListener([windowClosing: {WindowEvent e -> mainFrame.visible = false; handleEvent(e)}] as IWindowListener)

        ModelRegistry.instance.addListener(p1RATModel)
    }

    private void handleEvent(WindowEvent e) {
        ModelRegistry.instance.removeListener(p1RATModel)
        ExitAction.terminate()
    }


}
