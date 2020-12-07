package jTaskUI.jTaskEdit.tabs;

// Import the TreeTable
import TreeTable.TreeTable;
import jtaskui.TreeTableModels.notesTabTreeTableModel;

import jtaskui.TaskObj;

import jtaskview.ui.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class notesTab {
    private TaskObj task;

    public notesTab(TaskObj task) {
        this.task = task;
    }

    public JPanel buildPanel() {
        // Create the main panel that will get added to the tPane
        JPanel notesPanel = new JPanel();
        // LayoutManager helper
        LayoutManager lm = new LayoutManager(notesPanel);

        // Make a new TreeTableModel and pass the root object
        notesTabTreeTableModel treeTableModel = new notesTabTreeTableModel(this.task);
        // Add the TreeTableModel to the Treetable
        TreeTable notesTreeTable = new TreeTable(treeTableModel);
        // Hide the root node
        notesTreeTable.setRootVisible(false);
        // Make sure all the chidlren have a expansion handles
        notesTreeTable.setShowsRootHandles(true);

        // Add the TreeTable to a scroll pane
        JScrollPane notesScrollpane = new JScrollPane(notesTreeTable);

        lm.addNextStretch(notesScrollpane);

        return notesPanel;
    }
}
