package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.parameterization.model.EnumParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationClassifierTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.base.application.*

import org.pillarone.riskanalytics.core.user.PersonAuthority
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType

class UIUtils {

    private static Log LOG = LogFactory.getLog(UIUtils)

    private static FontMetrics sFontMetrics
    private static Font font
    public static final String ICON_DIRECTORY = "/org/pillarone/riskanalytics/application/icons/"
    public static final String ROOT_PANE = "rootPane"

    static int calculateTreeWidth(node) {
        return calculateTreeWidth(node, 0)
    }

    static int calculateColumnWidth(node, int columnIndex) {
        return calculateColumnWidth(node, 0, 0, columnIndex)
    }

    static NumberParser getNumberParser() {
        return new NumberParser(ClientContext.locale)
    }


    private static FontMetrics getFontMetrics() {
        if (sFontMetrics == null) {
            BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_BGR)
            Graphics graphics = bufferedImage.getGraphics()
            sFontMetrics = graphics.getFontMetrics()
        }

        return sFontMetrics
    }

    public static Font getFont() {
        if (font == null) {
            font = new Font("SansSerif", Font.PLAIN, 12);
        }
        return font
    }

    private static int calculateTreeWidth(node, int columnIndex) {
        return calculateColumnWidth(node, 25, 25, columnIndex)
    }

    protected static int calculateColumnWidth(MultiDimensionalParameterizationTableTreeNode node, initialOffset, offset, int columnIndex) {
        return 0 // this node should not rule for the columnWidth, therefor we return 0
    }

    protected static int calculateColumnWidth(EnumParameterizationTableTreeNode node, initialOffset, offset, int columnIndex) {
        return determineColumnWidthForValues(node)
    }

    protected static int calculateColumnWidth(ParameterizationClassifierTableTreeNode node, initialOffset, offset, int columnIndex) {
        return determineColumnWidthForValues(node)
    }

    protected static int calculateColumnWidth(node, initialOffset, offset, int columnIndex) {
        def nodeMax = 0

        (0..<node.childCount).each {
            def childMax = calculateColumnWidth(node.getChildAt(it), initialOffset, initialOffset + offset, columnIndex)
            nodeMax = Math.max(childMax, nodeMax)
        }

        int columnWidth = 0

        def value = node.getValueAt(columnIndex)

        if (value instanceof List) {
            columnWidth = 0
        } else {
            columnWidth = getFontMetrics().stringWidth(value.toString()) + offset
        }
        return Math.max(columnWidth, nodeMax)
    }

    private static int determineColumnWidthForValues(node) {
        int columnWidth = 0
        node.values.each {
            int enumMax = getFontMetrics().stringWidth(it.toString())
            columnWidth = Math.max(columnWidth, enumMax)
        }
        return columnWidth
    }

    public static ULCIcon getIcon(String fileName) {
        URL url = new UIUtils().class.getResource(ICON_DIRECTORY + fileName)
        if (url) {
            return new ULCIcon(url)
        }
    }

    public static ImageIcon getImageIcon(String fileName) {
        URL url = new UIUtils().class.getResource(ICON_DIRECTORY + fileName)
        if (url) {
            return new ImageIcon(url)
        }
    }

    public static String getConfigProperty(String property) {
        try {
            return ApplicationHolder.getApplication().getConfig().getProperty(property)
        } catch (Exception ex) {
            return ""
        }
    }


    public static ULCBoxPane spaceAround(ULCComponent comp, int top, int left, int bottom, int right, String alignment = null) {
        ULCBoxPane deco = new ULCBoxPane()
        deco.border = BorderFactory.createEmptyBorder(top, left, bottom, right)
        (alignment) ? deco.add(alignment, comp) : deco.add(comp)
        return deco
    }

    public static boolean isUnixOs() {
        String osName = ClientContext.getSystemProperty("os.name").toUpperCase()
        return "UNIX" == osName || "LINUX" == osName || "SOLARIS" == osName
    }

    public static Locale getClientLocale() {
        Locale locale = Locale.default

        try {
            locale = LocaleResources.getLocale()
        } catch (Exception e) {
            LOG.warn "Unable to detect client locale. Using default."
        }
        return locale
    }

    public static final String getText(Class objClass, String key, List argsValue = null) {
        String str = null
        try {
            str = LocaleResources.getString(objClass.simpleName + "." + key)
            if (argsValue) {
                argsValue.eachWithIndex {String value, int index ->
                    str = str.replace("[${index}]", value)
                }
            }
        } catch (Exception ex) {}
        return str ? str : key;
    }

    public static com.ulcjava.base.application.util.Color toULCColor(java.awt.Color color) {
        return new com.ulcjava.base.application.util.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static java.awt.Color toAwtColor(com.ulcjava.base.application.util.Color color) {
        return new java.awt.Color((color.getRed() / 255).floatValue(), (color.getGreen() / 255).floatValue(), (color.getBlue() / 255).floatValue(), (color.getAlpha() / 255).floatValue());
    }


    public static com.ulcjava.base.application.util.Color getFontColor(com.ulcjava.base.application.util.Color color) {
        if (color.alpha < 150)
            return Color.black
        java.awt.Color c = toAwtColor(color);
        //calculate brightness
        return Math.round((0.212671F * c.getRed()) + (0.715160F * c.getGreen()) + (0.072169F * c.getBlue())) > 100 ? Color.black : Color.white;
    }

    public static ULCBoxPane boxLayout(String title, Closure body) {
        ULCBoxPane result = new ULCBoxPane()
        result.border = BorderFactory.createTitledBorder(" $title ")
        ULCBoxPane inner = new ULCBoxPane()
        body(inner)
        result.add ULCBoxPane.BOX_EXPAND_EXPAND, spaceAround(inner, 0, 5, 5, 5, ULCBoxPane.BOX_EXPAND_EXPAND)
        return result
    }

    public static ULCRootPane getRootPane() {
        return UserContext.getAttribute(ROOT_PANE)
    }

    public static void setRootPane(ULCRootPane pane) {
        UserContext.setAttribute(ROOT_PANE, pane)
    }

    public static String getUserInfo() {
        if (UserManagement.isLoggedIn()) {
            Person loggedUser = null
            String userAuthorities = ""
            Person.withTransaction {def status ->
                loggedUser = UserContext.getCurrentUser()
                List authorities = (PersonAuthority.findAllByPerson(loggedUser).collect { it.authority } as Set)*.authority
                List i18nAuthorities = authorities.collect {UIUtils.getText(PersonAuthority.class, it)}
                userAuthorities = i18nAuthorities.join(", ")
            }
            return loggedUser.username + " " + userAuthorities
        }
        return ""
    }

    public static Color getColor(ValidationType validationType) {
        switch (validationType) {
            case ValidationType.ERROR: return Color.red
            case ValidationType.WARNING: return Color.darkGray
            case ValidationType.HINT: return Color.blue
            default: return Color.black
        }
    }

}