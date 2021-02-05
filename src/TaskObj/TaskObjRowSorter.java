package jtaskui;

import jtaskui.view.jTaskViewTreeTableModel;

import javax.swing.table.TableRowSorter;
import java.util.Comparator;

import javax.swing.tree.TreePath;

public class TaskObjRowSorter implements Comparator<TaskObj> {
    jTaskViewTreeTableModel treeTableModel;

    public TaskObjRowSorter(jTaskViewTreeTableModel treeTableModel) {
        this.treeTableModel = treeTableModel;
    }

    /**
     * Quickly build a TreePath by recursing here with task parents
     *
     * @param TaskObj - the task to build a TreePath for
     * @return TreePah - from the root to the task
     */
    private TreePath buildTreePath(TaskObj task) {
        TaskObj parent = task.getParent();
        // If the parent is null we are at the top of the tree so return a new TreePath with this element (the root) in it
        if(parent == null) return new TreePath(task);
        return buildTreePath(parent).pathByAddingChild(task);
    }

    /**
     * This method returns the index of the item in the TreePath that is common
     * to both task1 and task2 parameters. This can be used to ascertain what order
     * the two objects might be in depending on the position relative to each other
     * and if a common path is present.
     *
     * @param TreePath - first TreePath to compare for a common ancestor in the path
     * @param TreePath - second TreePath to compare for a common ancestor in the path
     * @return int - position of common ancestor in the path or 0 if no matches
     */
    private int getCommonPath(TreePath task1, TreePath task2) {
        // Variable that will hold the length of the shortest path, used to ensure that an index beyond the shortest path is not requested (the longer path wont be checked beyond this point)
        int shortestPathLen = 0;
        // Figure out between task1 and task2 which path is shortest
        if(task1.getPathCount() >= task2.getPathCount()) shortestPathLen = task2.getPathCount();
        else shortestPathLen = task1.getPathCount();

        // Loop each element in the array, starting with the longest path possible (whichever is shorter of the two paths) and go backwards until something matches
        for(int i = (shortestPathLen-1); i != 0; i--) {
            // TODO: String comparisions are not best here. It works but this should actually compare the objects so if this converted to TaskObj then the equals from TaskObj could be used
            // If this is true then there is a match so return this index
            if(task1.getPathComponent(i).toString() == task2.getPathComponent(i).toString()) return i;
        }
        // No match so return 0
        return 0;
    }

    /**
     * Compare two TaskObj objects to keep children together
     * and order by completion status.
     */
    // TODO: All the positional logic is here, just need to sort the Task state (completed, started etc)
    @Override
    public int compare(TaskObj task1, TaskObj task2) {
//        System.out.println("Asked to sort:");
//        System.out.println("    Task1: " + task1);
//        System.out.println("    Task2: " + task2);

        // If the parents are the same then these are both children of the same parent
        if(task1.getParent() == task2.getParent()) { 
            // TODO: Compare things like:
            //       is either/both task completed?
            //       is either/both task started?
            //       is either/both task not started?
            //       is either/both tasks overdue?
            //       Finally simple string compare
            return task1.toString().compareTo(task2.toString());
        }
        // task1 is parent of task2, task1 must come first so less than
        else if(task1 == task2.getParent()) return -1;
        // task2 is parent of task1, task2 come first so task1 is greater than
        else if(task2 == task1.getParent()) return 1;

        // Build TreePath objects for both tasks to be compared
        TreePath p1 = buildTreePath(task1);
        TreePath p2 = buildTreePath(task2);
        // Get the common ancestor position, if any
        int i = getCommonPath(p1, p2);

//        System.out.println("task1 and task2 share path " + i + ": " + p1.getPathComponent(i));
//        System.out.println(p1);
//        System.out.println(p2);

        // TODO: Probably not best to compare strings here either
        // Compare the next element (i+1) from the common ancestor
        if(p1.getPathCount() > (i+1) && p2.getPathCount() > (i+1)) return p1.getPathComponent(i+1).toString().compareTo(p2.getPathComponent(i+1).toString());
        else System.out.println("Cant compare i+1");

//        System.out.println("** No matches, return equal");
        return 0;
    }   
}
