package jtaskui;

import jtaskui.view.jTaskViewTreeTableModel;

import javax.swing.table.TableRowSorter;
import java.util.Comparator;

import javax.swing.tree.TreePath;

public class TaskObjRowSorter implements Comparator<TaskObj> {
    jTaskViewTreeTableModel treeTableModel;

    private Object root;

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
        if(parent == null) { root = task; return new TreePath(task);}
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
        // Build TreePath objects for both tasks to be compared
        TreePath path1 = buildTreePath(task1);
        TreePath path2 = buildTreePath(task2);
        // Get the common ancestor position, if any
        int i = getCommonPath(path1, path2);
        // Debuging flags to help solve sorting issues
        boolean debug = false;
        //System.out.println(path1.getPathComponent(1).toString());
        //if (path1.getPathCount() >= 3 && path1.getPathComponent(2).toString().compareTo("Some Test String") == 0) debug = true;
        //if (path2.getPathCount() >= 3 && path2.getPathComponent(2).toString().compareTo("Some Test String") == 0) debug = true;
        int path1Len = path1.getPathCount();
        int path2Len = path2.getPathCount();
        TaskObj path1NextFromCommon;
        TaskObj path2NextFromCommon;
        if(path1Len > (i+1)) path1NextFromCommon = (TaskObj) path1.getPathComponent(i+1);
        else path1NextFromCommon = task1;
        if(path2Len > (i+1)) path2NextFromCommon = (TaskObj) path2.getPathComponent(i+1);
        else path2NextFromCommon = task2;

        if(debug) {
            System.out.println("----------");
            System.out.println("Task1: " + task1);
            System.out.println("Next Common: " + path1NextFromCommon);
            System.out.println("Path: " + path1);
            System.out.println("----------");
            System.out.println("Task2: " + task2);
            System.out.println("Next Common: " + path2NextFromCommon);
            System.out.println("Path: " + path2);
            System.out.println("----------");
            System.out.println();
        }

        // Check that both tasks and next common ancestors are the same as tasks
        if(path1NextFromCommon == task1 && path2NextFromCommon == task2) {
            if(debug) System.out.println("====== Both common are the same as task");

            // If these tasks are siblings
            if(task1.getParent() == task2.getParent()) {
                if(debug) System.out.println("====== parents are the same");

                return path1NextFromCommon.compareTo(path2NextFromCommon);
            }
            // task1 is the parent of task2
            else if(task1 == task2.getParent()) {
                if(debug) System.out.println("^^^^^^^^ task1 is the parent of task2, returning -1");
                return -1;
            }
            // task2 is the parent of task1
            else if(task2 == task1.getParent()) {
                if(debug) System.out.println("vvvvvvvv task2 is the parent of task1, returning 1");
                return 1;
            }
            else {
               //TODO: Does anything cause this ?
               System.out.println("====== First if " + task1 + " --- " + task2);
            }
        }
        // One or both of the ancestors is not the same as the task
        else {
            if(debug) System.out.println("<<<>>>======= One of common are not the same as task");

            // Check that the parent of path1NextFromCommon is the same as path2NextFromCommon, this ensures the children stay with parents
            if(path1NextFromCommon.getParent() == path2NextFromCommon) {
                if(debug) System.out.println("<<<>>>======= path2 common is parent of path1 common, returning 1");
                return 1;
            }
            else if(path2NextFromCommon.getParent() == path1NextFromCommon) {
                if(debug) System.out.println("<<<>>>======= path1 common is parent of path2 common, returning -1");
                return -1;
            }

            return path1NextFromCommon.compareTo(path2NextFromCommon);
        }

        // This means there is missing logic and could result in lost children
        System.out.println("SHOULD NOT BE HERE");

        if(debug) System.out.println("** No matches, return equal");
        return 0;
    }   
}
