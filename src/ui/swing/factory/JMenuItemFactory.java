package jtaskui.ui.swing.factory;

import javax.swing.JMenuItem;
import java.awt.event.ActionListener;

import javax.swing.KeyStroke;

import java.awt.Dimension;

public class JMenuItemFactory {

    // This is a helper class and only contains static methods/fields. No instantiation allowed.
    private JMenuItemFactory() {}

    // TODO: need to add icon support
    public static JMenuItem configureMenuItem(String title, int mnemonic, int accKeyCode, int accModifiers, ActionListener action, int width) {
        JMenuItem item = configureMenuItem(title, mnemonic, accKeyCode, accModifiers, action);

        item.setPreferredSize(new Dimension(width, item.getPreferredSize().height));

        return item;
    }   

    public static JMenuItem configureMenuItem(String title, int mnemonic, int accKeyCode, int accModifiers, ActionListener action, boolean initialState) {
        JMenuItem item = configureMenuItem(title, mnemonic, accKeyCode, accModifiers, action);

        item.setEnabled(initialState);

        return item;
    }

    public static JMenuItem configureMenuItem(String title, int mnemonic, int accKeyCode, int accModifiers, ActionListener action) {
        JMenuItem item = new JMenuItem(title);

        item.setMnemonic(mnemonic);
        item.setAccelerator(KeyStroke.getKeyStroke(accKeyCode, accModifiers));
        item.addActionListener(action);

        return item;
    }
}
