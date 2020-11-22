package jtaskview.ui;

import javax.swing.JComponent;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import java.awt.Dimension;

import javax.swing.JSeparator;

/**
 * Utility class for managing GridBagLayout
 */
public class LayoutManager {

    private int currentRow;
    private int currentCol;
    // The component having its GridBagLayout managed
    private JComponent destComponent;

    public LayoutManager(JComponent destComponent) {
        currentRow = 0;
        currentCol = 0;
        this.destComponent = destComponent;

        GridBagLayout destLayout = new GridBagLayout();
        this.destComponent.setLayout(destLayout);
    }

    /**
     * Add component to the next row. Resets the current row and column. Think of this like a typewriter after hitting return.
     */
    public void addNextRow(JComponent addComponent) {
        currentRow = currentRow + 1;
        currentCol = 0;
        addNext(addComponent);
    }

    /**
     * Places addComponent onto destComponent in the next available position on this row.
     */
    public void addNext(JComponent addComponent) {
        addNext(addComponent, currentCol, currentRow, 1, 1, 0.0, 0.5, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE);
    }

    /**
     * Internal helper for addNext
     */
    private void addNext(JComponent addComponent, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill) {
        addComponent(addComponent, gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill);
        currentCol = currentCol + 1;
    }

// TODO: This repeats a lot of addNext and addNextRow methods!
    /**
     * Add a separator to the next row.
     */
    public void addSeparator() {
        currentRow = currentRow + 1;
        currentCol = 0;
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setPreferredSize(new Dimension(0,1));
        addComponent(separator, currentCol, currentRow, GridBagConstraints.REMAINDER, 1, 1.0, 1.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL);
        currentRow = currentRow + 1;
    }

    /**
     * Adds a component to destComponent taking all the grid bag contraints as params.
     */
    private void addComponent(JComponent addComponent, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        constraints.anchor = anchor;
        constraints.fill = fill;

        destComponent.add(addComponent, constraints);
    }
}
