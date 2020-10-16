package jtaskui;

// Import the TreeTable Model used for jTaskView
import jtaskui.view.jTaskViewTreeTableModel;
// Import the TreeTable
import TreeTable.TreeTable;

// Import the Input classes
import jtaskui.Input.*;
// Import the Output classes
import jtaskui.Output.*;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

import javax.swing.border.BevelBorder;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This code creates the main window and adds/manages the Task Table.
 */
public class jtaskView implements ActionListener {
    // File chooser
    private JFileChooser fileChooser;
    // The main window with the Task Table on it
    private JFrame rootFrame;
    // The menu bar for the rootFrame
    private JMenuBar menuBar;
    // The menus for the rootFrame
    private JMenu file, view, viewColumns;
    // The menu items for the rootFrame menus
    private JMenuItem fileOpen, fileSave, fileClose, fileQuit, viewColumnsCreationDate, viewColumnsModificationDate, viewColumnsDescription;
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

    public jtaskView(TaskObj root) {
        this.root = root;
        // Make the frame and do some general setup
        rootFrame = new JFrame();
        rootFrame.setLayout(new BorderLayout());

        // Create the menu bar
        menuBar = new JMenuBar();

        // The "File" menu bar item
        file = new JMenu("File");
        fileOpen = new JMenuItem("Open");
        fileOpen.addActionListener(this);
        fileSave = new JMenuItem("Save");
        fileSave.addActionListener(this);
        fileClose = new JMenuItem("Close");
        fileClose.addActionListener(this);
        fileQuit = new JMenuItem("Quit");
        fileQuit.addActionListener(this);
        // Build the File menu
        file.add(fileOpen);
        file.add(fileSave);
        file.add(fileClose);
        file.add(fileQuit);

        // The "View" menu bar item
        view = new JMenu("View");
        viewColumns = new JMenu("Columns");
        // TODO: Need to sort out a list of columns somewhere, maybe TaskObj ?
        viewColumnsCreationDate = new JMenuItem("Creation Date");
        viewColumnsCreationDate.addActionListener(this);
        viewColumnsModificationDate = new JMenuItem("Modification Date");
        viewColumnsModificationDate.addActionListener(this);
        viewColumnsDescription = new JMenuItem("Description");
        viewColumnsDescription.addActionListener(this);
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
        // Add the TreeTableModel to the Treetable
        taskTreeTable = new TreeTable(treeTableModel);
        // Hide the root node
        taskTreeTable.setRootVisible(false);
        // Make sure all the chidlren have a expansion handles
        taskTreeTable.setShowsRootHandles(true);
/*
        // TODO: Fix this derpy code. It does work to return what row was clicked on and is currently used to test hiding/showing cols
        taskTreeTable.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent me) {
            if (me.getClickCount() == 2) {     // to detect doble click events
                int selectedRow = getTaskTable().getSelectedRow();
                int selectedColumn = getTaskTable().getSelectedColumn();
                if(i == 0) {
//                    TODO: The model is missing the ability to identify a row and get the value on it like a JTable would be able to do
//                    System.out.println(getModel().getValueAt(selectedRow, selectedColumn));
                    showColumn("Creation Date",1);
                    i = 1;
                }
                else {
                    hideColumn(1);
                    i = 0;
                }
                
            }   
         }   
      }); 
*/
        // Add the TreeTable to the scroll pane
        JScrollPane rootScrollpane = new JScrollPane(taskTreeTable);
        // Add the scroll pane to the JFrame
        rootFrame.add(rootScrollpane, BorderLayout.CENTER);
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

    /**
     * Action listener method
     */
    public void actionPerformed(ActionEvent e) {
        // Store the location of the event so we don't have to keep caling for it
        Object sourceEvent = e.getSource();

        if(sourceEvent == fileOpen) {
            // do file chooser
            fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Task Coach files", "tsk"));
            int returnVal = fileChooser.showOpenDialog(rootFrame);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                // Make an XML reader object
                TaskObjXMLReader taskXMLReader = new TaskObjXMLReader(fileChooser.getSelectedFile().getAbsoluteFile().toString(), this.root);
                // Connect to the data source
                taskXMLReader.connect();
                // Read the data source (this updates this.root which is passed in the constructor)
                taskXMLReader.read();
                // Tell the model that nodes have been inserted into the model
                getModel().nodesWereInserted();
                itemsLabel.setText("Tasks: " + this.root.getAllChildCount() + " total");
            }
        }
        else if (sourceEvent == fileSave) {
            // TODO: This needs to save back to the original file
            TaskObjXMLWriter save = new TaskObjXMLWriter("./test.tsk", getModel().getRoot());
            save.connect();
            if(save.isConnected()) {
                save.write();
                save.disconnect();
            }
        }
        else if (sourceEvent == fileClose) {
            // do reset of TreeTable, close file handles etc etc....
        }
        else if(sourceEvent == fileQuit) {
            // Time to exit
            rootFrame.dispose();
            System.exit(0);
        }
        else if(sourceEvent == viewColumnsCreationDate) {
            toggleColumnVisible("Creation Date",1);
        }
        else if(sourceEvent == viewColumnsModificationDate) {
            toggleColumnVisible("Modification Date",2);
        }
        else if(sourceEvent == viewColumnsDescription) {
            toggleColumnVisible("Description",3);
        }
    }
}
