package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import com.ulcjava.base.application.util.Color
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.ParameterizationDAO
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
    private static boolean vetoDupQtrTagsInWorkflow = System.getProperty("vetoDupQtrTagsInWorkflows","false").equalsIgnoreCase("true");

    public ULCBoxPane content
    public List<Tag> itemTags
    private List<ULCCheckBox> tagsCheckBoxes
    private List<ModellingItem> modellingItems
    List<Tag> allTags

    public TagsListView(List<ModellingItem> modellingItems) {
        this.modellingItems = modellingItemss
        content = new ULCBoxPane(2, 0)
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
                    //TODO add check for workflow p14n and quarter tags at ART
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

        if( ! tag.isQuarterTag() ){
            return false;                                               // Dont care about non quarter tags here
        }
        if( ! modellingItem instanceof Parameterization ){
            return false                                                // Dont care about non p14ns here
        }
        Parameterization parameterization = modellingItem as Parameterization
        if(parameterization.status == Status.NONE){
            return false                                                // Dont care about non workflow p14ns here
        }

        // User is adding quarter tag to a workflow - Does any p14n in workflow already have it ?
        // @matthias: What is best way to get hold of all p14ns matching name and class ?

        SortedSet allVersions = new TreeSet(VersionNumber.getExistingVersions(parameterization))

        for( VersionNumber versionNumber : allVersions ){
            if(parameterization.versionNumber.toString().equals(versionNumber.toString())){
                continue
            }
            ParameterizationDAO dao = ParameterizationDAO.find(parameterization.name,parameterization.modelClass.name, versionNumber.toString())
            List<Tag> tagList = dao.tags*.tag
            // For some reason, pDao's tags cannot be seen when running ra-apps vanilla - maybe the table is not
            // setup in the in-memory or mysql db that is used here ?  I will leave this code deactivated by default.
            // Then I will override to true at runtime to test at ART
            //
            for( Tag t in tagList ){
                if( tag.equals(t) ){
                    String msg = "'${tag.name}' already exists on version v${pDao.itemVersion}"
                    LOG.warn("NOT TAGGING ${parameterization.nameAndVersion}; " + msg)
                    LOG.info("To allow duplicate qtr tags in workflows, override -DvetoDupQtrTagsInWorkflow=false ")
                    ULCAlert alert = new ULCAlert(
                            UlcUtilities.getWindowAncestor(this),
                            "Cannot duplicate quarter tag in Workflow",
                            msg,
                            "Ok")
                    alert.messageType = ULCAlert.INFORMATION_MESSAGE
                    alert.show()
                    return true
                }
            }
        }

//        List<ParameterizationDAO> allInWorkflow =
//            parameterization.daoClass.findAllByNameAndModelClassName(parameterization.name, parameterization.modelClass.name)
//
//        for( ParameterizationDAO pDao : allInWorkflow ){
//            if(parameterization.versionNumber.toString().equals(pDao.itemVersion)){
//                continue
//            }
//
//            // Will this load the dao including the tags ?
//            //
//            ParameterizationDAO.find(parameterization.name, parameterization.modelClass?.name, pDao.itemVersion)
//
//        }
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
