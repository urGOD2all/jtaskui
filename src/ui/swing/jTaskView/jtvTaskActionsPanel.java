package jtaskui.ui.swing.jTaskView;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;
import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.Dimension;

import java.awt.Image;
import javax.imageio.ImageIO;
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

// TODO: This works to read icons but the icons must be in the class path. I could also have them raw or in a jar. I need to consider how to start the application, do I have a script that will set the class path or should I put them in where the compiled code is an have some kind of monolith structure. The source structure is a mess anyway and that needs tidying too.
    private ImageIcon getIcon(String iconPath) {
        Image img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getSystemResource(iconPath));
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }

        return new ImageIcon(img);
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
        createButton(getIcon("icons/16x16/actions/newtask.png")).addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsNewTask(); });
        // New sub task
        createButton(getIcon("icons/16x16/actions/new_sub.png")).addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsNewSubTask(); });
        // TODO: Insert seperator
        createButton(getIcon("icons/16x16/actions/editdelete.png")).addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsDeleteTask(); });
    }
}
