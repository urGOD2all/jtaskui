package jtaskui.ui.swing.jTaskEdit.tabs;

import jtaskui.TaskObj;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;

public class descTab {
    private TaskObj task;

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
        JTextArea subject = new JTextArea(task.getSubject());
        // Description
        JLabel descriptionLabel = new JLabel("Description");
        JTextArea description = new JTextArea(task.getDescription());
        description.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(description);
        // Priority
        JLabel priorityLabel = new JLabel("Priority");
        JSpinner priority = new JSpinner(new javax.swing.SpinnerNumberModel());
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
        JLabel modificationDate = new JLabel(task.getFormattedModificationDateTime());

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
}
