package jtaskui.TreeTableModels;

import jtaskui.TaskObj;

import TreeTable.AbstractTreeTableModel;
import TreeTable.TreeTableModel;

import java.util.ArrayList;

public class notesTabTreeTableModel extends AbstractTreeTableModel {
    // Set the version ID for this class because it is serializable
    private static final long serialVersionUID = 1L;

    // This object will be the top of the TreeTable
    private TaskObj root;

    // This Arraylist will store all the names of the columns
    private ArrayList<String> columnNames;

    // TODO: This needs some fixing
    // Store the column classes
    private Class<?>[] columnTypes = { TreeTableModel.class, String.class };

    private notesTabTreeTableModel() {
        super();
        columnNames = new ArrayList<String>();
        this.addColumn("Note Subject");
        this.addColumn("Description");
        this.addColumn("Attachments");
        this.addColumn("Creation Date");
        this.addColumn("Modification Date");
    }

    public notesTabTreeTableModel(TaskObj root) {
        this();
        this.root = root;
    }

    /** 
     * Get the class of the column. The first column will always be the Subject of the Note and will always be expandable.
     * Other columns should return a correct Object type.
     */
    @Override
    public Class<?> getColumnClass(int column) {
        //TODO: fix me, when adding columns all columns should get added with a type or we will have to figure it out and add it to the array
        if (column == 0) return columnTypes[0];
        return columnTypes[1];
    }

    /**
     * Gets the value from the TaskObj for the specified column.
     *
     * @param node - A TaskObj that is in the table being inspected
     * @param columnIndex - The column index from the table that is being inspected
     *
     * @Return Object - Data for the column specified from the TaskObj
     */
// TODO: Check if need a column converstion here
    @Override
    public Object getValueAt(Object node, int columnIndex) {
        return this.getValueForColumn((TaskObj) node, columnIndex);
    }

   /**
    * Get the number of columns from the view
    */
   @Override
   public int getColumnCount() {
       return columnNames.size();
   }

    /** 
     * Returns the name of the column appearing in the view at column position index.
     *
     * @param index - the column in the view being queried
     *
     * @return the name of the column at position the given index in the view where the first column is column 0
     */
    @Override
    public String getColumnName(int index) {
        return columnNames.get(index);
    }

    /**
     * Returns the index of child in parent.
     *
     * @param parent - a node in the tree
     * @param child - the node we are interested in
     *
     * @return int - index of the child
     */
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        TaskObj parentTask = (TaskObj) parent;
        TaskObj childTask = (TaskObj) child;
        return parentTask.getSubNoteIndex(childTask);
    }

    /**
     * Returns whether the specified node is a leaf node.
     *
     * @param node - the node to check
     * @return true if the node is a leaf node
     */
    @Override
    public boolean isLeaf(Object node) {
        TaskObj task = (TaskObj) node;
        return ! task.hasSubNote();
    }

    /**
     * Get the number of child tasks from the parent
     *
     * @param parent - Object of the parent to get child count for
     * @return int - number of child objects
     */
    @Override
    public int getChildCount(Object parent) {
        // Cast to a TaskObj
        TaskObj parentTask = (TaskObj) parent;
        // Return the number of children
        return parentTask.getSubNoteCount();
    }

    /**
     * Gets the child/subtask from the parent at the given index
     *
     * @param parent - Object of the parent, will be cast to TaskObj
     * @param index - int position of the child to retrieve
     *
     * @return Object - Object of type TaskObj will be child/subtask at the given location index
     */
    @Override
    public Object getChild(Object parent, int index) {
        // Cast to a TaskObj
        TaskObj task = (TaskObj) parent;
        // Return the child at index
        return task.getSubNoteAt(index);
    }

    /**
     * Get the root object for this TreeTable
     *
     * @return TaskObj - root object. This TaskObj wont have any data, just subtasks
     */
    @Override
    public TaskObj getRoot() {
        return root;
    }

    /**
     * Add a column to the TreeTable. The column names are fixed because they must be something that the TaskObj supports hence this is private.
     * This model will setup all the relevant columns.
     *
     * @param col - String representing the column name. This will map to something in TaskObj
     */
    private void addColumn(String col) {
        columnNames.add(col);
    }

    /**
     * This method will get the value associated with the columnIndex only for the columns that are on show in the view.
     * The columnIndex will get translated by column name to the correct method call against the task.
     *
     * @param task - A TaskObj that the column data will be retieved for
     * @param columnIndex - the column position to retrieve
     */
    private Object getValueForColumn(TaskObj task, int columnIndex) {
        String colName = this.getColumnName(columnIndex);

        // Call the right method for the right column name
        switch (colName) {
            case "Note Subject":
                return task.getSubject();
            case "Description":
                return task.getDescription();
            case "Attachments":
                // TODO: Fix attachments
                return "Currently not supported";
            case "Creation Date":
                return task.getCreationDateTime();
            case "Modification Date":
                return task.getModificationDateTime();
            default:
                return "ERROR: Failed to getValueAt " + columnIndex + " for " + colName;
        }
    }
}
