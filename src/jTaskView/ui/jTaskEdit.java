package jtaskview.ui;

import jtaskui.TaskObj;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class jTaskEdit implements ActionListener {
    private TaskObj task;
    private JFrame editFrame;
    private JTabbedPane tPane;
    private JButton closeEditFrame;
    private JPanel editPanel, descPanel, datesPanel, prereqsPanel, progressPanel, categoriesPanel, budgetPanel, effortPanel, notesPanel, attachmentsPanel, appearancePanel;
    private JLabel subjectLabel, descriptionLabel, creationDateLabel, creationDate, modificationDateLabel, modificationDate;
    private JTextArea subject, description;
    private JScrollPane descScroll;

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
        editFrame.setVisible(true);
        // Create the tabbed pane
        tPane = new JTabbedPane();

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
        // TODO: Get the full path correctly. ATM this only gets the parent and the current task as the path.
        JLabel taskPath = new JLabel(task.getParent().getSubject() + " -> " + task.getSubject());
        efSouthWest.add(taskPath, BorderLayout.WEST);

        // Create the close button and add to SOUTH EAST
        closeEditFrame = new JButton("Close");
        closeEditFrame.addActionListener(this);
        efSouthEast.add(closeEditFrame, BorderLayout.EAST);

        // Create panels for each tab
        prereqsPanel = new JPanel(new BorderLayout());
        progressPanel = new JPanel(new BorderLayout());
        categoriesPanel = new JPanel(new BorderLayout());
        budgetPanel = new JPanel(new BorderLayout());
        effortPanel = new JPanel(new BorderLayout());
        notesPanel = new JPanel(new BorderLayout());
        attachmentsPanel = new JPanel(new BorderLayout());
        appearancePanel = new JPanel(new BorderLayout());

        // Build each tab
        initDescTab();
        initDatesTab();

        // Add the panels and create the tabs
        tPane.addTab("Prerequisites", prereqsPanel);
        tPane.addTab("Progress", progressPanel);
        tPane.addTab("Categories", categoriesPanel);
        tPane.addTab("Budget", budgetPanel);
        tPane.addTab("Effort", effortPanel);
        tPane.addTab("Notes", notesPanel);
        tPane.addTab("Attachments", attachmentsPanel);
        tPane.addTab("Appearance", appearancePanel);

        // Add components to the frames panel
        editPanel.add(tPane, BorderLayout.CENTER);
        editPanel.add(efSouth, BorderLayout.SOUTH);
    }

    /**
     * Used internally to create the Description Tabbed Pane
     * Uses BorderLayout because GridBagLayout makes a massive mess when resizing
     */
    private void initDescTab() {
        // Create the main panel that will get added to the tPane
        descPanel = new JPanel(new BorderLayout(50, 20));

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
        subjectLabel = new JLabel("Subject");
        subject = new JTextArea(task.getSubject());
        // Description
        descriptionLabel = new JLabel("Description");
        description = new JTextArea(task.getDescription());
        description.setLineWrap(true);
        descScroll = new JScrollPane(description);
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
        creationDateLabel = new JLabel("Creation Date");
        creationDate = new JLabel(task.getFormattedCreationDateTime());
        // Modification date
        modificationDateLabel = new JLabel("Modification Date");
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

        // Add the Description panel to the tPane
        tPane.addTab("Description", descPanel);
    }

    public void initDatesTab() {

        datesPanel = new JPanel();
        LayoutManager lm = new LayoutManager(datesPanel);

        JLabel plannedStartDateLabel = new JLabel("Planned start date");
        JCheckBox plannedStartDateCheck = new JCheckBox();
        JTextArea plannedStartDate = new JTextArea(task.getFormattedPlannedStartDate());

        lm.addNext(plannedStartDateLabel);
        lm.addNext(plannedStartDateCheck);
        lm.addNext(plannedStartDate);

        JLabel dueDateLabel = new JLabel("Due date");
        JCheckBox dueDateCheck = new JCheckBox();
        JTextArea dueDate = new JTextArea(task.getFormattedDueDateTime());

        lm.addNextRow(dueDateLabel);
        lm.addNext(dueDateCheck);
        lm.addNext(dueDate);

        lm.addSeparator();

        JLabel actualStartDateLabel = new JLabel("Actual start date");
        JCheckBox actualStartDateCheck = new JCheckBox();
        JTextArea actualStartDate = new JTextArea(task.getFormattedActualStartDateTime());

        lm.addNext(actualStartDateLabel);
        lm.addNext(actualStartDateCheck);
        lm.addNext(actualStartDate);

        JLabel completionDateLabel = new JLabel("Completion date");
        JCheckBox completionDateCheck = new JCheckBox();
        JTextArea completionDate = new JTextArea(task.getFormattedCompletionDateTime());

        lm.addNextRow(completionDateLabel);
        lm.addNext(completionDateCheck);
        lm.addNext(completionDate);

        lm.addSeparator();

        JLabel reminderLabel = new JLabel("Reminder");
        JCheckBox reminderCheck = new JCheckBox();
        // TODO: FIX ME
        JTextArea reminderDate = new JTextArea(task.getFormattedActualStartDateTime());

        lm.addNext(reminderLabel);
        lm.addNext(reminderCheck);
        lm.addNext(reminderDate);

        lm.addSeparator();

        JLabel recurrenceLabel = new JLabel("Recurrence");

        lm.addNext(recurrenceLabel);

        // Add the Dates panel to the tPane
        tPane.addTab("Dates", datesPanel);
    }

    public void actionPerformed(ActionEvent e) {
        // Store the location of the event so we don't have to keep caling for it
        Object sourceEvent = e.getSource();

        if(sourceEvent == closeEditFrame) {
            editFrame.dispose();
        }
    }
}
