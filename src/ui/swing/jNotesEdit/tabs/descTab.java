package jtaskui.ui.swing.jNotesEdit.tabs;

import jtaskui.Task.NoteObj;
import jtaskui.ui.swing.jTaskEdit.tabs.notesListener;

import java.util.List;
import java.util.ArrayList;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class descTab implements FocusListener {
    // Listeners to be notified when actions are performed
    private List<notesListener> listeners = new ArrayList<notesListener>();

    private NoteObj note;

    private JTextArea subject;
    private JTextArea description;
    private JLabel modificationDate;

    public descTab(NoteObj note) {
        // Store a reference to the note being worked on
        this.note = note;
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
        subject = new JTextArea(note.getSubject());
        subject.addFocusListener(this);

        // Description
        JLabel descriptionLabel = new JLabel("Description");
        description = new JTextArea(note.getDescription());
        description.setLineWrap(true);
        description.addFocusListener(this);
        JScrollPane descScroll = new JScrollPane(description);

        // Creation date
        JLabel creationDateLabel = new JLabel("Creation Date");
        JLabel creationDate = new JLabel(note.getFormattedCreationDateTime());
        // Modification date
        JLabel modificationDateLabel = new JLabel("Modification Date");
        modificationDate = new JLabel(note.getFormattedModificationDateTime());

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

        // Add the creation and modification labels
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

    /**
     * When focus is lost on a field, update the Note
     */
    public void focusLost(FocusEvent e) {
        // Get the event source
        Object source = e.getSource();
        // If the source is subject and the text has changed
        if(source == subject && !note.getSubject().equals(subject.getText())) {
            // Update the text on the NoteObj
            note.setSubject(subject.getText());
            // Update the modification timestamp
            note.updateModificationDateTime();
            // Update the Notes TreeTable on the jTaskEdit notesTab
            for (notesListener aListener : listeners) aListener.jteNoteUpdateNoteTreeTable(note.getPath(), "Note Subject");
        }
        // If the source is description and the text has canged
        else if(source == description && !description.getText().equals(note.getDescription())) {
            // Update the text on the NoteObj
            note.setDescription(description.getText());
            // Update the modification timestamp
            note.updateModificationDateTime();
            // Update the Notes TreeTable on the jTaskEdit notesTab
            for (notesListener aListener : listeners) aListener.jteNoteUpdateNoteTreeTable(note.getPath(), "Description");
        }
        // Update the Notes TreeTable on the jTaskEdit notesTab
        for (notesListener aListener : listeners) aListener.jteNoteUpdateNoteTreeTable(note.getPath(), "Modification Date");
        // Update the modification date label on the panel to the one stored in the NoteObj
        modificationDate.setText(note.getFormattedModificationDateTime());
    }

    /**
     * Do nothing when focus is gained
     */
    public void focusGained(FocusEvent e) {
    }

    /**
     * Adds a listener to the listener list. These listeners will get notified when actions are performed.
     *
     * @param notesListener - Class implementing the jtvListener interface
     */
    public void addListener(notesListener listener) {
        listeners.add(listener);
    }
}
