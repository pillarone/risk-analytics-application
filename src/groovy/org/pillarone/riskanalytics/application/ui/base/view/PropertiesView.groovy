package org.pillarone.riskanalytics.application.ui.base.view

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import org.pillarone.riskanalytics.application.ui.base.model.PropertiesViewModel
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.LocaleResources

class PropertiesView {

    ULCBoxPane content
    PropertiesViewModel model
    ULCLabel lastModifierInfo
    int MAX_CHARS = 255

    public PropertiesView(PropertiesViewModel model) {
        this.@model = model
        initComponents()
    }

    private initComponents() {
        ULCBoxPane props = boxLayout(getText('settings')) { ULCBoxPane box ->

            ULCBoxPane content = new ULCBoxPane(2, 0)
            def textArea = new ULCTextArea(4, 50)
            textArea.name = 'textArea'
            textArea.text = model.comment
            textArea.lineWrap = true
            textArea.wrapStyleWord = true
            textArea.addValueChangedListener([valueChanged: { ValueChangedEvent event ->
                String text = textArea.text
                if (text && text.length() > MAX_CHARS) {
                    I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(content), "CommentTooLong")
                    alert.show()
                } else
                    model.comment = text
            }] as IValueChangedListener)
            content.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(new ULCLabel(getText('comment')), 5, 10, 0, 0))
            content.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(textArea, 5, 10, 0, 0))
            box.add(content)
        }

        ULCBoxPane holder = new ULCBoxPane(2, 1)
        holder.add(ULCBoxPane.BOX_EXPAND_TOP, props)
        holder.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())

        content = new ULCBoxPane(true)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, holder)

        if (model.item.creator != null) {
            content.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCLabel(getCreatorInfo()))
        }
        if (model.item.lastUpdater != null) {
            lastModifierInfo = new ULCLabel(getModificatorInfo())
            content.add(ULCBoxPane.BOX_EXPAND_TOP, lastModifierInfo)
        }
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }

    private String getCreatorInfo() {
        String creator = model.item.creator.username
        String date = DateFormatUtils.formatDetailed(model.item.creationDate)
        return getTextByKeys("creator", date, creator)
    }

    private String getModificatorInfo() {
        String changer = model.item.lastUpdater.username
        String date = DateFormatUtils.formatDetailed(model.item.modificationDate)
        return getTextByKeys("modificator", date, changer)
    }

    private ULCBoxPane spaceAround(ULCComponent comp, int top, int left, int bottom, int right) {
        ULCBoxPane deco = new ULCBoxPane()
        deco.border = BorderFactory.createEmptyBorder(top, left, bottom, right)
        deco.add comp
        return deco
    }

    private ULCBoxPane boxLayout(String title, Closure body) {
        ULCBoxPane result = new ULCBoxPane()
        result.border = BorderFactory.createTitledBorder(" $title ")
        ULCBoxPane inner = new ULCBoxPane()
        body(inner)
        result.add spaceAround(inner, 0, 5, 5, 5)
        result.add ULCBoxPane.BOX_EXPAND_CENTER, new ULCFiller()
        return result
    }

    protected String getTextByKeys(String key, String key1, String key2) {
        String text = LocaleResources.getString("PropertiesView." + key + ".args")
        text = text.replace("[0]", key1)
        text = text.replace("[1]", key2)
        return text
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("PropertiesView." + key);
    }

}
