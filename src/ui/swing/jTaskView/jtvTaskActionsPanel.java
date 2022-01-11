package jtaskui.ui.swing.jTaskView;

import jtaskui.ui.swing.factory.JButtonFactory;
import jtaskui.util.IconHelper;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;
import java.awt.FlowLayout;

import java.awt.Dimension;

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

    private void setupButtons() {
        // New task
        panel.add(JButtonFactory.createButton(IconHelper.getIcon("icons/16x16/actions/newtask.png"), (e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsNewTask(); }), buttonSize));
        // New sub task
        panel.add(JButtonFactory.createButton(IconHelper.getIcon("icons/16x16/actions/new_sub.png"), (e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsNewSubTask(); }), buttonSize));
        // TODO: Insert seperator
        panel.add(JButtonFactory.createButton(IconHelper.getIcon("icons/16x16/actions/editdelete.png"), (e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsDeleteTask(); }), buttonSize));
    }
}
