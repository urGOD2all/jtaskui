package jtaskui.ui.swing.jTaskView;

import jtaskui.util.IconHelper;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;
import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.Dimension;

import javax.swing.ImageIcon;

public class jtvTaskActionsPanel {
    private List<jtvListener> listeners = new ArrayList<jtvListener>();
    private FlowLayout flowLayout;
    private JPanel panel;
    private Dimension buttonSize;

    public jtvTaskActionsPanel() {
        flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        panel = new JPanel(flowLayout);

        buttonSize = new Dimension(16, 16);

        setupButtons();
    }

    public void addListener(jtvListener listener) {
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
        createButton(IconHelper.getIcon("icons/16x16/actions/newtask.png")).addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsNewTask(); });
        // New sub task
        createButton(IconHelper.getIcon("icons/16x16/actions/new_sub.png")).addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsNewSubTask(); });
        // TODO: Insert seperator
        createButton(IconHelper.getIcon("icons/16x16/actions/editdelete.png")).addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsDeleteTask(); });
    }
}
