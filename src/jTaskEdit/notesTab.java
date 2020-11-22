package jTaskUI.jTaskEdit.tabs;

import jtaskui.TaskObj;

import jtaskview.ui.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class notesTab {
    private TaskObj task;

    public notesTab(TaskObj task) {
        this.task = task;
    }

    public JPanel buildPanel() {
        // Create the main panel that will get added to the tPane
        JPanel notesPanel = new JPanel();
        // LayoutManager helper
        LayoutManager lm = new LayoutManager(notesPanel);

        return notesPanel;
    }
}
