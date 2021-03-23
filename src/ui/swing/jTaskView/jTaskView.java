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

import javax.swing.SwingUtilities;

import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

import javax.swing.border.BevelBorder;
import java.awt.Dimension;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.table.TableRowSorter;

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

    /*
     * Constructors
     */

    /**
     * Default constructor
     */
    public jTaskView() {
        this.root = new TaskObj("ROOT");
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
                // Get the node for this row from the model using view indexes incase of sorting
                TaskObj rowData = (TaskObj) getModel().nodeForRow(convertRowIndexToModel(row));
                // TODO: Need to have overdue items in red and due today in orange
                // Depending on the state of the task, change the text color
                if (rowData.isComplete()) c.setForeground(Color.GREEN);
                else if (rowData.isStarted()) c.setForeground(Color.BLUE);
                else c.setForeground(Color.GRAY);

                // Return the modified component
                return c;
            }
        };
        // Hide the root node
        taskTreeTable.setRootVisible(false);
        // Make sure all the chidlren have a expansion handles
        taskTreeTable.setShowsRootHandles(true);

        // Set the columns sortable
        taskTreeTable.setAutoCreateRowSorter(true);
        // Get the RowSorter and set the custom comparator so we get proper sorting
        TableRowSorter treeTableRowSorter = (TableRowSorter) taskTreeTable.getRowSorter();
        // TODO: Need to fix the warning when setting this
        treeTableRowSorter.setComparator(0, new TaskObjRowSorter(treeTableModel));

        // TODO: Should this be here ?
        taskTreeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                // Detect double click events
                if (me.getClickCount() == 2) {
                    // Get the selected row in the view
                    int selectedRow = getTaskTable().getSelectedRow();
                    // get the TaskObj powering that row by converted the row selected in the view back to the model order
                    TaskObj selectedTask = (TaskObj) getModel().nodeForRow(taskTreeTable.convertRowIndexToModel(selectedRow));

                    // Invoke a thread for the edit frame
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            jTaskEdit jte = new jTaskEdit(selectedTask);
                            jte.initGUI();
                        }
                    });
                }
            }
        }); 

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
        TaskObjXMLReader taskXMLReader = new TaskObjXMLReader(taskFilePath, this.root);
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
        // TODO: do reset of TreeTable, close file handles etc etc....
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
        // TODO: Implement new task
        System.out.println("New task");
    }

    public void jtvTaskActionsNewSubTask() {
        // TODO: Implement new sub task
        System.out.println("New sub task");
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
            i = i + 1;
            // Call ourself so we can have a deep look
            childCount+=printDiag(tasks.getChildAt(i), false);

            // TODO: This is diag and will need to be clean. Maybe I can add this to part of the UI later
            for(Map.Entry<String, String> unsupItem : tasks.getChildAt(i).getUnsupportedAttributes().entrySet()) {
                System.out.println("Unsupported items for ID: " + tasks.getChildAt(i).getID() + " - " + unsupItem.getKey() + " = " + unsupItem.getValue());
            }
        }

        if(isRoot == true) {
            System.out.println("In root = " + tasks.getChildCount());
            System.out.println("All children count = " + (i+childCount));
        }
        return (i+childCount);
    }
}
