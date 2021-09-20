package jtaskui.ui.swing.jTaskEdit.tabs;

import jtaskui.TaskObj;
import jtaskui.ui.swing.jTaskView.jtvListener;

import java.util.List;
import java.util.ArrayList;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;

public class descTab implements FocusListener {
    // Listeners to be notified when actions are performed
    private List<jtvListener> listeners = new ArrayList<jtvListener>();

    private TaskObj task;

    private JTextArea subject;
    private JTextArea description;
    private JSpinner priority;
    private JLabel modificationDate;

    public descTab(TaskObj task) {
        this.task = task;
    }

    public JPanel buildPanel() {
        // Create the main panel that will get added to the tPane
        JPanel descPanel = new JPanel(new BorderLayout(50, 20));

        // West panel will contain the labels, Center panel will contain the data
        BorderLayout westPanelLayout = new BorderLayout();
        BorderLayout centerPanelLayout = new BorderLayout();
        BorderLayout southPanelLayout = new BorderLayout();
        // Set a small gap between the content on the panels (seperates the labels/text areas from each other with a small vertical gap)
        JPanel westPanel = new JPanel(new BorderLayout(0, 10));
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        // The south panel will hold the same as above because more items are needed
        JPanel southPanel = new JPanel(new BorderLayout(17, 10));
        JPanel southWestPanel = new JPanel(new BorderLayout(0, 10));
        JPanel southCenterPanel = new JPanel(new BorderLayout(0, 10));

        // Populate the Description panel labels and data
        // Subject
        JLabel subjectLabel = new JLabel("Subject");
        subject = new JTextArea(task.getSubject());
        subject.addFocusListener(this);

        // Description
        JLabel descriptionLabel = new JLabel("Description");
        description = new JTextArea(task.getDescription());
        description.setLineWrap(true);
        description.addFocusListener(this);
        JScrollPane descScroll = new JScrollPane(description);

        // Priority
        // TODO: Implement update for the Task when value is changed
        JLabel priorityLabel = new JLabel("Priority");
        priority = new JSpinner(new javax.swing.SpinnerNumberModel());
        priority.setEditor(new javax.swing.JSpinner.NumberEditor(priority, "0"));
        if(task.getPriority() != null) {
            try {
                priority.setValue(Integer.parseInt(task.getPriority()));
            }   
            catch(NumberFormatException e) {
                priority.setValue(0);
            }   
        }   

        // Creation date
        JLabel creationDateLabel = new JLabel("Creation Date");
        JLabel creationDate = new JLabel(task.getFormattedCreationDateTime());
        // Modification date
        JLabel modificationDateLabel = new JLabel("Modification Date");
        modificationDate = new JLabel(task.getFormattedModificationDateTime());

        // Add the labels and the data to the correct locations
        westPanel.add(subjectLabel, BorderLayout.NORTH);
        centerPanel.add(subject, BorderLayout.NORTH);
        westPanel.add(descriptionLabel, BorderLayout.CENTER);
        centerPanel.add(descScroll, BorderLayout.CENTER);
        westPanel.add(southWestPanel, BorderLayout.SOUTH);
        centerPanel.add(southCenterPanel, BorderLayout.SOUTH);
        // SOUTH has a panel with two panels in it
        southPanel.add(southWestPanel, BorderLayout.WEST);
        southPanel.add(southCenterPanel, BorderLayout.CENTER);
        // south NORTH reserved for priority
        southWestPanel.add(priorityLabel, BorderLayout.NORTH);
        southCenterPanel.add(priority, BorderLayout.NORTH);
        southWestPanel.add(creationDateLabel, BorderLayout.CENTER);
        southCenterPanel.add(creationDate, BorderLayout.CENTER);
        southWestPanel.add(modificationDateLabel, BorderLayout.SOUTH);
        southCenterPanel.add(modificationDate, BorderLayout.SOUTH);

        // Add the west and center panels to the main panel
        descPanel.add(westPanel, BorderLayout.WEST);
        descPanel.add(centerPanel, BorderLayout.CENTER);
        descPanel.add(southPanel, BorderLayout.SOUTH);

        return descPanel;
    }

    public void focusGained(FocusEvent e) {
        // Do nothing...
    }

    /**
     * When focus is lost on a field, update the Task
     */
    public void focusLost(FocusEvent e) {
        Object source = e.getSource();
        if(source == subject && !task.getSubject().equals(subject.getText())) {
            task.setSubject(subject.getText());
            task.updateModificationDateTime();
            // Inform the UI of the update
            for (jtvListener aListener : listeners) aListener.jtvUpdateTaskTreeTable(task.getPath(), "Task Subject");
        }
        else if(source == description && !description.getText().equals(task.getDescription())) {
            task.setDescription(description.getText());
            task.updateModificationDateTime();
            for (jtvListener aListener : listeners) aListener.jtvUpdateTaskTreeTable(task.getPath(), "Description");
        }
        modificationDate.setText(task.getFormattedModificationDateTime());
        for (jtvListener aListener : listeners) aListener.jtvUpdateTaskTreeTable(task.getPath(), "Modification Date");
    }

    /**
     * Adds a listener to the listener list. These listeners will get notified when actions are performed.
     *
     * @param jtvListener - Class implementing the jtvListener interface
     */
    public void addListener(jtvListener listener) {
        listeners.add(listener);
    }
}
