package org.pillarone.riskanalytics.application.ui.resultnavigator.view

import com.ulcjava.applicationframework.application.AbstractBean
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.applicationframework.application.ApplicationContext
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ResultAccess

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCScrollPane

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElementTableModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryColumnMapping

/**
 * @author martin.melchior
 */
class ResultNavigator extends AbstractBean {

    private static Log LOG = LogFactory.getLog(ResultNavigator.class);

    private ContentView contentView;
    ResultAccess resultAccess

    /* Context is needed to load resources (such as icons, etc).*/
    private ApplicationContext context;


    /**
     * @param context Application context is used for accessing and using resources (such as icons, etc.).
     */
    public ResultNavigator(ApplicationContext context) {
        this.context = context;
        resultAccess = new ResultAccess()
        contentView = new ContentView()
    }

    /**
     *
     */
    public ULCComponent getContentView() {
        return contentView;
    }

    public void loadSimulationRun(SimulationRun run) {
        contentView.loadSimulationRun run
    }


    private class ContentView extends ULCBoxPane {

        ContentView() {
            super(true)
            this.setPreferredSize(new Dimension(800, 600))
            this.setBorder(BorderFactory.createEmptyBorder());
            this.setVisible true
        }

        void loadSimulationRun(SimulationRun run) {
            List<OutputElement> elements = resultAccess.getOutputElements(run)
            CategoryColumnMapping categories = new CategoryColumnMapping()
            resultAccess.addCategoryInformation(elements, categories, run)
            OutputElementTableModel model = new OutputElementTableModel(elements, categories)
            
            OutputElementTable table = new OutputElementTable(model)
            ULCScrollPane scrollPane = new ULCScrollPane()
            scrollPane.setViewPortView(table)

            FilterPanel filterPanel = new FilterPanel(model.getCategories())

            contentView.removeAll()
            contentView.add(ULCBoxPane.BOX_LEFT_TOP, filterPanel);
            contentView.add(ULCBoxPane.BOX_EXPAND_EXPAND, scrollPane);

            filterPanel.registerFilterListener table

            table.addCategoryListChangeListener(filterPanel.getCategoryToFilter())
        }
    }
}
