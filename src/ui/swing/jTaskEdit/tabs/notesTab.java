package jtaskui.ui.swing.jTaskEdit.tabs;

// Import the TreeTable
import TreeTable.TreeTable;
import jtaskui.TreeTableModels.notesTabTreeTableModel;
import jtaskui.ui.swing.jNotesEdit.jNotesEdit;
import jtaskui.ui.swing.jTaskEdit.jteListener;
import jtaskui.TaskObj;
import jtaskui.Task.NoteObj;

import javax.swing.SwingUtilities;

import javax.swing.tree.TreePath;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class notesTab implements notesListener {
    // This is the Task that is "open" in jTaskEdit (possibly with notes attached)
    private TaskObj task;
    private notesTabTreeTableModel treeTableModel;
    private TreeTable notesTreeTable;

    /**
     * Accepts the TaskObj that is open in jTaskEdit
     */
    public notesTab(TaskObj task) {
        // Set a reference to the open Task
        this.task = task;
    }

    /**
     * Builds and returns a JPanel with the notesTab on it. This is added to the tabbed pane in jTaskEdit.
     */
    public JPanel buildPanel() {
        // Create the main panel that will get added to the tPane
        JPanel notesPanel = new JPanel(new BorderLayout());

        // Make a new TreeTableModel and pass the note ROOT object
        treeTableModel = new notesTabTreeTableModel(this.task.getNoteRoot());
        // Add the TreeTableModel to the Treetable
        notesTreeTable = new TreeTable(treeTableModel);

        // TODO: This looks like a smell
        notesTab t = this;
        // TODO: Should this be here ?
        notesTreeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                // Detect double click events
                if (me.getClickCount() == 2) {
                    // Invoke a thread for the edit frame
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: I need to listen for changes from this so the TreeTable can be updated on changes
                            // Open the notes editor window with the selected note
                            jNotesEdit jne = new jNotesEdit(getSelectedNote());
                            jne.addListener(t);
                        }
                    });
                }
            }
        });

        // Hide the root node
        notesTreeTable.setRootVisible(false);
        // Make sure all the chidlren have a expansion handles
        notesTreeTable.setShowsRootHandles(true);

        // Create the action panel for the notes Tab
        notesActionPanel notesActionPanel = new notesActionPanel();
        // Listen for events from the notesActionsPanel buttons
        notesActionPanel.addListener(this);

        // Add the TreeTable to a scroll pane
        JScrollPane notesScrollpane = new JScrollPane(notesTreeTable);

        // Add the action panel and scroll pane with TreeTable to the JPanel
        notesPanel.add(notesActionPanel.getActionsPanel(), BorderLayout.NORTH);
        notesPanel.add(notesScrollpane, BorderLayout.CENTER);

        return notesPanel;
    }

    /**
     * Get selected note from the notes table
     *
     * @return int - row number that has been selected
     */
    private int getSelectedRow() {
        return notesTreeTable.getSelectedRow();
    }

    /**
     * Get the selected Task (in this case, its a note) from the notes table
     *
     * @return NoteObj - the selected note
     */
    private NoteObj getSelectedNote() {
        return (NoteObj) treeTableModel.nodeForRow(notesTreeTable.convertRowIndexToModel(getSelectedRow()));
    }

    public void jteNoteActionsNewNote() {
        NoteObj newNote = new NoteObj("New Note");
        treeTableModel.insertNodeInto(newNote, task.getNoteRoot(), task.getNoteRoot().getChildCount());
        // Launch a new editor window
        jNotesEdit jne = new jNotesEdit(newNote);
        jne.addListener(this);
    }

    public void jteNoteActionsNewSubNote() {
        NoteObj selectedTask = getSelectedNote();
        NoteObj newNote = new NoteObj("New Sub Note");
        treeTableModel.insertNodeInto(newNote, selectedTask, selectedTask.getChildCount());
        // Launch a new editor window
        jNotesEdit jne = new jNotesEdit(newNote);
        jne.addListener(this);
    }

    public void jteNoteActionsDeleteNote() {
        treeTableModel.removeNodeFromParent(getSelectedNote());
    }

    public void jteNoteUpdateNoteTreeTable(TreePath path, String columnName) {
        // TODO: This is required because NoteObj.getPath() doesnt include the ROOT object
        TreePath p = new TreePath(treeTableModel.getRoot());
        for(int i = 0; i < path.getPathCount(); i++) {
            p = p.pathByAddingChild(path.getPathComponent(i));
        }
        // Notify the model of the relevant change
        treeTableModel.valueForPathChanged(p, treeTableModel.findColumn(columnName));
    }
}
