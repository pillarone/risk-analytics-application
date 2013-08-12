package org.pillarone.riskanalytics.application;

import org.pillarone.riskanalytics.core.modellingitem.ModellingItemHibernateListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostDeleteEvent;
import com.ulcjava.base.application.UlcUtilities;


public class ULCAwareHibernateListener extends ModellingItemHibernateListener {

    @Override
    public void onPostInsert(final PostInsertEvent postInsertEvent) {
        if (UlcUtilities.isUlcThread()) {
            UlcUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ULCAwareHibernateListener.super.onPostInsert(postInsertEvent);
                }
            });
        } else {
            super.onPostInsert(postInsertEvent);
        }
    }

    @Override
    public void onPostUpdate(final PostUpdateEvent postUpdateEvent) {
        if (UlcUtilities.isUlcThread()) {
            UlcUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ULCAwareHibernateListener.super.onPostUpdate(postUpdateEvent);
                }
            });
        } else {
            super.onPostUpdate(postUpdateEvent);
        }
    }

    @Override
    public void onPostDelete(final PostDeleteEvent postDeleteEvent) {
        if (UlcUtilities.isUlcThread()) {
            UlcUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ULCAwareHibernateListener.super.onPostDelete(postDeleteEvent);
                }
            });
        } else {
            super.onPostDelete(postDeleteEvent);
        }
    }
}
