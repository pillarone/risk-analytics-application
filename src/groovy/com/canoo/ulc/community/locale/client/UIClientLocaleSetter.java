package com.canoo.ulc.community.locale.client;

import com.ulcjava.base.client.UIProxy;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: Matthias Ansorge
 * Date: Nov 22, 2010
 * Time: 3:16:34 PM
 */
public class UIClientLocaleSetter extends UIProxy {

    @Override
    protected Object createBasicObject(Object[] arguments) {
        Locale.setDefault(new Locale((String) arguments[0], (String) arguments[1], (String) arguments[2]));
        resetResourceBundles();
        return null;
    }

    private void resetResourceBundles() {
        try {
            Field field = Toolkit.class.getDeclaredField("resources");
            field.setAccessible(true);
            field.set(null, ResourceBundle.getBundle("sun.awt.resources.awt"));
        } catch (NoSuchFieldException ignored) {
        } catch (IllegalAccessException ignored) {
        }
    }
}