package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCCheckBox
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import com.ulcjava.base.application.event.ValueChangedEvent
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.border.ULCAbstractBorder
import com.ulcjava.base.application.BorderFactory
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TagesListView extends AbstractView {

    public ULCBoxPane content
    public List<Tag> itemTages
    private List<ULCCheckBox> tagsCheckBoxes
    private ModellingItem modellingItem
    private List<ModellingItem> modellingItems
    List<Tag> allTags

    public TagesListView(List<ModellingItem> modellingItems) {
        this.modellingItems = modellingItems
        content = new ULCBoxPane(2, 0)
    }

    public void addTag(String tagName) {
        if (tagName && !Tag.findByName(tagName)) {
            Tag newTag = new Tag(name: tagName, tagType: EnumTagType.PARAMETERIZATION)
            Tag.withTransaction {
                newTag.save()
                allTags.add(newTag)
            }
            content.removeAll()
            initComponents()
        }

    }

    @Override
    protected void initComponents() {
        allTags = getAllTags()
        itemTages = getAllModellingItemTages()

        tagsCheckBoxes = new ArrayList<ULCCheckBox>()
        allTags.each {Tag tag ->
            ULCCheckBox checkBox = new ULCCheckBox(tag.name)
            checkBox.name = tag.name
            setLookAndFeel(checkBox, tag)
            checkBox.setSelected(itemTages.contains(tag))
            checkBox.addValueChangedListener([valueChanged: {ValueChangedEvent valueChangedEvent ->
                ULCCheckBox box = (ULCCheckBox) valueChangedEvent.source
                Tag newTag = allTags.find {it.name == box.getText() }
                if (box.isSelected() && newTag) {
                    addTag(newTag)
                } else {
                    removeTag(newTag)
                }
                setLookAndFeel(box, newTag)
            }] as IValueChangedListener)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, checkBox)
            content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        }
    }

    @Override
    protected void layoutComponents() {
    }

    @Override
    protected void attachListeners() {
    }

    public List<Tag> getAllTags() {
        return Tag.findAll(" from ${Tag.class.name} as tag where tag.tagType =? and tag.name != ? order by tag.name asc", [EnumTagType.PARAMETERIZATION, "LOCKED"])
    }

    private List<Tag> getAllModellingItemTages() {
        Set<Tag> all = new HashSet<Tag>()
        for (ModellingItem item: modellingItems) {
            for (Tag tag: item.getTags()) {
                all.add(tag)
            }
        }
        return all as List
    }

    private Color getColor(Tag tag) {
        if (modellingItems.every {it.getTags().contains(tag)}) return Color.black
        if (modellingItems.any {it.getTags().contains(tag)}) return Color.gray
        return Color.black
    }

    private void addTag(Tag tag) {
        if (!itemTages.contains(tag)) itemTages << tag
        for (ModellingItem modellingItem: modellingItems) {
            if (!modellingItem.getTags().contains(tag)) {
                modellingItem.getTags().add(tag)
                modellingItem.setChanged(true)
            }
        }
    }

    private void removeTag(Tag tag) {
        itemTages.remove(tag)
        for (ModellingItem modellingItem: modellingItems) {
            if (modellingItem.getTags().contains(tag)) {
                modellingItem.getTags().remove(tag)
                modellingItem.setChanged(true)
            }
        }
    }

    private void setLookAndFeel(ULCCheckBox checkBox, Tag tag) {
        Color color = getColor(tag)
        checkBox.setForeground(color)
        checkBox.setBorder(BorderFactory.createLineBorder(color))
    }

}
