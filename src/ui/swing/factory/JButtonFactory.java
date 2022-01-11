package jtaskui.ui.swing.factory;

import javax.swing.JButton;
import javax.swing.ImageIcon;

import java.awt.Dimension;
import java.awt.event.ActionListener;

public class JButtonFactory {

    /**
     * Create a button with the specified text
     *
     * @param text - String text to set on the button
     */
    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        return button;
    }

    /**
     * Create a button with no text, icon only.
     *
     * @param icon - ImageIcon object for the icon to load onto the button
     * @param size - Dimension to set the preferred size of the button
     */
    public static JButton createButton(ImageIcon icon) {
        JButton button = new JButton();
        button.setIcon(icon);
        return button;
    }

    /**
     * Create a button with no text, icon only. Also sets the ActionListener.
     *
     * @param icon - ImageIcon object for the icon to load onto the button
     * @param listener - ActionListener to set on this button
     */
    public static JButton createButton(ImageIcon icon, ActionListener listener) {
        JButton button = createButton(icon);
        button.addActionListener(listener);
        return button;
    }

    /**
     * Create a button with no text, icon only. Sets the ActionListener and the preferred size.
     *
     * @param icon - ImageIcon object for the icon to load onto the button
     * @param listener - ActionListener to set on this button
     * @param size - Dimension to set the preferred size of the button
     */
    public static JButton createButton(ImageIcon icon, ActionListener listener, Dimension size) {
        JButton button = createButton(icon, listener);
        button.setPreferredSize(size);
        return button;
    }
}
