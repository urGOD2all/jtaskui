package jtaskui;

import jtaskui.Input.TaskObjXMLReader;

// TODO: This is only needed because of the diag printing code
import java.util.Map;

/* TODO:
   * Remove all code that handles XML from TaskObj and move to a XML processing class. This will hopefully allow for changing the output format easily
   * When will we save, auto like TaskCoach or manual or both
 */
public class jtaskui {

    public static void main(String[] args) {
        // TODO: Remove the need to pass a TaskObj to the jtaskView. Its not needed anymore
        TaskObj root = new TaskObj("ROOT");

        // TODO: This should be done as runable
        // Create the GUI
        jtaskView jtv = new jtaskView(root);

        // Lets take a look at whats what!
//        printDiag(root, true);
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
