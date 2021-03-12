package jtaskui;

// Import the TreeTable Model used for jTaskView
import jtaskui.view.jTaskViewTreeTableModel;
// Import the TreeTable
import TreeTable.TreeTable;

// Import the Input classes
import jtaskui.Input.*;
// Import the Output classes
import jtaskui.Output.*;

import jtaskview.ui.jTaskEdit;

import javax.swing.SwingUtilities;

import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JButton;

import javax.swing.border.BevelBorder;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.table.TableRowSorter;

import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

// TODO: Remove this when the diag code is removed
import java.util.Map;

/**
 * This code creates the main window and adds/manages the Task Table.
 */
public class jtaskView {
    // File chooser
    private JFileChooser fileChooser;
    // File that was opened
    private String taskFilePath;
    // The main window with the Task Table on it
    private JFrame rootFrame;
    // The menu bar for the rootFrame
    private JMenuBar menuBar;
    // The menus for the rootFrame
    private JMenu file, view, viewColumns;
    // The menu items for the rootFrame menus
    private JMenuItem fileOpen, fileSave, fileSaveAs, fileClose, fileQuit, viewColumnsCreationDate, viewColumnsModificationDate, viewColumnsDescription;
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
    public jtaskView() {
        this.root = new TaskObj("ROOT");
    }

    /**
     * Constructor to take a prepopulated TaskObj.
     */
    @Deprecated
    public jtaskView(TaskObj root) {
        this.root = root;
    }

// TODO: This entire method is massive and needs to be broken down into components. This will make this easier to read and maintain
    public void initGUI() {
        // Make the frame and do some general setup
        rootFrame = new JFrame();
        rootFrame.setLayout(new BorderLayout());

        buildMenuBar();

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
        // Create a button panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        // Add some buttons to the button panel
        JButton newTask = new JButton();
// TODO: This works to read icons but the icons must be in the class path. I could also have them raw or in a jar. I need to consider how to start the application, do I have a script that will set the class path or should I put them in where the compiled code is an have some kind of monolith structure. The source structure is a mess anyway and that needs tidying too.
         try {
             Image img = ImageIO.read(getClass().getResource("icons/16x16/actions/newtask.png"));
             newTask.setIcon(new ImageIcon(img));
         } catch (Exception ex) {
             System.out.println(ex);
         }
        newTask.setPreferredSize(new Dimension(16, 16));

        buttonPanel.add(newTask, BorderLayout.WEST);
        // Add a button panel to the north of the center panel
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
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

    /**
     * Build the menu bar and add to the frame
     */
    private void buildMenuBar() {
        // Create the menu bar
        menuBar = new JMenuBar();

        // The "File" menu bar item
        file = new JMenu("File");
        fileOpen = new JMenuItem("Open");
          fileOpen.addActionListener(e -> menuFileOpen());
        fileSave = new JMenuItem("Save");
          fileSave.addActionListener(e -> menuFileSave());
        fileSave.setEnabled(false);
        fileSaveAs = new JMenuItem("Save As");
        fileSaveAs.addActionListener(e -> menuFileSaveAs());
        fileSaveAs.setEnabled(false);
        fileClose = new JMenuItem("Close");
        fileClose.addActionListener(e -> menuFileClose());
        fileClose.setEnabled(false);
        fileQuit = new JMenuItem("Quit");
        fileQuit.addActionListener(e -> menuFileQuit());
        // Build the File menu
        file.add(fileOpen);
        file.add(fileSave);
        file.add(fileSaveAs);
        file.add(fileClose);
        file.add(fileQuit);

        // The "View" menu bar item
        view = new JMenu("View");
        viewColumns = new JMenu("Columns");
        viewColumnsCreationDate = new JMenuItem("Creation Date");
        viewColumnsCreationDate.addActionListener(e -> toggleColumnVisible("Creation Date",1));
        viewColumnsModificationDate = new JMenuItem("Modification Date");
        viewColumnsModificationDate.addActionListener(e -> toggleColumnVisible("Modification Date",2));
        viewColumnsDescription = new JMenuItem("Description");
        viewColumnsDescription.addActionListener(e -> toggleColumnVisible("Description",3));
        // Build the View menu
        view.add(viewColumns);
        viewColumns.add(viewColumnsCreationDate);
        viewColumns.add(viewColumnsModificationDate);
        viewColumns.add(viewColumnsDescription);

        // Add the menus to the menu bar
        menuBar.add(file);
        menuBar.add(view);

        // Add the menu bar to the rootFrame
        rootFrame.add(menuBar, BorderLayout.NORTH);
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
     * Actions
     */

    /**
     * This is called when the File->Open menu item is clicked
     */
    private void menuFileOpen() {
        // do file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Task Coach files", "tsk"));
        int returnVal = fileChooser.showOpenDialog(rootFrame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            // Store the file path that was opened
            taskFilePath = fileChooser.getSelectedFile().getAbsoluteFile().toString();
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
            // If this is a merge we need to disable the save option so because it is not known which file to save to.
            if (fileOpen.getText() == "Merge") {
                fileSave.setEnabled(false);
            }
            else {
                fileSave.setEnabled(true);
            }
            // Update the item text to show future opens will merge
            fileOpen.setText("Merge");
            fileSaveAs.setEnabled(true);
            fileClose.setEnabled(true);
            // TODO: Let see whats what
            printDiag(this.root, true);
        }
    }

    private void menuFileSave() {
        TaskObjXMLWriter save = new TaskObjXMLWriter(taskFilePath, getModel().getRoot());
        save.connect();
        if(save.isConnected()) {
            save.write();
            save.disconnect();
        }
    }

    private void menuFileSaveAs() {
        // File chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Task Coach files", "tsk"));
        int returnVal = fileChooser.showSaveDialog(rootFrame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            // Store the file path that was opened
            taskFilePath = fileChooser.getSelectedFile().getAbsoluteFile().toString();
            // Fix the extention if the user didnt type it
            if( ! taskFilePath.endsWith(".tsk")) taskFilePath = taskFilePath + ".tsk";
            // Save to the specified file
            TaskObjXMLWriter save = new TaskObjXMLWriter(taskFilePath, getModel().getRoot());
            save.connect();
            if(save.isConnected()) {
                save.write();
                save.disconnect(); 
            }
            // Enable the save option again
            fileSave.setEnabled(true);
        }
    }

    private void menuFileClose() {
        // TODO: do reset of TreeTable, close file handles etc etc....
        fileOpen.setText("Open");
    }

    private void menuFileQuit() {
        // Time to exit
        System.exit(0);
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
