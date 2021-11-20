package jtaskui.ui.swing.jTaskView;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import java.util.List;
import java.util.ArrayList;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;

import java.awt.Dimension;

public class jTaskViewTreeTableRC {
    // Listeners to be notified when actions are performed
    private List<jtvListener> listeners = new ArrayList<jtvListener>();
    // The popup menu
    private JPopupMenu popupMenu;
    // Items that need to change state
    private JMenuItem pasteTaskItem, pasteSubTaskItem;

    public jTaskViewTreeTableRC() {
        popupMenu     = new JPopupMenu();

        // This listener will select the row that was right-clicked before showing the popup
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                for (jtvListener aListener : listeners) aListener.jtvSelectRowAtPoint(popupMenu);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        build();
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    private void build() {
        // Add and built all the items to the menu
        popupMenu.add(configureMenuItem("Cut", KeyEvent.VK_T, KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK, e->cutAction()));
        popupMenu.add(configureMenuItem("Copy", KeyEvent.VK_C, KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK, e->copyAction()));
        pasteTaskItem = configureMenuItem("Paste", KeyEvent.VK_P, KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK, e->pasteTaskAction(), false);
        pasteSubTaskItem = configureMenuItem("Paste as sub-task", KeyEvent.VK_A, KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, e->pasteAsSubTaskAction(), false);
        popupMenu.add(pasteTaskItem);
        popupMenu.add(pasteSubTaskItem);
        popupMenu.addSeparator();
        popupMenu.add(configureMenuItem("Edit", KeyEvent.VK_E, KeyEvent.VK_ENTER, 0, e->{ for (jtvListener aListener : listeners) aListener.jtvTaskActionsEditTask(); }, 300));
        popupMenu.add(configureMenuItem("Delete", KeyEvent.VK_D, KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK, e->{ for (jtvListener aListener : listeners) aListener.jtvTaskActionsDeleteTask(); }));
        popupMenu.addSeparator();
    }

    // TODO: need to add icon support
    private JMenuItem configureMenuItem(String title, int mnemonic, int accKeyCode, int accModifiers, ActionListener action, int width) {
        JMenuItem item = configureMenuItem(title, mnemonic, accKeyCode, accModifiers, action);

        item.setPreferredSize(new Dimension(width, item.getPreferredSize().height));

        return item;
    }

    private JMenuItem configureMenuItem(String title, int mnemonic, int accKeyCode, int accModifiers, ActionListener action, boolean initialState) {
        JMenuItem item = configureMenuItem(title, mnemonic, accKeyCode, accModifiers, action);

        item.setEnabled(initialState);

        return item;
    }

    private JMenuItem configureMenuItem(String title, int mnemonic, int accKeyCode, int accModifiers, ActionListener action) {
        JMenuItem item = new JMenuItem(title);

        item.setMnemonic(mnemonic);
        item.setAccelerator(KeyStroke.getKeyStroke(accKeyCode, accModifiers));
        item.addActionListener(action);

        return item;
    }

    private void cutAction() {
        // Inform the listeners there has been a cut
        for (jtvListener aListener : listeners) aListener.jtvTaskActionsCutTask();
        // Enable the paste actions
        togglePasteState(true);
    }

    private void copyAction() {
        // Inform the listeners there has been a copy
        for (jtvListener aListener : listeners) aListener.jtvTaskActionsCopyTask();
    }

    private void pasteTaskAction() {
        // Inform the listeners there has been a paste
        for (jtvListener aListener : listeners) aListener.jtvTaskActionsPasteTask();
        // Disable the pase options
        togglePasteState(false);
    }

    private void pasteAsSubTaskAction() {
        // Inform the listeners there has been a paste as sub-task
        for (jtvListener aListener : listeners) aListener.jtvTaskActionsPasteAsSubTask();
        // Disable the pase options
        togglePasteState(false);
    }

    private void togglePasteState(boolean newState) {
        pasteTaskItem.setEnabled(newState);
        pasteSubTaskItem.setEnabled(newState);
    }

    /**
     * Adds a listener to the listener list. These listeners will get notified when actions are performed.
     *
     * @param jtvListener - Class implementing the jtvListener interface
     */
    public void addListener(jtvListener listener) {
        listeners.add(listener);
    }
}
