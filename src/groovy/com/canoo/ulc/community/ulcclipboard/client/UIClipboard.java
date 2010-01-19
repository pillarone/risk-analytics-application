package com.canoo.ulc.community.ulcclipboard.client;

import com.ulcjava.base.client.UIProxy;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;


public class UIClipboard extends UIProxy {


    public void initiateClipboardAction() {
        invokeULC("applyContent", new Object[]{getContent()});
    }

    private Object getContent() {
        Object content = null;
        Clipboard clipboard = getSystemClipboard();
        if (clipboard != null) {
            try {
                Transferable transferable = clipboard.getContents(this);
                if (transferable != null) {
                    content = transferable.getTransferData(DataFlavor.stringFlavor);
                }
            } catch (UnsupportedFlavorException e1) {            // TODO (Apr 14, 2009, msh): Log exception
                e1.printStackTrace();
            } catch (IOException e1) {            // TODO (Apr 14, 2009, msh): Log exception
                e1.printStackTrace();
            }
        }

        return content;
    }

    public void setContent(String content) {
        if (canAccessSystemClipboard()) {
            getSystemClipboard().setContents(new StringSelection(content), null);
        }
        // TODO (Apr 14, 2009, msh): and if not ??
    }

    private boolean canAccessSystemClipboard() {
        return getSystemClipboard() != null;
    }

    private Clipboard getSystemClipboard() {
        Clipboard clipboard = null;
        try {
            clipboard = AccessController.doPrivileged(new PrivilegedAction<Clipboard>() {
                public Clipboard run() {
                    return Toolkit.getDefaultToolkit().getSystemClipboard();
                }
            });
        } catch (Exception e1) {
            // TODO (Apr 14, 2009, msh): Log exception
        }
        return clipboard;
    }
}
