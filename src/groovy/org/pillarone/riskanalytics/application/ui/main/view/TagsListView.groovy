package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import com.ulcjava.base.application.util.Color
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import org.pillarone.riskanalytics.core.workflow.Status

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TagsListView extends AbstractView {

    private static Log LOG = LogFactory.getLog(TagsListView )

    // Set -DvetoDupQtrTagsInWorkflow=true to activate extra Quarter Tag validation on Workflow p14ns
    //
    private static boolean vetoDupQtrTagsInWorkflow = System.getProperty("vetoDupQtrTagsInWorkflows","true").equalsIgnoreCase("true");

    private ULCWindow parent
    public ULCBoxPane content
    public List<Tag> itemTags
    private List<ULCCheckBox> tagsCheckBoxes
    private List<ModellingItem> modellingItems
    List<Tag> allTags

    public TagsListView(List<ModellingItem> modellingItems, ULCWindow parent) {
        this.modellingItems = modellingItems
        content = new ULCBoxPane(2, 0)
        this.parent = parent
    }

    // TODO rename to createNewTag and fix any breaking tests
    //
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
        itemTags = getAllModellingItemTages()

        tagsCheckBoxes = new ArrayList<ULCCheckBox>()
        allTags.each { Tag tag ->
            ULCCheckBox checkBox = new ULCCheckBox(tag.name)
            checkBox.name = tag.name
            setLookAndFeel(checkBox, tag)
            checkBox.setSelected(itemTags.contains(tag))
            checkBox.addValueChangedListener([valueChanged: { ValueChangedEvent valueChangedEvent ->
                ULCCheckBox box = (ULCCheckBox) valueChangedEvent.source
                Tag newTag = allTags.find { it.name == box.getText() }
                if (box.isSelected() && newTag) {
                    addTagToItem(newTag)
                } else {
                    removeTag(newTag)
                }
                setLookAndFeel(box, newTag)
            }] as IValueChangedListener)
            content.add(ULCBoxPane.BOX_LEFT_TOP, checkBox)
            content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        }
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
    }

    @Override
    protected void layoutComponents() {
    }

    @Override
    protected void attachListeners() {
    }

    public List<Tag> getAllTags() {
        return Tag.findAll(" from ${Tag.class.name} as tag where tag.tagType =? and tag.name != ? order by tag.name asc", [EnumTagType.PARAMETERIZATION, Tag.LOCKED_TAG])
    }

    private List<Tag> getAllModellingItemTages() {
        Set<Tag> all = new HashSet<Tag>()
        for (ModellingItem item : modellingItems) {
            for (Tag tag : item.getTags()) {
                all.add(tag)
            }
        }
        return all as List
    }

    private void addTagToItem(Tag tag) {
        if (!itemTags.contains(tag)) itemTags << tag            // Some itemTags were only on subset of selected items
        for (ModellingItem modellingItem : modellingItems) {
            if (!modellingItem.getTags().contains(tag)) {
                if(!vetoDupQtrTagsInWorkflow){
                    modellingItem.getTags().add(tag)
                    modellingItem.setChanged(true)
                }else{

                    if( !workflowAlreadyContainsQuarterTag( modellingItem, tag ) ){
                        modellingItem.getTags().add(tag)
                        modellingItem.setChanged(true)
                    }
                }
            }
        }
    }

    // PMO-2741
    //
    private boolean workflowAlreadyContainsQuarterTag( ModellingItem modellingItem, Tag tag ){

        // Only checking quarter tags in workflows (p14ns with workflow status)
        //
        if( ! tag.isQuarterTag() ){
            return false
        }
        // Separated this test from previous one - groovy is sometimes very uncool
        // Problem was ! binds tigher than instanceof, and !modellingItem decays to a null-ness check.
        boolean isP14n = (modellingItem instanceof Parameterization)
        if( ! isP14n ){
            return false
        }

        Parameterization parameterization = modellingItem as Parameterization
        if( parameterization.status == Status.NONE ){
            return false                                                // Dont care about non workflow p14ns here
        }

        // Okay, user is adding quarter tag to a workflow :-
        // Does any p14n in workflow already have it ?
        // @matthias: What is lightest way to get hold of all tags in p14ns matching name and class ?
        //

        for( VersionNumber versionNumber : VersionNumber.getExistingVersions(parameterization)){

            if(parameterization.versionNumber.toString().equals(versionNumber.toString())){
                continue // No need to check p14n being tagged - it doesn't have it yet
            }

            // TODO use a method like existing getAllTags() instead to get the tags for each p14n
            // Or ask Matthias for a better way
            //
            Parameterization otherWorkflowP14n = new Parameterization(parameterization.name,parameterization.modelClass)
            otherWorkflowP14n.versionNumber = versionNumber
            otherWorkflowP14n.load();

            // Is there a better way ? All i need is its tags..
            for( Tag t in otherWorkflowP14n.tags ){
                if( tag.equals(t) ){
                    String firstLine = "Cannot add tag ${tag.name} to ${parameterization.nameAndVersion}"
                    String secondLine= "Pls untag version: $parameterization.name v${otherWorkflowP14n.versionNumber.toString()} first."
                    LOG.warn(firstLine + " " + secondLine)
                    LOG.info("To allow duplicate qtr tags in workflows, override -DvetoDupQtrTagsInWorkflow=false ")
                    ULCAlert alert = new ULCAlert( parent, "Error duplicating quarter tag in workflow", firstLine + "\n" + secondLine, "Ok")
                    alert.messageType = ULCAlert.INFORMATION_MESSAGE
                    alert.show()
                    return true
                }
            }
        }

        return false

    }

    private void removeTag(Tag tag) {
        itemTags.remove(tag)
        for (ModellingItem modellingItem : modellingItems) {
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

    private Color getColor(Tag tag) {
        if (modellingItems.every { it.getTags().contains(tag) }) return Color.black
        if (modellingItems.any { it.getTags().contains(tag) }) return Color.gray
        return Color.black
    }

}
