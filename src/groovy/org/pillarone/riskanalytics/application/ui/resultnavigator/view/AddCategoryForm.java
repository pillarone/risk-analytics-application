package org.pillarone.riskanalytics.application.ui.resultnavigator.view;

import com.ulcjava.applicationframework.application.form.AbstractFormBuilder;
import com.ulcjava.applicationframework.application.form.model.FormModel;
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.NameBean;

public class AddCategoryForm extends AbstractFormBuilder<FormModel<NameBean>> {

    public AddCategoryForm() {
        super(new FormModel<NameBean>(new NameBean()));

    }

    @Override
    protected void initForm() {
        setColumnWeights(0f, 0f, 1f);
        addTextField("name", "Name");
    }
}