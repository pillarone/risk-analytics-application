package org.pillarone.riskanalytics.application.environment.shared;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import org.pillarone.riskanalytics.application.client.MetalTextFieldUI;
import org.pillarone.riskanalytics.application.client.WindowsTextFieldUI;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.html.parser.ParserDelegator;

//import apple.laf.CUIAquaLookAndFeel;
//import apple.laf.CUIAquaTextField;

public class UIManagerHelper {

    public static void setLookAndFeel() {
        if (isWindowsOS()) {
            setWindowsLookAndFeel();
        } else if (isLinux()) {
            setLinuxLookAndFeel();
        } //else if (isMacOS()) {
          //  setMacLookAndFeel();
        //}
        else setSystemLookAndFeel();

        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

        //workaround for http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6993073
        new ParserDelegator();
    }


    private static boolean isWindowsOS() {
        return getOS().indexOf("windows") > -1;
    }
    private static boolean isMacOS() {
        return getOS().startsWith("mac");
    }

    private static String getOS() {
        return System.getProperty("os.name").toLowerCase();
    }


    private static boolean isLinux() {
        return getOS().indexOf("linux") > -1;
    }


    private static void setLookAndFeel(LookAndFeel lookAndFeel) {
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* disabled temporarily to make it run on hudson where there are not Aqua classes.... (dk)
    public static void setMacLookAndFeel() {
        setLookAndFeel(new CUIAquaLookAndFeel());
        UIManager.put("TextFieldUI", CUIAquaTextField.class.getName());
    }
    */

    private static void setWindowsLookAndFeel() {
        setLookAndFeel(new WindowsLookAndFeel());
        UIManager.put("TextFieldUI", WindowsTextFieldUI.class.getName());

    }

    public static void setLinuxLookAndFeel() {
        setLookAndFeel(new MetalLookAndFeel());
        UIManager.put("TextFieldUI", MetalTextFieldUI.class.getName());
    }
}
