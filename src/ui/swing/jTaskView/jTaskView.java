package jtaskui.ui.swing.jTaskView;

import jtaskui.TaskObj;
import jtaskui.TaskObjRowSorter;

// Import the TreeTable Model used for jTaskView
import jtaskui.view.jTaskViewTreeTableModel;
// Import the TreeTable
import TreeTable.TreeTable;

// Import the Input classes
import jtaskui.Input.*;
// Import the Output classes
import jtaskui.Output.*;

import jtaskui.ui.swing.jTaskEdit.jTaskEdit;

import jtaskui.scheduler.scheduleHandler;

import javax.swing.SwingUtilities;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

import javax.swing.border.BevelBorder;
import java.awt.Dimension;
import java.awt.Point;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.table.TableRowSorter;

import javax.swing.tree.TreePath;

// TODO: Remove this when the diag code is removed
import java.util.Map;

/**
 * This code creates the main window and adds/manages the Task Table.
 */
public class jTaskView implements jtvListener {
    // File chooser
    private JFileChooser fileChooser;
    // File that was opened
    private String taskFilePath;
    // The main window with the Task Table on it
    private JFrame rootFrame;
    // A scroll pane for Task Table (ensures we can scroll if the nodes or nodes that are expanded go beyond the bounds)
    private JScrollPane rootScrollpane;
    // Labels used in the status bar
    private JLabel itemsLabel;
    private JLabel itemStatsLabel;
    // The Task Table
    private TreeTable taskTreeTable;
    // The Model of the Task Table
    private jTaskViewTreeTableModel treeTableModel;
    // The TaskObj for root that is passed to the TreeTableModel
    private TaskObj root;
    // The scheduler, responsible for scheduling operations for reminders
    private scheduleHandler scheduleHandler;
    // Stores a reference to the TaskObj that has been cut or copied, ready for pasting
    private TaskObj taskCutCopy;

    /*
     * Constructors
     */

    /**
     * Default constructor
     */
    public jTaskView() {
        scheduleHandler = new scheduleHandler();
        this.root = new TaskObj("ROOT", scheduleHandler);
    }

    /**
     * Constructor to take a prepopulated TaskObj.
     */
    @Deprecated
    public jTaskView(TaskObj root) {
        this.root = root;
    }

// TODO: This entire method is massive and needs to be broken down into components. This will make this easier to read and maintain
    public void initGUI() {
        // Make the frame and do some general setup
        rootFrame = new JFrame();
        rootFrame.setLayout(new BorderLayout());

        // Make the menu component and listen for changes
        jtvMenuBar menuBar = new jtvMenuBar();
        menuBar.addListener(this);
        // Add the menubar to the frame
        rootFrame.add(menuBar.getMenuBar(), BorderLayout.NORTH);

        // Make a panel for the status bar
        JPanel statusPanel = new JPanel();
        // Lower its edges
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        // Add the status panel to the frame at the bottom
        rootFrame.add(statusPanel, BorderLayout.SOUTH);
        // Make the size of the panel equal to the width of the frame
        statusPanel.setPreferredSize(new Dimension(rootFrame.getWidth(), 16));
        // Set the layout
        statusPanel.setLayout(new BorderLayout());
        // Create a JLabel to display the text of the status panel
        itemsLabel = new JLabel("Will display total number of items etc");
        itemStatsLabel = new JLabel("Will display number of overdue items etc");
        // Add the label to the panel
        statusPanel.add(itemsLabel, BorderLayout.WEST);
        statusPanel.add(itemStatsLabel, BorderLayout.EAST);

        // Make a new TreeTableModel and pass the root object
        treeTableModel = new jTaskViewTreeTableModel(this.root);
        // Create a TreeTable using our TreeTableModel and override prepareRenderer to adjust the cell component depending on Task state.
        taskTreeTable = new TreeTable(treeTableModel) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                // First, call the TreeTable to prepare the renderer
                Component c = super.prepareRenderer(renderer, row, column);
                JComponent jc = (JComponent) c;
                // Get the node for this row from the model using view indexes incase of sorting
                TaskObj rowData = (TaskObj) getModel().nodeForRow(convertRowIndexToModel(row));
                // TODO: Need to have overdue items in red and due today in orange
                // Depending on the state of the task, change the text color
                if (rowData.isComplete()) c.setForeground(Color.GREEN);
                else if (rowData.isStarted()) c.setForeground(Color.BLUE);
                else if (rowData.isDue()) c.setForeground(Color.ORANGE);
                else if (rowData.isOverdue()) c.setForeground(Color.RED);
                else c.setForeground(Color.GRAY);
                // TODO: The expandable column doesnt get the border, need to figure this out. It will be a TreeTable problem not here.
                // Set the copy border
                if(rowData == taskCutCopy) jc.setBorder(BorderFactory.createDashedBorder(Color.DARK_GRAY));
                // Return the modified component
                return c;
            }
        };
        // Hide the root node
        taskTreeTable.setRootVisible(false);
        // Make sure all the chidlren have a expansion handles
        taskTreeTable.setShowsRootHandles(true);

        // Create a TableRowSorter for our model
        TableRowSorter<jTaskViewTreeTableModel> treeTableRowSorter = new TableRowSorter<jTaskViewTreeTableModel>(treeTableModel);
        // Set this TableRowSorter on the TreeTable
        taskTreeTable.setRowSorter(treeTableRowSorter);
        // Set the comparator for the sorter to use
        treeTableRowSorter.setComparator(0, new TaskObjRowSorter(treeTableModel));

        // TODO: This feels like a smell! It's only here because I need to pass it to the anonymous below!
        jTaskView jtui = this;
        // TODO: Should this be here ?
        taskTreeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                // Detect double click events
                if (me.getClickCount() == 2 && me.getButton() == MouseEvent.BUTTON1) {
                    // Call the edit UI
                    jtvTaskActionsEditTask();
                }
            }
        });

        // Remove the TreeTable action on pressing the enter key. This will be handled by the menu accelerator
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        taskTreeTable.getInputMap(TreeTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "Edit");

        // Create the popup menu for the Task TreeTable
        jTaskViewTreeTableRC popupMenu = new jTaskViewTreeTableRC();
        // Listen for events from the menu here
        popupMenu.addListener(this);
        // Set the popupMenu on the TreeTable
        taskTreeTable.setComponentPopupMenu(popupMenu.getPopupMenu());

        // Add the TreeTable to the scroll pane
        JScrollPane rootScrollpane = new JScrollPane(taskTreeTable);
        // Create a center panel
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Create the Tasks Action Panel (has buttons such as New Task, New Sub Task...)
        jtvTaskActionsPanel taskActionsPanel = new jtvTaskActionsPanel();
        // Listen for events from the TaskActionsPanel buttons
        taskActionsPanel.addListener(this);
        // Add the panel to the North of the Center panel
        centerPanel.add(taskActionsPanel.getActionsPanel(), BorderLayout.NORTH);

        // Add the ScrollPane with the TreeTable to the center of the center panel
        centerPanel.add(rootScrollpane, BorderLayout.CENTER);
        // Add the scroll pane to the JFrame
        //rootFrame.add(rootScrollpane, BorderLayout.CENTER);
        // Add the center panel to the JFrame
        rootFrame.add(centerPanel, BorderLayout.CENTER);
        // TODO: Perhaps take these values from params to the constructor. Then we can save this to the file config and restore the correct size ?
        rootFrame.setSize(800,600);
        rootFrame.setVisible(true);
    }

    /*
     * Setters
     */

    /*
     * Getters
     */

    // Private because I dont want others with direct access to the model atm
    /**
     * Returns the TreeTableModel
     *
     * @return TreeTableModel
     */
    private jTaskViewTreeTableModel getModel() {
        return treeTableModel;
    }

    /**
     * Returns the TreeTable controlling the Task Table.
     *
     * @return TreeTable
     */
    private TreeTable getTaskTable() {
        return taskTreeTable;
    }

    /**
     * Returns the schedule handler used for scheduling reminders
     *
     * @return scheduleHandler - The scheduling handler
     */
    private scheduleHandler getScheduler() {
        return scheduleHandler;
    }

    /*
     * Other methods
     */

    /**
     * Takes the name of the column and the position in the view it should appear in.
     *
     * @param columnName - String representing a valid column in a TaskObj to show
     * @param position - The location it should appear in the table
     */
    public void showColumn(String columnName, int position) {
        getModel().addColumnByName(columnName, position);
    }

    /**
     * Takes the name of the column to hide.
     *
     * @param columnName - String representing a valid column in a TaskObj to hide
     * @param position - The location it should appear in the table
     */
    public void hideColumn(String columnName) {
        getModel().removeColumn(columnName);
    }

    /**
     * Takes the name of a column to toggle the visibility for. If the column is hidden in the view then the column will be shown in the specified position.
     * If the column is already showing in the view, it will be removed and the position parameter is ignored.
     *
     * @param columnName - String representing a valid column in a TaskObj to hide
     * @param position - The location it should appear in the table
     */
    private void toggleColumnVisible(String columnName, int position) {
        if(isColumnVisible(columnName)) {
            hideColumn(columnName);
        }
        else {
            if(position > getModel().getColumnCount()) position = getModel().getColumnCount();
            showColumn(columnName, position);
        }
    }

    /**
     * Returns true if the specified column is showing in the view, false otherwise
     *
     * @param columnName - String representing a valid column in a TaskObj to test
     * @return boolean - true if column on show, false otherwise
     */
    public boolean isColumnVisible(String columnName) {
        return getModel().isColumnVisible(columnName);
    }


    /*
     * From the jtvListener interface
     */

    /**
     * This is invoked when the File->Open menu item is clicked
     */
    public void jtvMenuBarFileOpen(String filename) {
        System.out.println(filename);
        // Store the file path that was opened (also used by save)
        taskFilePath = filename;
        // Make an XML reader object
        TaskObjXMLReader taskXMLReader = new TaskObjXMLReader(taskFilePath, this.root, getScheduler());
        // Connect to the data source
        taskXMLReader.connect();
        // Read the data source (this updates this.root which is passed in the constructor)
        taskXMLReader.read();
        // Tell the model that nodes have been inserted into the model
        getModel().nodesWereInserted();
        // Update the status bar
        itemsLabel.setText("Tasks: " + getModel().getRoot().getAllChildCount() + " total");
        itemStatsLabel.setText("Status: ? Overdue, ? Late, " + (this.root.getAllChildCount() - this.root.getStatsStartedCount()) + " inactive, " + this.root.getStatsCompletedCount() + " complete");
        // TODO: Let see whats what
        printDiag(this.root, true);
    }

    /**
     * This is invoked when the File->Save menu item is clicked
     */
    public void jtvMenuBarFileSave() {
        // TODO: Check the path in taskFilePath still exists
        TaskObjXMLWriter save = new TaskObjXMLWriter(taskFilePath, getModel().getRoot());
        save.connect();
        if(save.isConnected()) {
            save.write();
            save.disconnect();
        }
    }

    /**
     * This is invoked when the File->Save As menu item is clicked
     */
    public void jtvMenuBarFileSaveAs(String filename) {
        // Update the file path that is to be saved to
        taskFilePath = filename;
        // Fix the extention if the user didnt type it
        if( ! taskFilePath.endsWith(".tsk")) taskFilePath = taskFilePath + ".tsk";
        // Save the file
        jtvMenuBarFileSave();
    }

    /**
     * This is invoked when the File->Close menu item is clicked
     */
    public void jtvMenuBarFileClose() {
        // TODO: do reset of TreeTable, close file handles, cancel reminders etc etc....
        //getScheduler().cancelAll();
    }

    /**
     * This is invoked when the File->Quit menu item is clicked
     */
    public void jtvMenuBarFileQuit() {
        // Time to exit
        System.exit(0);
    }

    /**
     * This is invoked when the View->Creation Date menu item is clicked
     */
    public void jtvMenuBarViewCreationDate() {
        toggleColumnVisible("Creation Date",1);
    }

    /**
     * This is invoked when the View->Modification Date menu item is clicked
     */
    public void jtvMenuBarViewModificationDate() {
        toggleColumnVisible("Modification Date",2);
    }

    /**
     * This is invoked when the View->Description menu item is clicked
     */
    public void jtvMenuBarViewDescription() {
        toggleColumnVisible("Description",3);
    }

    /**
     * This is invoked when the New Task button is clicked
     */
    public void jtvTaskActionsNewTask() {
        TaskObj newTask = new TaskObj("New Task", getScheduler());
        getModel().insertNodeInto(newTask, getModel().getRoot(), getModel().getRoot().getChildCount());
        // Launch a new editor window
        jTaskEdit jte = new jTaskEdit(newTask);
        jte.addListener(this);
        jte.initGUI();
    }

    /**
     * This is invoked when a task is edited (enter is pressed, right click edit is used, edit menu item is used)
     */
    public void jtvTaskActionsEditTask() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jTaskEdit jte = new jTaskEdit(getSelectedTask());
                jte.addListener(jTaskView.this);
                jte.initGUI();
            }
        });
    }


    /**
     * This is invoked on the currently selected row when the Delete Task button is clicked
     */
    public void jtvTaskActionsDeleteTask() {
        jtvTaskActionsDeleteTask(getSelectedTask());
    }

    /**
     * Deletes the task in the parameter from its parent.
     *
     * @param task - TaskObj to remove
     */
    public void jtvTaskActionsDeleteTask(TaskObj task) {
        getModel().removeNodeFromParent(task);
    }

    /**
     * This is invoked when the New Sub Task button is clicked
     */
    public void jtvTaskActionsNewSubTask() {
        TaskObj selectedTask = getSelectedTask();
        TaskObj newTask = new TaskObj("New Sub Task", getScheduler());
        getModel().insertNodeInto(newTask, selectedTask, selectedTask.getChildCount());
        jTaskEdit jte = new jTaskEdit(newTask);
        jte.addListener(this);
        jte.initGUI();
    }

    /**
     * Takes a reference to the TaskObj being cut and removes it immediately from the TreeTable
     */
    public void jtvTaskActionsCutTask() {
        taskCutCopy = getSelectedTask();
        jtvTaskActionsDeleteTask(taskCutCopy);
    }

    /**
     * STUB
     * Performs a deep copy of the TaskObj on the selected row ready for paste action
     * STUB
     */
    public void jtvTaskActionsCopyTask() {
        //taskCutCopy = getSelectedTask();
        // TODO: I am going to need to implement a deep copy in TaskObj to get this working.
        System.err.println("Feature not implemented");
    }

    /**
     * Inserts the TaskObj referenced during a cut or copy operation into the ROOT of the TreeTable
     */
    public void jtvTaskActionsPasteTask() {
        if (taskCutCopy != null) getModel().insertNodeInto(taskCutCopy, getModel().getRoot(), getModel().getRoot().getChildCount());
        taskCutCopy = null;
    }

    /**
     * Inserts the TaskObj referenced during a cur or copy operation as a SubTask of the Task on the selected row.
     */
    public void jtvTaskActionsPasteAsSubTask() {
        TaskObj selectedTask = getSelectedTask();
        if (taskCutCopy != null) getModel().insertNodeInto(taskCutCopy, selectedTask, selectedTask.getChildCount());
        taskCutCopy = null;
    }

    /**
     * This is invoked when something external causes a change to the data that
     * requires the Task TreeTable be updated
     */
    public void jtvUpdateTaskTreeTable(TreePath path, String columnName) {
        // TODO: This is required because TaskObj.getPath() doesnt include the ROOT object
        TreePath p = new TreePath(getModel().getRoot());
        for(int i = 0; i < path.getPathCount(); i++) {
            p = p.pathByAddingChild(path.getPathComponent(i));
        }
        //System.out.println(p + " " + columnName + " " + getModel().findColumn(columnName));
        // Notify the model of the relevant change
        getModel().valueForPathChanged(p, getModel().findColumn(columnName));
    }

    /**
     * Gets the row under the pointer and selects it in the TreeTable
     */
    public void jtvSelectRowAtPoint(Component source) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int rowAtPoint = getTaskTable().rowAtPoint(SwingUtilities.convertPoint(source, new Point(0, 0), getTaskTable()));
                if (rowAtPoint >= 0) getTaskTable().setRowSelectionInterval(rowAtPoint, rowAtPoint);
             }
        });
    }

    private int getSelectedRow() {
        return getTaskTable().getSelectedRow();
    }

    private TaskObj getSelectedTask() {
        return (TaskObj) getModel().nodeForRow(taskTreeTable.convertRowIndexToModel(getSelectedRow()));
    }

    /**
     * Some really smelly code to print some diag stuff so I can see whats what.
     * This will be removed later
     * TODO: Of the information shown here, some of it will need to go into the Status bar, other stuff might live somewhere else
     */
    private static int printDiag(TaskObj tasks, boolean isRoot) {
        int i=0;
        int childCount=0;

        while (i<tasks.getChildCount()) {
            // Call ourself so we can have a deep look
            childCount+=printDiag(tasks.getChildAt(i), false);

            // TODO: This is diag and will need to be clean. Maybe I can add this to part of the UI later
            for(Map.Entry<String, String> unsupItem : tasks.getChildAt(i).getUnsupportedAttributes().entrySet()) {
                System.out.println("Unsupported items for ID: " + tasks.getChildAt(i).getID() + " - " + unsupItem.getKey() + " = " + unsupItem.getValue());
            }
            i = i + 1;
        }

        if(isRoot == true) {
            System.out.println("In root = " + tasks.getChildCount());
            System.out.println("All children count = " + (i+childCount));
        }
        return (i+childCount);
    }
}
