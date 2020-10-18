package jtaskui;

import javax.swing.SwingUtilities;

// TODO: This is only needed because of the diag printing code
import java.util.Map;

/* TODO:
   * When will we save, auto like TaskCoach or manual or both
 */
public class jtaskui {

    public static void main(String[] args) {
        // Create the GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jtaskView jtv = new jtaskView();
                jtv.initGUI();
            }
        });

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
