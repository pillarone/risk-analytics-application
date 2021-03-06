package org.pillarone.riskanalytics.application.ui.base.model;

import com.ulcjava.base.application.AbstractListModel;
import com.ulcjava.base.application.IComboBoxModel;
import groovy.transform.CompileStatic;
import org.pillarone.riskanalytics.application.util.LocaleResources;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class accepts enum classes and provides their sorted i18n values in the model.
 *
 * @author stefan.kunz@intuitive-collaboration.com
 */
@CompileStatic
public class EnumI18NComboBoxModel extends AbstractListModel implements IComboBoxModel {

    /**
     * contains the localized representation
     */
    private Object fSelectedObject;
    private Class fEnumeration;
    private Map<String, Object> fLocalizationMap = new TreeMap<String, Object>();

    public EnumI18NComboBoxModel(Object[] values) {
        fEnumeration = values[0].getClass();
        assert fEnumeration.isEnum();
        for (Object key : values) {
            String localized = getLocalizedText(key.toString());
            fLocalizationMap.put(localized, key);
        }
    }

    public EnumI18NComboBoxModel(Object[] values, Object selected) {
        this(values);
        setSelectedEnum(selected);
    }

    public EnumI18NComboBoxModel(Object[] values, Object selected, Object exclude) {
        this(removeElements(values, new Object[]{exclude}), selected);
    }

    public EnumI18NComboBoxModel(Object[] values, Object selected, Object[] exclude) {
        this(removeElements(values, exclude), selected);
    }

    /**
     * The model must fire a ListDataEvent(this,
     * ListDataEvent.CONTENTS_CHANGED, -1, -1) if the selection has actually changed.
     *
     * @param item the item to be selected
     */
    public void setSelectedItem(Object item) {
        fSelectedObject = item;
        fireContentsChanged(this, -1, -1);
    }

    /**
     * @return the selected item. If none is selected, the "first" element of the map will be selected and returned.
     */
    public Object getSelectedItem() {
        if (fSelectedObject == null) {
            setSelectedEnum(fLocalizationMap.values().toArray()[0]);
        }
        return fSelectedObject;
    }

    /**
     * @return the selected enum. If none is selected, the "first" element of the map will be selected and returned.
     */
    public Object getSelectedEnum() {
        if (fSelectedObject == null) {
            setSelectedEnum(fLocalizationMap.values().toArray()[0]);
        }
        return fLocalizationMap.get(fSelectedObject);
    }

    public void setSelectedEnum(Object enumToSelect) {
        fSelectedObject = (enumToSelect == null) ? null : getLocalizedText(enumToSelect.toString());
        fireContentsChanged(this, -1, -1);
    }

    public int getSize() {
        return fLocalizationMap.size();
    }

    public Object getElementAt(int index) {
        if (index >= 0 && index < getSize()) {
            return fLocalizationMap.keySet().toArray()[index];
        } else {
            return null;
        }
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getLocalizedText(String key) {
        return LocaleResources.getString(fEnumeration.getSimpleName() + "." + key);
    }

    public static Object[] removeElements(Object[] allElements, Object[] elementsToRemove) {
        Object[] newList = new Object[allElements.length - elementsToRemove.length];
        int i = 0;
        for (Object item : allElements) {
            if (!Arrays.asList(elementsToRemove).contains(item)) {
                newList[i++] = item;
            }
        }
        return newList;
    }

    public void removeElement(Object element) {
        fLocalizationMap.remove(getLocalizedText(element.toString()));
    }
}
