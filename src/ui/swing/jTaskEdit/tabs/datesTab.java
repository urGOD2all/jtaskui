package jtaskui.ui.swing.jTaskEdit.tabs;

import jtaskui.TaskObj;

import jtaskview.ui.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;

public class datesTab {
    private TaskObj task;

    public datesTab(TaskObj task) {
        this.task = task;
    }

    public JPanel buildPanel() {
        // Panel to add to the tPane storing all these components
        JPanel datesPanel = new JPanel();
        // LayoutManager helper
        LayoutManager lm = new LayoutManager(datesPanel);

        // TODO: All the check boxes here need to check if the attribute is set and tick if it is
        // TODO: All the text boxes where there are no attributes set should be disabled

        // Planned start date section
        JLabel plannedStartDateLabel = new JLabel("Planned start date");
        JCheckBox plannedStartDateCheck = new JCheckBox();
        JTextArea plannedStartDate = new JTextArea(task.getFormattedPlannedStartDate());

        lm.addNext(plannedStartDateLabel);
        lm.addNext(plannedStartDateCheck);
        lm.addNext(plannedStartDate);

        // Due date section
        JLabel dueDateLabel = new JLabel("Due date");
        JCheckBox dueDateCheck = new JCheckBox();
        JTextArea dueDate = new JTextArea(task.getFormattedDueDateTime());

        lm.addNextRow(dueDateLabel);
        lm.addNext(dueDateCheck);
        lm.addNext(dueDate);

        // Insert separator
        lm.addSeparator();

        // Actual start date section
        JLabel actualStartDateLabel = new JLabel("Actual start date");
        JCheckBox actualStartDateCheck = new JCheckBox();
        JTextArea actualStartDate = new JTextArea(task.getFormattedActualStartDateTime());

        lm.addNext(actualStartDateLabel);
        lm.addNext(actualStartDateCheck);
        lm.addNext(actualStartDate);

        // Completion date section
        JLabel completionDateLabel = new JLabel("Completion date");
        JCheckBox completionDateCheck = new JCheckBox();
        JTextArea completionDate = new JTextArea(task.getFormattedCompletionDateTime());

        lm.addNextRow(completionDateLabel);
        lm.addNext(completionDateCheck);
        lm.addNext(completionDate);

        // Insert separator
        lm.addSeparator();

        // Reminder section
        JLabel reminderLabel = new JLabel("Reminder");
        JCheckBox reminderCheck = new JCheckBox();
        // TODO: FIX ME
        JTextArea reminderDate = new JTextArea(task.getFormattedActualStartDateTime());

        lm.addNext(reminderLabel);
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
}
