package jtaskui.TreeTableModels;

import jtaskui.Task.NoteObj;
import jtaskui.util.DateUtil;

import TreeTable.AbstractTreeTableModel;
import TreeTable.TreeTableModel;

import java.util.ArrayList;

public class notesTabTreeTableModel extends AbstractTreeTableModel {
    // Set the version ID for this class because it is serializable
    private static final long serialVersionUID = 1L;

    // This object will be the top of the TreeTable
    private NoteObj root;

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

    public notesTabTreeTableModel(NoteObj root) {
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
     * Gets the value from the NoteObj for the specified column.
     *
     * @param node - A NoteObj that is in the table being inspected
     * @param columnIndex - The column index from the table that is being inspected
     *
     * @Return Object - Data for the column specified from the NoteObj
     */
// TODO: Check if need a column converstion here
    @Override
    public Object getValueAt(Object node, int columnIndex) {
        return this.getValueForColumn((NoteObj) node, columnIndex);
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
        NoteObj parentTask = (NoteObj) parent;
        NoteObj childTask = (NoteObj) child;
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
        NoteObj note = (NoteObj) node;
        return ! note.hasSubNote();
    }

    /**
     * Get the number of child notes from the parent
     *
     * @param parent - Object of the parent to get child count for
     * @return int - number of child objects
     */
    @Override
    public int getChildCount(Object parent) {
        // Cast to a NoteObj
        NoteObj parentNote = (NoteObj) parent;
        // Return the number of children
        return parentNote.getSubNoteCount();
    }

    /**
     * Gets the child/sub-note from the parent at the given index
     *
     * @param parent - Object of the parent, will be cast to NoteObj
     * @param index - int position of the child to retrieve
     *
     * @return Object - Object of type NoteObj will be child/sub-note at the given location index
     */
    @Override
    public Object getChild(Object parent, int index) {
        // Cast to a NoteObj
        NoteObj note = (NoteObj) parent;
        // Return the child at index
        return note.getSubNoteAt(index);
    }

    /**
     * Get the root object for this TreeTable
     *
     * @return NoteObj - root object.
     */
    @Override
    public NoteObj getRoot() {
        return root;
    }

    /**
     * Add a column to the TreeTable. The column names are fixed because they must be something that the NoteObj supports hence this is private.
     * This model will setup all the relevant columns.
     *
     * @param col - String representing the column name. This will map to something in NoteObj
     */
    private void addColumn(String col) {
        columnNames.add(col);
    }

    /**
     * This method will get the value associated with the columnIndex only for the columns that are on show in the view.
     * The columnIndex will get translated by column name to the correct method call against the note.
     *
     * @param note - A NoteObj that the column data will be retieved for
     * @param columnIndex - the column position to retrieve
     */
    private Object getValueForColumn(NoteObj note, int columnIndex) {
        String colName = this.getColumnName(columnIndex);

        // Call the right method for the right column name
        switch (colName) {
            case "Note Subject":
                return note.getSubject();
            case "Description":
                return note.getDescription();
            case "Attachments":
                // TODO: Fix attachments
                return "Currently not supported";
            case "Creation Date":
                return DateUtil.formatToString(note.getCreationLocalDateTime(), DateUtil.DISPLAY_FORMATTER, true);
            case "Modification Date":
                if (note.hasModificationDate()) return DateUtil.formatToString(note.getModificationLocalDateTime(), DateUtil.DISPLAY_FORMATTER, true);
                else return "N/A";
            default:
                return "ERROR: Failed to getValueAt " + columnIndex + " for " + colName;
        }
    }
}
