package jtaskui.Task;

import jtaskui.TaskObj;

/**
 * Object created for each Note attached to a Task.
 * Each NoteObj will have a link back to its parent or the ROOT NoteObj on the Task if
 * it has no parent.
 *
 * Extends TaskObj. A Sub-note will behave as a child just like a Sub-task does in TaskObj
 * so NoteObj primarily maps "Note operations" back to the ones in TaskObj for adding Tasks.
 */
public class NoteObj extends TaskObj {

    /**
     * Only constructor that gets used. Accepts a subject for the Note
     * and passes it to the super constructor of the same.
     *
     * @param String - subject of this Note
     */
    public NoteObj(String subject) {
        super(subject, null);
    }

    /**
     * Map adding a note to adding a Child. Tasks add new Notes to its ROOT
     * and Notes add sub-notes as "children" in the same way that Tasks add
     * sub-tasks as "children".
     *
     * @param NoteObj - The new NoteObj to add as a child of "this" NoteObj
     */
    @Override
    public void addNote(NoteObj note) {
        addChild(note);
    }

    /**
     * Get the number of sub-notes by mapping the call to children from TaskObj.
     *
     * @return int - Number of Child/Sub-notes that "this" Note has
     */
    @Override
    public int getSubNoteCount() {
        return this.getChildCount();
    }

    /**
     * Gets a sub-note by index by mapping the call to children in TaskObj and casting to NoteObj
     *
     * @param int - index of the Child/Sub-note to get
     * @return NoteObj - The Note at the position specified
     */
    public NoteObj getSubNoteAt(int index) {
        return (NoteObj) super.getChildAt(index);
    }

    /**
     * Get the Index of the specified Child/sub-note. Maps to getIndex in TaskObj
     *
     * @param NoteObj child - this is a child object / sub-note to find the index of
     */
    public int getSubNoteIndex(NoteObj note) {
        return getIndex(note);
    }
}
