package jtaskui.ui.swing.jTaskEdit;

import jtaskui.TaskObj;
import jtaskui.ui.swing.jTaskEdit.tabs.descTab;
import jtaskui.ui.swing.jTaskEdit.tabs.datesTab;
import jtaskui.ui.swing.jTaskEdit.tabs.notesTab;
import jtaskui.ui.swing.jTaskView.jtvListener;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class jTaskEdit implements ActionListener {
    // Listeners to be notified when actions are performed
    private List<jtvListener> listeners = new ArrayList<jtvListener>();

    private TaskObj task;
    private JFrame editFrame;
    private JTabbedPane tPane;
    private JButton closeEditFrame;
    private JPanel editPanel;

    private descTab dt;

    public jTaskEdit(TaskObj task) {
        // Store a reference to the TaskObj with all the information in it
        this.task = task;
        // Create the frame
        editFrame = new JFrame(task.getSubject());
        // Create a panel for the frame and set attributes (layout, border...)
        editPanel = new JPanel(new BorderLayout());
        editPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // Add the panel to the frame
        editFrame.add(editPanel);
        editFrame.setSize(1200,480);
        // Create the tabbed pane
        tPane = new JTabbedPane();
        // Initialise the descTab
        dt = new descTab(task);

        editFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void initGUI() {
        // Create a panel for SOUTH position below tPane
        JPanel efSouth = new JPanel(new BorderLayout());
        efSouth.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel efSouthWest = new JPanel(new BorderLayout());
        JPanel efSouthEast = new JPanel(new BorderLayout());
        efSouth.add(efSouthWest, BorderLayout.WEST);
        efSouth.add(efSouthEast, BorderLayout.EAST);

        // Create the path label and add to the SOUTH WEST
        JLabel taskPath = new JLabel(task.getPrettyPath());
        efSouthWest.add(taskPath, BorderLayout.WEST);

        // Create the close button and add to SOUTH EAST
        closeEditFrame = new JButton("Close");
        closeEditFrame.addActionListener(this);
        efSouthEast.add(closeEditFrame, BorderLayout.EAST);

        // Build each tab
        tPane.addTab("Description", dt.buildPanel());
        datesTab dat = new datesTab(task);
        tPane.addTab("Dates", dat.buildPanel());

        // Add the panels and create the tabs
        tPane.addTab("Prerequisites", new JPanel());
        tPane.addTab("Progress", new JPanel());
        tPane.addTab("Categories", new JPanel());
        tPane.addTab("Budget", new JPanel());
        tPane.addTab("Effort", new JPanel());
        notesTab nt = new notesTab(task);
        tPane.addTab("Notes", nt.buildPanel());
        tPane.addTab("Attachments", new JPanel());
        tPane.addTab("Appearance", new JPanel());

        // Add components to the frames panel
        editPanel.add(tPane, BorderLayout.CENTER);
        editPanel.add(efSouth, BorderLayout.SOUTH);
        // Make the frame visible
        editFrame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        // Store the location of the event so we don't have to keep caling for it
        Object sourceEvent = e.getSource();

        if(sourceEvent == closeEditFrame) {
            editFrame.dispose();
        }
    }

    /**
     * Adds a listener to the listener list. These listeners will get notified when actions are performed.
     *
     * @param jtvListener - Class implementing the jtvListener interface
     */
    public void addListener(jtvListener listener) {
        listeners.add(listener);
        dt.addListener(listener);
    }
}
