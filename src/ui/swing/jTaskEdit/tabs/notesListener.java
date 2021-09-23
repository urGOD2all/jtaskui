package jtaskui.ui.swing.jTaskEdit.tabs;

import javax.swing.tree.TreePath;

public interface notesListener {
    /* 
     * Task Action Panel actions
     */
    void jteNoteActionsNewNote();
    void jteNoteActionsNewSubNote();

    void jteNoteActionsDeleteNote();
    /*
     * Notes tab updates
     */
    void jteNoteUpdateNoteTreeTable(TreePath path, String columnName);
}
