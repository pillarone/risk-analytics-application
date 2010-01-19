package com.canoo.ulc.detachabletabbedpane.client;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.io.IOException;

/**
 * UIClass that wraps functionality of BasicDnDTabbedPane. For further explanation, see BasicDnDTabbedPane
 * <p>
 * 
 * @see BasicDnDTabbedPane
 * @author Alexandra Teynor
 * @author <a href="mailto:Alexandra.Teynor@canoo.com">Alexandra.Teynor@canoo.com</a>
 * @version 1.0, &nbsp; 02-OCT-2009
 */
public class UIDetachableTabbedPane extends UICloseableTabbedPane {
    
    private int groupId;
    
    
    @Override
    protected Object createBasicObject(Object[] args) {
        return new BasicDnDTabbedPane();
    }
    
    /**
     * Set the groupID of this tabbed pane. The groupID is the same for all tabbedPanes that were derived from one tabbed pane (this tabbed
     * has also the same groupID
     * 
     * @param id the group ID
     **/
    public void setGroupId(int id) {
        groupId = id;
    }
    
    /**
     * Get the groupID of this tabbed pane.
     * 
     * @return the groupId
     **/
    public int getGroupId() {
        return groupId;
    }
    
    /**
     * Class that manages detaching functionality for a tabbed pane on top of a <code>ULCCloseableTabbedPane</code>. Here the the Drag and
     * Drop functionality and some additional logic is implemented.
     */
    public class BasicDnDTabbedPane extends BasicCloseableTabbedPane {
        
        private static final String NAME = "TRANSFERTAB";
        private final DataFlavor TABFLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
        
        private boolean dragSourceNow;
        
        /**
         * Closes a tab if it is closeable and sends a request to the server side so eventually empty frames can be closed and deregistered
         * 
         * @param component the tab containing this component should be closed
         */
        protected void closeClosingTab(Component component) {
            // Override from UICloseableTabbedPane => also inform the ULC Part that something has been closed,
            // in order to eventually clean up
            super.closeClosingTab(component);
            invokeULC("cleanUpFrames", new Object[] {});
        }
        
        /**
         * DropTargetListener that handles the dropping functionality (i.e. check whether a tab might be acceptable and the actual drop
         * (telling the server side to import a tab)
         */
        class TpDropTargetListener implements DropTargetListener {
            
            public void dragEnter(DropTargetDragEvent e) {
                if (isDragAcceptable(e)) {
                    e.acceptDrag(e.getDropAction());
                } else {
                    e.rejectDrag();
                }
            }
            
            public void dragExit(DropTargetEvent e) {
            }
            
            public void dropActionChanged(DropTargetDragEvent e) {
            }
            
            public void dragOver(final DropTargetDragEvent e) {
            }
            
            public void drop(DropTargetDropEvent e) {
                
                // transferring the tab
                Transferable t = e.getTransferable();
                
                try {
                    UIDetachableTabbedPane tp = (UIDetachableTabbedPane)t.getTransferData(TABFLAVOR);
                    int sIdx = tp.getBasicTabbedPane().getSelectedIndex();
                    int objectId = tp.getId();
                    
                    invokeULC("importTab", new Object[] {new Integer(objectId), new Integer(sIdx)});
                    
                    setSelectedIndex(getComponentCount() - 1);
                    
                } catch (UnsupportedFlavorException ufe) {
                    ufe.printStackTrace();
                    e.dropComplete(false);
                } catch (IOException iex) {
                    iex.printStackTrace();
                    e.dropComplete(false);
                }
            }
            
            public boolean isDragAcceptable(DropTargetDragEvent e) {
                
                // if dragging from this tab, do not accept
                // (dragging only to other tabbed panes)
                if (dragSourceNow)
                    return false;
                
                // eventually check, whether the frame belongs to the same "group"
                // => this frame is a dependent frame of the dragSource or
                // this frame and the drag source are the dependent fames of the same
                // tabbedPane
                // => aks the client
                Transferable t = e.getTransferable();
                try {
                    
                    UIDetachableTabbedPane tp = (UIDetachableTabbedPane)t.getTransferData(TABFLAVOR);
                    
                    if (getGroupId() != tp.getGroupId())
                        return false;
                    
                } catch (UnsupportedFlavorException ufe) {
                    ufe.printStackTrace();
                } catch (IOException iex) {
                    iex.printStackTrace();
                }
                

                return e.isDataFlavorSupported(TABFLAVOR);
            }
            
            public boolean isDropAcceptable(DropTargetDropEvent e) {
                return true;
            }
            
        }
        
        /**
         * Public default constructor
         */
        public BasicDnDTabbedPane() {
            
            // init
            dragSourceNow = false;
            
            // Drag Source Listener
            final DragSourceListener dsl = new DragSourceListener() {
                
                public void dragEnter(DragSourceDragEvent e) {
                    e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
                }
                
                public void dragExit(DragSourceEvent e) {
                    e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
                }
                
                public void dragOver(DragSourceDragEvent e) {
                }
                
                public void dragDropEnd(DragSourceDropEvent e) {
                    dragSourceNow = false;
                }
                
                public void dropActionChanged(DragSourceDragEvent e) {
                }
            };
            

            final Transferable t = new Transferable() {
                
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                    
                    if (flavor.getHumanPresentableName().equals(NAME)) {
                        return UIDetachableTabbedPane.this;
                    } else {
                        throw new UnsupportedFlavorException(flavor);
                    }
                }
                
                public DataFlavor[] getTransferDataFlavors() {
                    DataFlavor[] f = new DataFlavor[1];
                    f[0] = TABFLAVOR;
                    return f;
                }
                
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return flavor.getHumanPresentableName().equals(NAME);
                }
            };
            

            final DragGestureListener dgl = new DragGestureListener() {
                
                public void dragGestureRecognized(DragGestureEvent e) {
                    try {
                        e.startDrag(DragSource.DefaultMoveNoDrop, t, dsl);
                        dragSourceNow = true;
                        
                    } catch (InvalidDnDOperationException idoe) {
                        idoe.printStackTrace();
                    }
                }
            };
            
            new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new TpDropTargetListener(), true);
            new DragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, dgl);
            
        }
        

    }
    
}
