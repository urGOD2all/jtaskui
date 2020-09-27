package jtaskui;

// Import the TreeTable Model used for jTaskView
import jtaskui.view.jTaskViewTreeTableModel;
// Import the TreeTable
import TreeTable.TreeTable;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

import javax.swing.border.BevelBorder;
import java.awt.Dimension;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This code creates the main window and adds/manages the Task Table.
 */
public class jtaskView {
    // The main window with the Task Table on it
    private JFrame rootFrame;
    // A scroll pane for Task Table (ensures we can scroll if the nodes or nodes that are expanded go beyond the bounds)
    private JScrollPane rootScrollpane;
    // The Task Table
    private TreeTable taskTreeTable;
    // The Model of the Task Table
    private jTaskViewTreeTableModel treeTableModel;
    // TODO: This needs to be removed when we have proper show/hide support in the GUI. ATM its tested from doubleclicks which this controls
    int i;
    /*
     * Constructors
     */

    public jtaskView(TaskObj root) {
        // TODO: This needs to be removed when we have proper show/hide support in the GUI. ATM its tested from doubleclicks which this controls
        i = 1;
        // Make the frame and setup the layout
        rootFrame = new JFrame();
        rootFrame.setLayout(new BorderLayout());

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
        JLabel itemsLabel = new JLabel("Will display total number of items etc");
        JLabel itemStatsLabel = new JLabel("Will display number of overdue items etc");
        // Add the label to the panel
        statusPanel.add(itemsLabel, BorderLayout.WEST);
        statusPanel.add(itemStatsLabel, BorderLayout.EAST);

        // Make a new TreeTableModel and pass the root object
        treeTableModel = new jTaskViewTreeTableModel(root);
        // Add the TreeTableModel to the Treetable
        taskTreeTable = new TreeTable(treeTableModel);
        // Hide the root node
        taskTreeTable.setRootVisible(false);
        // Make sure all the chidlren have a expansion handles
        taskTreeTable.setShowsRootHandles(true);

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

    // TODO: Need to make sure that the model is able to put the column in the given position
    /**
     * Takes the name of the column and the position in the view it should appear in.
     *
     * @param columnName - String representing a valid column in a TaskObj to show
     * @param position - The location it should appear in the table
     */
    public void showColumn(String columnName, int position) {
        getModel().addColumnByName("Creation Date");
    }

    // TODO: Maybe change this to be by name ?
    /**
     * Takes a column position from the Table and hides it from the view
     *
     * @param position - The location of the column to hide
     */
    public void hideColumn(int position) {
        getModel().removeColumn(position);
    }
}
