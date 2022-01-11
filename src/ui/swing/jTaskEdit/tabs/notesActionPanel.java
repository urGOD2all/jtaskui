package jtaskui.ui.swing.jTaskEdit.tabs;

import jtaskui.util.IconHelper;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;
import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.Dimension;

import javax.swing.ImageIcon;

public class notesActionPanel {
    private List<notesListener> listeners = new ArrayList<notesListener>();
    private FlowLayout flowLayout;
    private JPanel panel;
    private Dimension buttonSize;

    public notesActionPanel() {
        flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        panel = new JPanel(flowLayout);

        buttonSize = new Dimension(16, 16);

        setupButtons();
    }

    public void addListener(notesListener listener) {
        listeners.add(listener);
    }

    public JPanel getActionsPanel() {
        return panel;
    }

    private JButton createButton(ImageIcon icon) {
        JButton button = new JButton();
        button.setPreferredSize(buttonSize);
        button.setIcon(icon);
        panel.add(button);
        return button;
    }

    private void setupButtons() {
        // New task
        createButton(IconHelper.getIcon("icons/16x16/actions/newtask.png")).addActionListener(e -> { for (notesListener aListener : listeners) aListener.jteNoteActionsNewNote(); });
        // New sub task
        createButton(IconHelper.getIcon("icons/16x16/actions/new_sub.png")).addActionListener(e -> { for (notesListener aListener : listeners) aListener.jteNoteActionsNewSubNote(); });
        // TODO: Insert seperator
        createButton(IconHelper.getIcon("icons/16x16/actions/editdelete.png")).addActionListener(e -> { for (notesListener aListener : listeners) aListener.jteNoteActionsDeleteNote(); });
    }
}
