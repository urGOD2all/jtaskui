package jtaskui.ui.swing.jTaskView;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Creates the JMenuBar for the main frame jTaskView
 */
public class jtvMenuBar {
    // Listeners to be notified when actions are performed
    private List<jtvListener> listeners = new ArrayList<jtvListener>();

    // References stored global so text and enabled state can be changed
    private JMenuBar menuBar;
    private JMenu file, edit, view, viewColumns, newMenu;
    private JMenuItem fileOpen, fileSave, fileSaveAs, fileClose, fileQuit, editTask, deleteTask, viewColumnsCreationDate, viewColumnsModificationDate, viewColumnsDescription;

    /**
     * Initialises the JMenuBar and calls methods to create all the items
     */
    public jtvMenuBar() {
        menuBar = new JMenuBar();
        buildFileMenus();
        buildEditMenus();
        buildViewMenus();
        buildNewMenus();
    }

    /**
     * Returns the created JMenuBar
     *
     * @return JMenuBar - the menu for jTaskView
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * Adds a listener to the listener list. These listeners will get notified when actions are performed.
     *
     * @param jtvListener - Class implementing the jtvListener interface
     */
    public void addListener(jtvListener listener) {
        listeners.add(listener);
    }

    /*
     * File menu
     */

    private void buildFileMenus() {
        file = new JMenu("File");
        fileOpen = new JMenuItem("Open");
        fileOpen.addActionListener(e -> fileOpen());
        fileSave = new JMenuItem("Save");
        fileSave.addActionListener(e -> fileSave());
        fileSave.setEnabled(false);
        fileSaveAs = new JMenuItem("Save As");
        fileSaveAs.addActionListener(e -> fileSaveAs());
        fileSaveAs.setEnabled(false);
        fileClose = new JMenuItem("Close");
        fileClose.addActionListener(e -> fileClose());
        fileClose.setEnabled(false);
        fileQuit = new JMenuItem("Quit");
        fileQuit.addActionListener(e -> fileQuit());
        // Build the File menu
        file.add(fileOpen);
        file.add(fileSave);
        file.add(fileSaveAs);
        file.add(fileClose);
        file.add(fileQuit);

        menuBar.add(file);
    }

    private void fileOpen() {
        // Do file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Task Coach files", "tsk"));
        int returnVal = fileChooser.showOpenDialog(menuBar);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            // Get the file path that was opened
            String taskFilePath = fileChooser.getSelectedFile().getAbsoluteFile().toString();
            // Notify listeners
            for (jtvListener aListener : listeners) aListener.jtvMenuBarFileOpen(taskFilePath);

            // Change the state of the other File menu items
            if (fileOpen.getText() == "Merge") fileSave.setEnabled(false);
            else fileSave.setEnabled(true);

            // Update the item text to show future opens will merge
            fileOpen.setText("Merge");
            fileSaveAs.setEnabled(true);
            fileClose.setEnabled(true);
        }
    }

    private void fileSave() {
        for (jtvListener aListener : listeners) aListener.jtvMenuBarFileSave();
    }   

    private void fileSaveAs() {
        // File chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Task Coach files", "tsk"));
        int returnVal = fileChooser.showSaveDialog(menuBar);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            // Store the file path that was opened
            String taskFilePath = fileChooser.getSelectedFile().getAbsoluteFile().toString();
            // Fix the extention if the user didnt type it
            if( ! taskFilePath.endsWith(".tsk")) taskFilePath = taskFilePath + ".tsk";
            // Enable the save option again
            fileSave.setEnabled(true);

            for (jtvListener aListener : listeners) aListener.jtvMenuBarFileSaveAs(taskFilePath);
        }   
    }   

    private void fileClose() {
        for (jtvListener aListener : listeners) aListener.jtvMenuBarFileClose();
        // Reset the text to Open
        fileOpen.setText("Open");
    }   


    private void fileQuit() {
        for (jtvListener aListener : listeners) aListener.jtvMenuBarFileQuit();
    }

    /*
     * Edit menu
     */

    private void buildEditMenus() {
        edit = new JMenu("Edit");
        editTask = new JMenuItem("Edit");
        editTask.addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsEditTask(); } );
        editTask.setMnemonic(KeyEvent.VK_E);
        editTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        deleteTask = new JMenuItem("Delete");

        edit.addSeparator();
        edit.add(editTask);
        edit.add(deleteTask);
        edit.addSeparator();

        menuBar.add(edit);
    }
    /*
     * View menu
     */

    private void buildViewMenus() {
        // The "View" menu bar item
        view = new JMenu("View");
        viewColumns = new JMenu("Columns");
        viewColumnsCreationDate = new JMenuItem("Creation Date");
        viewColumnsCreationDate.addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvMenuBarViewCreationDate(); } );
        viewColumnsModificationDate = new JMenuItem("Modification Date");
        viewColumnsModificationDate.addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvMenuBarViewModificationDate(); });
        viewColumnsDescription = new JMenuItem("Description");
        viewColumnsDescription.addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvMenuBarViewDescription(); });
        // Build the View menu
        view.add(viewColumns);
        viewColumns.add(viewColumnsCreationDate);
        viewColumns.add(viewColumnsModificationDate);
        viewColumns.add(viewColumnsDescription);

        menuBar.add(view);
    }

    private void buildNewMenus() {
        newMenu = new JMenu("New");
        JMenuItem newMenuNewTask = new JMenuItem("New Task");
        newMenuNewTask.addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsNewTask(); } );
        newMenuNewTask.setMnemonic(KeyEvent.VK_N);
        newMenuNewTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
        JMenuItem newMenuNewSubTask = new JMenuItem("New SubTask");
        newMenuNewSubTask.addActionListener(e -> { for (jtvListener aListener : listeners) aListener.jtvTaskActionsNewSubTask(); } );
        newMenuNewSubTask.setMnemonic(KeyEvent.VK_S);
        newMenuNewSubTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));

        newMenu.add(newMenuNewTask);
        newMenu.addSeparator();
        newMenu.add(newMenuNewSubTask);

        menuBar.add(newMenu);
    }
}
