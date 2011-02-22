package org.pillarone.riskanalytics.application.ui.comment.model

import com.ulcjava.base.application.DefaultListModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ItemListModel<T> extends DefaultListModel {
    List values

    public ItemListModel(T[] objects, List values) {
        super(objects);
        this.values = values
    }

    int[] getSelectedIndices(List<T> items) {
        if (!items) return [] as int[]
        int[] selectedItems = new int[items.size()]
        items.eachWithIndex {T item, int index ->
            selectedItems[index] = indexOf(item)
        }
        return selectedItems
    }

    Set getSelectedValues(int[] selectedIndices) {
        Set tags = new HashSet()
        for (int index: selectedIndices) {
            tags << values.get(index)
        }
        return tags
    }

    public void add(Object o, def value) {
        super.add(o);
        values.add(value)
    }


}
