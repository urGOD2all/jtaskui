package jtaskui.ui.swing.jNotesEdit;

import jtaskui.ui.swing.jNotesEdit.tabs.descTab;
import jtaskui.Task.NoteObj;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import javax.swing.border.EmptyBorder;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JButton;

public class jNotesEdit {
    // The note being worked on
    private NoteObj note;

    private JFrame editFrame;
    private JPanel editPanel;
    private JTabbedPane tPane;
    private JButton closeEditFrame;

    public jNotesEdit(NoteObj note) {
        // Store a reference to the note being worked on
        this.note = note;
        // Create the frame
        editFrame = new JFrame(note.getSubject());
        // Create a panel for the frame and set attributes (layout, border...)
        editPanel = new JPanel(new BorderLayout());
        editPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // Add the panel to the frame
        editFrame.add(editPanel);
        editFrame.setSize(850,480);
        editFrame.setVisible(true);
        // Create the tabbed pane
        tPane = new JTabbedPane();

        editFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Build the GUI
        initGUI();
    }

    private void initGUI() {
        // Create a panel for SOUTH position below tPane
        JPanel efSouth = new JPanel(new BorderLayout());
        efSouth.setBorder(new EmptyBorder(10, 10, 10, 10));
        // Create a panal for SOUTH EAST for the close button
        JPanel efSouthEast = new JPanel(new BorderLayout());
        efSouth.add(efSouthEast, BorderLayout.EAST);

        // Create the close button and add to SOUTH EAST
        closeEditFrame = new JButton("Close");
        closeEditFrame.addActionListener(e -> closeButton());
        efSouthEast.add(closeEditFrame, BorderLayout.EAST);

        // Build each tab
        descTab dt = new descTab(note);
        tPane.addTab("Description", dt.buildPanel());
        //tPane.addTab("Description", new JPanel());

        // TODO: Implement these panels
        // Add the panels and create the tabs
        tPane.addTab("Categories", new JPanel());
        tPane.addTab("Attachments", new JPanel());
        tPane.addTab("Appearance", new JPanel());

        // Add components to the frames panel
        editPanel.add(tPane, BorderLayout.CENTER);
        editPanel.add(efSouth, BorderLayout.SOUTH);
    }

    /**
     * This gets called when the close button is clicked.
     */
    private void closeButton() {
        // Close the GUI
        editFrame.dispose();
    }
}
