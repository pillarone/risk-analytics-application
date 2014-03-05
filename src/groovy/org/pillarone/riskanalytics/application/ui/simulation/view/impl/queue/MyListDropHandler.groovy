package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCList
import com.ulcjava.base.application.dnd.TransferHandler
import com.ulcjava.base.application.dnd.Transferable

import javax.swing.*
import java.awt.datatransfer.DataFlavor

class MyListDropHandler extends TransferHandler {
    ULCList list;


    public MyListDropHandler(ULCList list) {
        this.list = list;
    }

    @Override
    boolean importData(ULCComponent targetComponent, Transferable transferable) {

        String indexString;
        try {
            indexString = (String) transferable.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            return false;
        }

        int index = Integer.parseInt(indexString);
        JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
        int dropTargetIndex = dl.getIndex();

        System.out.println(dropTargetIndex + " : ");
        System.out.println("inserted");
        return true;
    }

    @Override
    void exportDone(ULCComponent sourceComponent, Transferable transferable, int dropAction) {

    }
}