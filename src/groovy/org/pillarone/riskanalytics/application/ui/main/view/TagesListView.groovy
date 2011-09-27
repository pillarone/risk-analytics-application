package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCCheckBox
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import com.ulcjava.base.application.event.ValueChangedEvent
import com.ulcjava.base.application.event.IValueChangedListener

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TagesListView extends AbstractView {

    public ULCBoxPane content
    public List<Tag> itemTages
    private List<ULCCheckBox> tagsCheckBoxes
    private ModellingItem modellingItem
    List<Tag> allTags


    public TagesListView(ModellingItem modellingItem) {
        this.modellingItem = modellingItem
        content = new ULCBoxPane(1, 0)
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
        itemTages = modellingItem.getTags()

        tagsCheckBoxes = new ArrayList<ULCCheckBox>()
        allTags.each {Tag tag ->
            ULCCheckBox checkBox = new ULCCheckBox(tag.name)
            println "checkBox ${tag.name}"
            checkBox.name = tag.name
            checkBox.setSelected(itemTages.contains(tag))
            checkBox.addValueChangedListener([valueChanged: {ValueChangedEvent valueChangedEvent ->
                ULCCheckBox box = (ULCCheckBox) valueChangedEvent.source
                Tag newTag = allTags.find {it.name == box.getText() }
                if (box.isSelected() && newTag) {
                    if (!itemTages.contains(newTag)) itemTages << newTag
                } else {
                    itemTages.remove(newTag)
                }
            }] as IValueChangedListener)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, checkBox)
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


}
