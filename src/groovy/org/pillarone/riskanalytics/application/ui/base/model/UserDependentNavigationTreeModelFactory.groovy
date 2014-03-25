package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.StandaloneTableTreeModel
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.config.AutowireCapableBeanFactory

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_BY_NAME

class UserDependentNavigationTreeModelFactory implements BeanFactoryAware {

    private BeanFactory beanFactory

    UserDependentNavigationTreeModelFactory() {
    }

    @Override
    void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory
    }

    ModellingInformationTableTreeModel createModel() {
        ModellingInformationTableTreeModel model
        if (UserContext.hasCurrentUser()) {
            model = new ModellingInformationTableTreeModel()
        } else {
            model = new StandaloneTableTreeModel()
        }
        AutowireCapableBeanFactory factory = beanFactory as AutowireCapableBeanFactory
        factory.autowireBeanProperties(model, AUTOWIRE_BY_NAME, false)
        model
    }
}
