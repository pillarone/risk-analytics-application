package org.pillarone.riskanalytics.application.ui.comment.model

import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MapComboBoxModel extends DefaultComboBoxModel {

    private Map<String, String> content = new LinkedHashMap<String, String>()

    public MapComboBoxModel(List list) {
        list.each {String key ->
            content.put(UIUtils.getText(this.class, key, null), key)
        }
        content.keySet().each { addElement it }
    }

    String getSelectedObject() {
        content.get(getSelectedItem())
    }
}
