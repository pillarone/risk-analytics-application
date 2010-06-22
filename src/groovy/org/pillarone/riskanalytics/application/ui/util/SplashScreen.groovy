package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.application.ui.util.SplashScreen
import java.awt.*
import javax.swing.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SplashScreen {
    private static final String BUNDLE_FILENAME = "org.pillarone.riskanalytics.application.clientResources";
    private static final String WAIT_ICON_FILENAME = "/org/pillarone/riskanalytics/application/icons/pillarone-splashscreen.png";

    private JWindow fSplashWindow;
    private volatile JLabel fWaitLabel;
    private JLabel fIconLabel;


    public SplashScreen() {
        initWidgets();
        layoutComponents();
    }


    private void initWidgets() {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_FILENAME);

        fSplashWindow = new JWindow();
        fSplashWindow.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        fSplashWindow.setLocation(getCenteredCoordinates());

        fIconLabel = new JLabel(new ImageIcon(SplashScreen.class.getResource(WAIT_ICON_FILENAME)));
        fWaitLabel = new JLabel(bundle.getString("loading"))
    }


    private Point getCenteredCoordinates() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double centerX = screenSize.getWidth() / 2;
        double centerY = screenSize.getHeight() / 2;
        double positionX = (centerX - 250);
        double positionY = (centerY - 150);
        return new Point(((int) positionX), ((int) positionY));
    }


    private void layoutComponents() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        contentPane.setPreferredSize new Dimension(498, 330)

        JPanel msgPanel = new JPanel(new BorderLayout());
        msgPanel.setBackground(new Color(212, 209, 202));// Color(0xdb, 0xdb, 0xdb));
        msgPanel.setPreferredSize new Dimension(500, 40)
        fWaitLabel.setHorizontalAlignment(JLabel.CENTER);
        fWaitLabel.setForeground(Color.black);
        msgPanel.add(BorderLayout.CENTER, fWaitLabel);

        fIconLabel.setHorizontalAlignment(JLabel.CENTER);
        fIconLabel.setVerticalAlignment(JLabel.BOTTOM);

        contentPane.add(BorderLayout.CENTER, fIconLabel);
        contentPane.add(BorderLayout.SOUTH, msgPanel);//fWaitLabel);

        fSplashWindow.getContentPane().add(contentPane);
        fSplashWindow.pack();
    }


    public JWindow getSplashWindow() {
        return fSplashWindow;
    }


    public JLabel getWaitLabel() {
        return fWaitLabel;
    }

    public void update(String newlabel) {
        println newlabel
        fWaitLabel.setText newlabel
        fWaitLabel.repaint()
        fSplashWindow.getContentPane().repaint()
    }


    public static void main(String[] args) {
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.getSplashWindow().setVisible(true);
    }
}
