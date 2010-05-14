package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.table.AbstractTableModel

import org.apache.log4j.Logger
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*
import com.ulcjava.base.application.util.*
import org.pillarone.riskanalytics.core.util.PropertiesUtils

class AboutDialog {

    private static Logger sLogger = Logger.getLogger(AboutDialog.class)

    private ULCDialog dialog
    private ULCBoxPane mainContent
    private ULCWindow rootPane
    private ULCTabbedPane tabbedPane
    Closure closeAction = {event -> dialog.visible = false; dialog.dispose()}

    public AboutDialog(ULCWindow rootPane, Closure closeAction) {
        this.rootPane = rootPane
        this.closeAction = closeAction
        initComponents()
        layoutComponents()
    }

    private void initComponents() {
        dialog = new ULCDialog(rootPane, getText("title"), true)
        dialog.setLocationRelativeTo(rootPane)
        dialog.size = new Dimension(550, 500)

        tabbedPane = new ULCTabbedPane()
        mainContent = new ULCBoxPane(1, 2)
    }

    private void layoutComponents() {
        tabbedPane.addTab(getText("about"), createMainTab())
        tabbedPane.addTab(getText("license"), createLicenseTab())
        tabbedPane.addTab(getText("credits"), BorderedComponentUtilities.createBorderedComponent(createCreditsTab(), ULCBoxPane.BOX_EXPAND_EXPAND, BorderFactory.createEmptyBorder(5, 5, 5, 5)))
        tabbedPane.addTab(getText("usedLibraries"), createUsedLibrariesTab())
        tabbedPane.addTab(getText("sysProps"), createPropertiesTab())
        mainContent.add(tabbedPane, ULCBoxPane.BOX_EXPAND_EXPAND)
        ULCButton closeButton = new ULCButton(getText("close"))
        closeButton.addActionListener([actionPerformed: closeAction] as IActionListener)
        mainContent.add(closeButton, ULCBoxPane.BOX_CENTER_BOTTOM)
        dialog.setContentPane(mainContent)
    }

    private ULCComponent createMainTab() {
        ULCBoxPane pane = new ULCBoxPane(1, 6, 5, 5)
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))
        pane.add(new ULCLabel(UIUtils.getIcon("PillarOneLogoSmall.png")), ULCBoxPane.BOX_RIGHT_TOP)
        Properties infoProperties = new PropertiesUtils().getProperties("/version.properties")
        String appName = "RiskAnalytics ${infoProperties.getProperty("version", "N/A")}"
        ULCLabel appNameLabel = new ULCLabel(appName)
        appNameLabel.font = appNameLabel.font.deriveFont(Font.BOLD)
        pane.add(appNameLabel, ULCBoxPane.BOX_CENTER_TOP)
        String buildDate = "${infoProperties.getProperty("build.date", "N/A")}"
        pane.add(new ULCLabel(buildDate), ULCBoxPane.BOX_CENTER_TOP)
        String buildNo = "${getText("build#")}: ${infoProperties.getProperty("build.no", "N/A")}"
        pane.add(new ULCLabel(buildNo), ULCBoxPane.BOX_CENTER_TOP)

        String url = getText("url")
        ULCButton web = new ULCButton("<html><a href='${url}'>${url}</a></html>")
        web.borderPainted = false
        web.setForeground pane.getBackground()
        web.setBackground pane.getBackground()
        web.setCursor Cursor.HAND_CURSOR
        web.setOpaque false
        web.actionPerformed = {event ->
            if (!UIUtils.isUnixOs()) {
                ClientContext.showDocument("http://www.pillarone.org", "_new")
            }
        }
        pane.add(web, ULCBoxPane.BOX_CENTER_TOP)

        pane
    }

    private ULCComponent createCreditsTab() {
        ULCBoxPane pane = new ULCBoxPane(2, 0)
        pane.background = Color.white
        pane.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ULCLabel title = new ULCLabel(getText("contributors"))
        title.font = title.font.deriveFont(Font.BOLD, 14)
        pane.add(ULCBoxPane.BOX_LEFT_TOP, title); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCFiller(1, 10)); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Brendle Detlef, Canoo Engineering AG")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Cartier Sebastian, Intuitive Collaboration GmbH")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Dittrich Joerg, Munich Re Group")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Huber Matthias, Canoo Engineering AG")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Jaada Fouad, Intuitive Collaboration GmbH")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Koenig Dierk, Canoo Engineering AG")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Kunz Stefan, Intuitive Collaboration GmbH")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Kuschel Norbert, Munich Re Group")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Majidi Ali, Munich Re Group")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Melchior Martin, UAS Northwestern Switzerland")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Noe Michael, Munich Re Group")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Spahn Michael, Intuitive Collaboration GmbH")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Stricker Markus, Intuitive Collaboration GmbH")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        pane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Zumsteg Stefan, Intuitive Collaboration GmbH")); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())

        //new ULCScrollPane(pane)
        pane
    }

    private ULCComponent createLicenseTab() {
        ULCHtmlPane pane = new ULCHtmlPane()
        pane.text = new Scanner(getClass().getResource("/gpl3.html").openStream()).useDelimiter("\\Z").next()
        new ULCScrollPane(pane)
    }

    private ULCComponent createUsedLibrariesTab() {
        ULCBoxPane pane = new ULCBoxPane(2, 0)
        pane.background = Color.white
        pane.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        StringBuilder builder = new StringBuilder("<html><table><tr>");
        builder.append("<p><b>")
        builder.append(getText("usedLibraries"))
        builder.append("</b></p><br>")
        builder.append("<table>")
        builder.append("<tr><td align='left' >Java</td>")
        builder.append("<td align='left' ><a href=''>http://java.sun.com/</a></td> ")
        builder.append("</tr>")
        builder.append("<tr><td align='left' >Grails</td>")
        builder.append("<td align='left' ><a href='http://www.grails.org'>http://www.grails.org</a></td> ")
        builder.append("</tr>")
        builder.append("<tr><td align='left' >Groovy</td>")
        builder.append("<td align='left' ><a href=''>http://groovy.codehaus.org/</a></td> ")
        builder.append("</tr>")
        builder.append("<tr><td align='left' >Spring<</td>")
        builder.append("<td align='left' ><a href=''>http://www.springsource.org</a></td>")
        builder.append("</tr>")
        builder.append("<tr><td align='left' >Hibernate</td>")
        builder.append("<td align='left' ><a href=''>http://www.hibernate.org</a></td>")
        builder.append("</tr>")
        builder.append("<tr><td align='left' >JFreechart</td>")
        builder.append(" <td align='left' ><a href=''>http://www.jfree.org</a></td>")
        builder.append("</tr>")
        builder.append("<tr><td align='left' >JasperReports</td>")
        builder.append("<td align='left' ><a href=''>http://www.jasperforge.org</a></td>")
        builder.append(" </tr>")
        builder.append(" <tr><td align='left' >JodaTime</td>")
        builder.append(" <td align='left' ><a href=''>http://joda.sourceforge.net</a></td>")
        builder.append("</tr>")
        builder.append(" <tr><td align='left' >UltraLightClient (ULC)</td>")
        builder.append("<td align='left' ><a href=''>http://canoo.com/ulc/</a></td>")
        builder.append("</tr>")
        builder.append("<tr><td align='left' >Apache Commons Math,<br> Logging</td>")
        builder.append("   <td align='left' ><a href=''>http://commons.apache.org/</a></td>")
        builder.append("</tr>")
        builder.append("<tr><td align='left' >POI</td>")
        builder.append("   <td align='left' ><a href=''>http://poi.apache.org</a></td>")
        builder.append("</tr>")
        builder.append("<tr><td align='left' >SSJ</td>")
        builder.append("   <td align='left' ><a href=''>http://www.iro.umontreal.ca/~simardr/ssj</a></td>")
        builder.append("</tr>")
        builder.append("<tr><td align='left' >COLT</td>")
        builder.append("   <td align='left' ><a href=''>http://acs.lbl.gov/~hoschek/colt</a></td>")
        builder.append("</tr>")


        ULCLabel html = new ULCLabel(builder.toString())
        //title.font = title.font.deriveFont(Font.BOLD, 14)
        pane.add(ULCBoxPane.BOX_LEFT_TOP, html); pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())

        pane
    }


    private ULCComponent createPropertiesTab() {
        Map props = System.properties
        int propCount = props.keySet().size()
        Object[][] model = new Object[propCount][propCount]
        props.entrySet().eachWithIndex {Map.Entry it, int index ->
            model[index][0] = it.key
            model[index][1] = it.value
        }
        ULCTable table = new ULCTable(new PropertiesTableModel(model))
        table.tableHeader = null
        new ULCScrollPane(table)
    }


    public void setVisible(boolean visible) {
        dialog.visible = visible
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("AboutDialog." + key);
    }
}

class PropertiesTableModel extends AbstractTableModel {

    Object[][] array

    public PropertiesTableModel(Object[][] array) {
        super()
        this.@array = array
    }

    public int getRowCount() {
        array.length
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        array[rowIndex][columnIndex]
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        false
    }


}
