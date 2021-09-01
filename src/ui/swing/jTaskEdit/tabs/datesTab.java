package jtaskui.ui.swing.jTaskEdit.tabs;

import jtaskui.TaskObj;
import jtaskview.ui.LayoutManager;

import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.zinternaltools.DateTimeChangeEvent;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeIncrement;

import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.time.LocalDateTime;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

public class datesTab {
    private TaskObj task;

    private JCheckBox plannedStartDateCheck, dueDateCheck, actualStartDateCheck, completionDateCheck, reminderCheck;
    private DateTimePicker plannedStartDate, dueDate, actualStartDate, completionDate, reminderDate;
    private TimePickerSettings timePickerSettings;
    private DateTimeFormatter format;

    /**
     * Constructor that configures the tab settings
     *
     * TaskObj - task to read
     */
    public datesTab(TaskObj task) {
        this.task = task;

        // Configure some TimePicker settings
        timePickerSettings = new TimePickerSettings();
        timePickerSettings.use24HourClockFormat();
        timePickerSettings.setDisplaySpinnerButtons(true);
        //timePickerSettings.generatePotentialMenuTimes(TimePickerSettings.TimeIncrement.FifteenMinutes, LocalTime.of(8,0), LocalTime.of(18,0));
        timePickerSettings.generatePotentialMenuTimes(TimePickerSettings.TimeIncrement.ThirtyMinutes, LocalTime.of(8,0), LocalTime.of(18,0));
    }

    /**
     * Builds all the components for the UI
     */
    public JPanel buildPanel() {
        // Panel to add to the tPane storing all these components
        JPanel datesPanel = new JPanel();
        // LayoutManager helper
        LayoutManager lm = new LayoutManager(datesPanel);

// TODO: Due date is not aligned with the other check boxes, why?

        // Planned start date section
        plannedStartDateCheck = buildCheckBox("Planned start date", task.hasPlannedStartDate());
        plannedStartDateCheck.addActionListener(e -> plannedStartDateChanged());

        plannedStartDate = buildDateTimePicker(task.getPlannedStartLocalDateTime(), task.hasPlannedStartDate());
        plannedStartDate.addDateTimeChangeListener(e -> plannedStartDateChanged());

        lm.addNext(plannedStartDateCheck);
        lm.addNext(plannedStartDate);

        // Due date section
        dueDateCheck = buildCheckBox("Due date", task.hasDueDate());
        dueDateCheck.addActionListener(e -> dueDateChanged());

        dueDate = buildDateTimePicker(task.getDueDate(), task.hasDueDate());
        dueDate.addDateTimeChangeListener(e -> dueDateChanged());

        lm.addNextRow(dueDateCheck);
        lm.addNext(dueDate);

        // Insert separator
        lm.addSeparator();

        // Actual start date section
        actualStartDateCheck = buildCheckBox("Actual start date", task.hasActualStartDate());
        actualStartDateCheck.addActionListener(e -> actualStartDateChanged());

        actualStartDate = buildDateTimePicker(task.getActualStartLocalDateTime(), task.hasActualStartDate());
        actualStartDate.addDateTimeChangeListener(e -> actualStartDateChanged());

        lm.addNextRow(actualStartDateCheck);
        lm.addNext(actualStartDate);

        // Completion date section
        completionDateCheck = buildCheckBox("Completion date", task.isComplete());
        completionDateCheck.addActionListener(e -> completionDateChanged());

        completionDate = buildDateTimePicker(task.getCompletionLocalDateTime(), task.isComplete());
        completionDate.addDateTimeChangeListener(e -> completionDateChanged());

        lm.addNextRow(completionDateCheck);
        lm.addNext(completionDate);

        // Insert separator
        lm.addSeparator();

        // Reminder section
        // TODO: FIX ME
        reminderCheck = new JCheckBox("Reminder", task.hasReminder());
        reminderCheck.setHorizontalTextPosition(SwingConstants.LEFT);
        reminderCheck.addActionListener(e -> reminderChanged());

        reminderDate = buildDateTimePicker(task.getReminderLocalDateTime(), task.hasReminder());
        reminderDate.addDateTimeChangeListener(e -> reminderChanged());

        lm.addNext(reminderCheck);
        lm.addNext(reminderDate);

        // Insert separator
        lm.addSeparator();

        // Reccurrence section
        JLabel recurrenceLabel = new JLabel("Recurrence");
        // TODO: Reccurrence is completly missing

        lm.addNext(recurrenceLabel);

        return datesPanel;

    }

    /**
     * Called when the Checkbox or the DatePicker is changed.
     * Enables/disables the DatePicker and sets the selected date and time on this TaskObj
     */
    private void plannedStartDateChanged() {
        // Toggle the DatePicker state dependard on the checkbox selection
        plannedStartDate.setEnabled(plannedStartDateCheck.isSelected());

        // If the check box is selected, set the date/time on this TaskObj - If its not selected, send a null to unset it
        if(plannedStartDateCheck.isSelected()) task.setPlannedStartDateTime(plannedStartDate.getDateTimePermissive());
        else task.setPlannedStartDateTime(null);
        task.updateModificationDateTime();
    }

    /**
     * Called when the Checkbox or the DatePicker is changed.
     * Enables/disables the DatePicker and sets the selected date and time on this TaskObj
     */
    private void dueDateChanged() {
        // Toggle the DatePicker state dependard on the checkbox selection
        dueDate.setEnabled(dueDateCheck.isSelected());

        // If the check box is selected, set the date/time on this TaskObj - If its not selected, send a null to unset it
        if(dueDateCheck.isSelected()) task.setDueDateTime(dueDate.getDateTimePermissive());
        else task.setDueDateTime(null);
        task.updateModificationDateTime();
    }

    /**
     * Called when the Checkbox or the DatePicker is changed.
     * Enables/disables the DatePicker and sets the selected date and time on this TaskObj
     */
    private void actualStartDateChanged() {
        // Toggle the DatePicker state dependard on the checkbox selection
        actualStartDate.setEnabled(actualStartDateCheck.isSelected());

        // If the check box is selected, set the date/time on this TaskObj - If its not selected, send a null to unset it
        if(actualStartDateCheck.isSelected()) task.setActualStartDateTime(actualStartDate.getDateTimePermissive());
        else task.setActualStartDateTime(null);
        task.updateModificationDateTime();
    }

    /**
     * Called when the Checkbox or the DatePicker is changed.
     * Enables/disables the DatePicker and sets the selected date and time on this TaskObj
     */
    private void completionDateChanged() {
        // Toggle the DatePicker state dependard on the checkbox selection
        completionDate.setEnabled(completionDateCheck.isSelected());

        // If the check box is selected, set the date/time on this TaskObj - If its not selected, send a null to unset it
        if(completionDateCheck.isSelected()) task.setCompletionDateTime(completionDate.getDateTimePermissive());
        else task.setCompletionDateTime(null);
        task.updateModificationDateTime();
    }

    // TODO: Implement this
    private void reminderChanged() {
        // Toggle the DatePicker state dependard on the checkbox selection
        reminderDate.setEnabled(reminderCheck.isSelected());

        // If the check box is selected, set the date/time on this TaskObj - If its not selected, send a null to unset it
        if(reminderCheck.isSelected()) task.setReminderDateTime(reminderDate.getDateTimePermissive());
        else task.setReminderDateTime(null);
        task.updateModificationDateTime();
    }

    /**
     * Create a JCheckBox component
     *
     * @param title - String text of the JCheckBox
     * @param enabeld - This is the initial enabled/disabled state of the JCheckBox componenet
     *
     * @return - an initialised JCheckBox
     */
    private JCheckBox buildCheckBox(String title, boolean enabled) {
        JCheckBox jbox = new JCheckBox(title, enabled);
        jbox.setHorizontalTextPosition(SwingConstants.LEFT);
        return jbox;
    }

    /**
     * Create a DatePicker component
     *
     * @param initialDateTime - LocalDateTime to set the initial values for the DatePicker and the TimePicker
     * @param enabled - This is the initial enabled/disabled state of the DateTimePicker componenet
     *
     * @return An initialised DateTimePicker componenet
     */
    private DateTimePicker buildDateTimePicker(LocalDateTime initialDateTime, boolean enabled) {
        DateTimePicker dtp = new DateTimePicker(null, timePickerSettings);
        dtp.setDateTimePermissive(initialDateTime);
        if (! enabled) dtp.setEnabled(false);
        return dtp;
    }
}
