package jtaskui;

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
                jtaskView jtv = new jtaskView();
                jtv.initGUI();
            }
        });
    }
}
