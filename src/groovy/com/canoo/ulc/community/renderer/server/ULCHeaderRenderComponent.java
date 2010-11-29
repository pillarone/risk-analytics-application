package com.canoo.ulc.community.renderer.server;

import com.ulcjava.base.application.IRendererComponent;
import com.ulcjava.base.application.ULCComponent;
import com.ulcjava.base.server.ICellComponent;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.swing.*;

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class ULCHeaderRenderComponent extends ULCComponent implements IRendererComponent {
    @Override
    protected String getPropertyPrefix() {
        return "HeaderRenderer";
    }

    ImageIcon icon;
    String text;

    public ULCHeaderRenderComponent() {
    }

    @Override
    protected void uploadStateUI() {
        super.uploadStateUI();
    }

    public void copyAttributes(ICellComponent iCellComponent) {
        if (iCellComponent instanceof ULCHeaderRenderComponent) {
            setIcon(((ULCHeaderRenderComponent) iCellComponent).getIcon());
            setText(((ULCHeaderRenderComponent) iCellComponent).getText());
        }
    }

    public boolean areAttributesEqual(ICellComponent iCellComponent) {
        if (iCellComponent instanceof ULCHeaderRenderComponent) {
            ULCHeaderRenderComponent renderComponent = (ULCHeaderRenderComponent) iCellComponent;
            return equals(getText(), renderComponent.getText()) && equals(getIcon(), renderComponent.getIcon());
        }
        return false;
    }

    public int attributesHashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getIcon() != null ? getIcon().hashCode() : 0).append(getText() != null ? getText().hashCode() : 0);
        return hashCodeBuilder.toHashCode();
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
        setStateUI("icon", icon);
    }

    public void setText(String text) {
        this.text = text;
        setStateUI("text", text);
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    @Override
    protected String typeString() {
        return "com.canoo.ulc.community.renderer.client.UIHeaderRenderComponent";
    }
}
