package view;

import network.ServerServiceThread;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

public class MyDropTarget extends DropTarget {

    private ServerServiceThread serverService;

    public MyDropTarget(ServerServiceThread serverService) {
        this.serverService = serverService;
    }

    @Override
    public synchronized void drop(DropTargetDropEvent dtde) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            List<File> droppedFiles = (List<File>)
                    dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            for (File file : droppedFiles) {
                serverService.addFileToServer(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
