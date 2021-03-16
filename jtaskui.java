package jtaskui;

import jtaskui.ui.swing.jTaskView.jTaskView;

import javax.swing.SwingUtilities;

/* TODO:
   * When will we save, auto like TaskCoach or manual or both
 */
public class jtaskui {

    public static void main(String[] args) {
        // Create the GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jTaskView jtv = new jTaskView();
                jtv.initGUI();
            }
        });
    }
}
