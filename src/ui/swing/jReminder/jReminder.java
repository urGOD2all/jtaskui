package jtaskui.ui.swing.jReminder;

import jtaskui.TaskObj;
import jtaskui.ui.swing.jTaskEdit.jTaskEdit;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.ComponentOrientation;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;

import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.time.LocalDateTime;
import java.time.LocalDate;

public class jReminder {
    private TaskObj task;

    private JFrame reminderFrame;
    private JPanel reminderPanel;
    private JSpinner days, hours, minutes;

    public jReminder(TaskObj task) {
        this.task = task;

        // Create the frame
        reminderFrame = new JFrame(task.getSubject());
        // Create a panel for the frame and set att
        reminderPanel = new JPanel();
        reminderPanel.setLayout(new BoxLayout(reminderPanel, BoxLayout.Y_AXIS));
        reminderPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        // Add the panel to the frame
        reminderFrame.add(reminderPanel);

        // Call the snooze method to push the reminder forward and close the UI. Closing the UI this way is the same as pressing the OK button
        reminderFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        reminderFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                snoozeReminder();
            }
        });
    }

    public void initGUI() {
        /*
         * JPanel for the first row of components
         */
        JPanel row1 = new JPanel();
        // Set the layout horizontal
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        // Add this row to the main panel
        reminderPanel.add(row1);

        // Task label
        JLabel taskLabel = new JLabel("Task:");
        row1.add(taskLabel);
        // Put a 10 pixel fixed spacer between the Task label and the Button
        row1.add(Box.createRigidArea(new Dimension(100, 0)));
        // Task button (opens the Task when actioned)
        JButton taskButton = new JButton(task.getPrettyPath());
        // Open the task in the editor when this button is clicked
        taskButton.addActionListener(e->taskOpen());
        // Set the minimum size of this button to 100 pixels to allow it to shrink when the UI is resized
        taskButton.setMinimumSize(new Dimension(100, 24));
        // Add button to the row
        row1.add(taskButton);
        // Put a 10 pixel fixed spacer between the Task Button and the last button
        row1.add(Box.createRigidArea(new Dimension(10, 0)));
        // TODO: Fix this button, what is it/what does it do ?
        // TODO: This should be an icon only button
        JButton unknownButton = new JButton("UKB");
        // Set its minimum size
        unknownButton.setMinimumSize(new Dimension(58, 24));
        // Add the button to the row
        row1.add(unknownButton);
        // Glue the buttons in place by filling the rest of the row with Glue
        row1.add(Box.createHorizontalGlue());

        /*
         * JPanel for the second row of components
         */
        JPanel row2 = new JPanel();
        row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
        // Put a 10 pixel fixed spacer between the first row of components and this row
        reminderPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        reminderPanel.add(row2);

        JLabel reminderDateTimeLabel = new JLabel("Reminder Date/Time:");
        row2.add(reminderDateTimeLabel);
        // Put 10 pixels between the reminder label and the reminder data
        row2.add(Box.createRigidArea(new Dimension(15, 0)));

        // Get the reminder date/time
        JLabel reminderDateTime = new JLabel(task.getReminderLocalDateTime().toString());
        row2.add(reminderDateTime);
        row2.add(Box.createHorizontalGlue());

        /*
         * JPanel for the third row of components
         */
        JPanel row3 = new JPanel();
        row3.setLayout(new BoxLayout(row3, BoxLayout.X_AXIS));
        reminderPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        reminderPanel.add(row3);

        JLabel snoozeLabel = new JLabel("Snooze:");
        row3.add(snoozeLabel);
        row3.add(Box.createRigidArea(new Dimension(89, 0)));

        // Days
        JLabel daysLabel = new JLabel("Days:");
        row3.add(daysLabel);
        days = new JSpinner(new javax.swing.SpinnerNumberModel(1, 0, 3650, 1));
        days.setEditor(new javax.swing.JSpinner.NumberEditor(days, "0"));
        days.setPreferredSize(new Dimension(50, 24));
        days.setMaximumSize(new Dimension(100, 24));
        row3.add(days);

        // Hours
        row3.add(Box.createRigidArea(new Dimension(10, 0)));
        JLabel hoursLabel = new JLabel("Hours:");
        row3.add(hoursLabel);
        hours = new JSpinner(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        hours.setEditor(new javax.swing.JSpinner.NumberEditor(hours, "0"));
        hours.setPreferredSize(new Dimension(50, 24));
        hours.setMaximumSize(new Dimension(100, 24));
        row3.add(hours);

        // Minutes
        row3.add(Box.createRigidArea(new Dimension(10, 0)));
        JLabel minutesLabel = new JLabel("Minutes:");
        row3.add(minutesLabel);
        minutes = new JSpinner(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        minutes.setEditor(new javax.swing.JSpinner.NumberEditor(minutes, "0"));
        minutes.setPreferredSize(new Dimension(50, 24));
        minutes.setMaximumSize(new Dimension(100, 24));
        row3.add(minutes);
        row3.add(Box.createHorizontalGlue());

        /*  
         * JPanel for the fouth row of components
         */
        JPanel row4 = new JPanel();
        row4.setLayout(new BoxLayout(row4, BoxLayout.X_AXIS));
        reminderPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        reminderPanel.add(row4);

        row4.add(Box.createRigidArea(new Dimension(100, 0)));
        // TODO: Make this configurable / remember what was picked. This is not TaskCoach behaviour but it makes more sense!
        // TODO: Make this do something
        // TODO: Think about the wording of this. its not clear what happens if the reminder time is 3 days ago and +1 days is specified. The text is unclear whether that make the reminder 2 days ago or +1 on today (its the latter)
        JCheckBox addToCheckbox = new JCheckBox("Add the specified amount of time to the current time", true);
        addToCheckbox.setEnabled(false);
        row4.add(addToCheckbox);
        row4.add(Box.createHorizontalGlue());

        /*  
         * JPanel for the fifth row of components
         */
        JPanel row5 = new JPanel();
        row5.setLayout(new BoxLayout(row5, BoxLayout.X_AXIS));
//        reminderPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        reminderPanel.add(row5);

        row5.add(Box.createRigidArea(new Dimension(100, 0)));
        JCheckBox defaultReminderTimeCheckbox = new JCheckBox("Make this the default snooze time for future reminders", false);
        defaultReminderTimeCheckbox.setEnabled(false);
        row5.add(defaultReminderTimeCheckbox);
        row5.add(Box.createHorizontalGlue());

        /*  
         * JPanel for the sixth row of components
         */
        JPanel row6 = new JPanel();
        row6.setLayout(new BoxLayout(row6, BoxLayout.X_AXIS));
        reminderPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        reminderPanel.add(row6);

        // TODO: This button should contain an image as well as text
        // OK button, pushes the reminder forward by the specified time and closes the reminder
        JButton okButton = new JButton("OK");
        // Open the task in the editor when this button is clicked
        okButton.addActionListener(e->snoozeReminder());
        row6.add(okButton);

        row6.add(Box.createRigidArea(new Dimension(10, 0)));
        JButton markCompleted = new JButton("Mark task completed");
        markCompleted.addActionListener(e->markCompleted());
        row6.add(markCompleted);

        row6.add(Box.createHorizontalGlue());

//        reminderPanel.add(Box.createVeritcalGlue());
        // Set the appropriate initial size of this reminder based on the size of the first row (likely to always be the longest) and add 30 pixels.
        reminderFrame.setSize(new Dimension(30+(int)row1.getPreferredSize().getWidth(), 210));
        // Set the minimum size of all reminder windows so that all components will always be visible
        reminderFrame.setMinimumSize(new Dimension(460, 210));
        // Center the reminder
        reminderFrame.setLocationRelativeTo(null);
        // Show the UI
        reminderFrame.setVisible(true);
    }

    /**
     * Opens the task in the reminder for editing.
     */
    private void taskOpen() {
        // Before opening the Task for editing, push the reminder date/time forward
        snoozeReminder();
        // Invoke a thread for the edit frame
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jTaskEdit jte = new jTaskEdit(task);
                jte.initGUI();
            }
        });
    }

    /**
     * Moves the reminder date/time forward and closes the reminder UI.
     */
    private void snoozeReminder() {
        try {
            // Set the date part of the reminder to today
            LocalDateTime dateAdjustedReminder = LocalDate.now().atTime(task.getReminderLocalDateTime().toLocalTime());
            //LocalDateTime dateAdjustedReminder = LocalDate.now().atTime(currentReminder.getHour(), currentReminder.getMinute());
            // Make the adjustments
            dateAdjustedReminder = dateAdjustedReminder.plusDays(Long.parseLong(days.getValue().toString()));
            dateAdjustedReminder = dateAdjustedReminder.plusHours(Long.parseLong(hours.getValue().toString()));
            dateAdjustedReminder = dateAdjustedReminder.plusMinutes(Long.parseLong(minutes.getValue().toString()));
            // Set the new reminder date/time
            task.setReminderDateTime(dateAdjustedReminder);
        }
        // Print the error to stderr and open the Task for the user to correct
        catch (NumberFormatException e) {
            e.printStackTrace();
            taskOpen();
        }
        reminderFrame.dispose();
    }

    private void markCompleted() {
        // Set the completion date to complete the Task
        task.setCompletionDateTime(LocalDateTime.now());
        // Close the reminder frame
        reminderFrame.dispose();
    }
}
