package jtaskui.ui.swing.jTaskView;

import javax.swing.tree.TreePath;

/**
 * Interface that sets up the contract between UI componenets of jTaskView
 */
public interface jtvListener {
    /*
     * Menu bar actions
     */
    // File
    void jtvMenuBarFileOpen(String filePath);
    void jtvMenuBarFileSave();
    void jtvMenuBarFileSaveAs(String filePath);
    void jtvMenuBarFileClose();
    void jtvMenuBarFileQuit();
    // View
    void jtvMenuBarViewCreationDate();
    void jtvMenuBarViewModificationDate();
    void jtvMenuBarViewDescription();

    /*
     * Task Action Panel actions
     */
    void jtvTaskActionsNewTask();
    void jtvTaskActionsNewSubTask();

    void jtvTaskActionsDeleteTask();

    /*
     * Main Task UI frame updates
     */
    void jtvUpdateTaskTreeTable(TreePath path, String columnName);
}
