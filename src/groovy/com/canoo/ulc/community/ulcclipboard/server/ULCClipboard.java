package com.canoo.ulc.community.ulcclipboard.server;

import com.ulcjava.base.application.ApplicationContext;
import com.ulcjava.base.application.ULCProxy;

public class ULCClipboard extends ULCProxy {

    protected String typeString() {
        return "com.canoo.ulc.community.ulcclipboard.client.UIClipboard";
    }

    protected void initiateClipboardAction() {
        invokeUI("initiateClipboardAction");
    }


    public static void install() {
        ULCClipboard clipboard = new ULCClipboard();
        clipboard.upload();
        ApplicationContext.setAttribute("clipboard", clipboard);
    }

    public static ULCClipboard getClipboard() {
        Object clipboard = ApplicationContext.getAttribute("clipboard");
        if (clipboard == null) {
            throw new IllegalStateException("No clipboard available. Call ULCClipboard.install() first!");
        }
        return (ULCClipboard) clipboard;
    }

    public void applyContent(String contentFromUI) {
        IClipboardHandler handler = (IClipboardHandler) ApplicationContext.getAttribute("clipboardHandler");
        if (handler == null) {
            throw new IllegalStateException("No IClipboardHandler available!");
        }

        try {
            handler.applyContent(contentFromUI);
        } finally {
            ApplicationContext.removeAttribute("clipboardHandler");
        }
    }


    public static void appyClipboardContent(IClipboardHandler handler) {
        ApplicationContext.setAttribute("clipboardHandler", handler);
        getClipboard().initiateClipboardAction();
    }

    public void setContent(String content) {
        invokeUI("setContent", new Object[]{content});
    }
}
