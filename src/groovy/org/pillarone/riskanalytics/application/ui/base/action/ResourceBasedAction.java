package org.pillarone.riskanalytics.application.ui.base.action;

import com.ulcjava.base.application.ApplicationContext;
import com.ulcjava.base.application.util.ULCIcon;
import org.pillarone.riskanalytics.application.util.LocaleResources;
import org.pillarone.riskanalytics.application.ui.util.UIUtils;

import java.util.ResourceBundle;

/**
 * A ResourceBasedAction is an AbstractAction with icon, rollovericon, mnemonic and a tooltip.
 * The resources for the icons, the mnemonic key and the tooltip text are to be specified in the
 * resource bundle actionResources.properties. The given actionName acts as a prefix to the keys in the
 * resource bundle and has to be unique in the context all ResourceBasedActions.<br>
 * <br>
 * For each actionName the following entries have to be created in the bundle:
 * <ul>
 * <li>&lt;actionName&gt;.name<br>
 * <li>&lt;actionName&gt;.icon<br>
 * <li>&lt;actionName&gt;.icon.rollover<br>
 * <li>&lt;actionName&gt;.mnemonic<br>
 * <li>&lt;actionName&gt;.tootiptext<br>
 * </ul>
 * <p><b>Note:</b> The Mnemonic character hase to be in upper case format.</p>
 */
public abstract class ResourceBasedAction extends ExceptionSafeAction {

    public static final String ROLL_OVER_ICON = "ResourceBasedAction.rollOverIcon";

    private String fActionName;

    public ResourceBasedAction(String actionName) {
        fActionName = actionName;
        ResourceBundle bundle = getBundle();

        String name = bundle.getString(actionName + ".name");
        String icon = bundle.getString(actionName + ".icon");
        String rollOverIcon = bundle.getString(actionName + ".icon.rollover");
        Integer mnemonic = (int) bundle.getString(actionName + ".mnemonic").toUpperCase().charAt(0);
        String tooltipText = bundle.getString(actionName + ".tooltiptext");

        putValue(NAME, name);
        putValue(SMALL_ICON, createIcon(icon));
        putValue(SHORT_DESCRIPTION, tooltipText);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ROLL_OVER_ICON, createIcon(rollOverIcon));

    }

    public String getActionName() {
        return fActionName;
    }

    protected ULCIcon createIcon(String rollOverIcon) {
        return UIUtils.getIcon(rollOverIcon);
    }

    protected ResourceBundle getBundle() {
        return LocaleResources.getBundle("org.pillarone.riskanalytics.application.actionResources");
    }

    protected static ResourceBasedAction getAction(Class key) {
        ResourceBasedAction instance = (ResourceBasedAction) ApplicationContext.getAttribute(key.getName());
        if (instance == null) {
            try {
                instance = (ResourceBasedAction) key.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            putAction(key, instance);
        }


        return instance;
    }

    protected static void putAction(Class key, ResourceBasedAction instance) {
        ApplicationContext.setAttribute(key.getName(), instance);
    }

}
