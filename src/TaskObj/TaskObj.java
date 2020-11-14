package jtaskui;

import java.util.HashMap;

// TODO: This can be removed when I'm done looking at unsupported items in notes in this class
import java.util.Map;

import java.util.UUID;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// TODO: Interesting observation, my test setup has Tasks with IDs that are mostly the same, look into this further
// TODO: as we now use this for notes as there is much overlap, we should consider perhaps having an enum to tell us what type of object this is ?
public class TaskObj {
    // This is a map of all the attributes that the task has
    private HashMap<String, String> attributes;

    // Store the description of this task. This is not an attribute so does not get stored in the attributes map
    private String description;

    // TODO: This can have multiple entries, I think it needs storing as an array or something so it can be interogated better
    // TODO: Understand what this can get populated with and what it means
    // TODO: As this is an attribute it should really be in (or returned within) the attributes map
    // This variable keeps state for when something is expanded or not and where
    private String expandedContexts;

    // This is a Map of all attributes that are unsupported by this code. Ideally this should have no items in it!
    private HashMap<String, String> unsupportedItems;

    // TODO: I dont like this, I think its naff. I think the better option would be to use a sortable Collection like TreeSet
    //  // TODO (cont.) : I can get that TreeSet Collection from a Vector so maybe this is best served as a Vector object ?
    // This is a Map of all the child tasks
    private HashMap<Integer, TaskObj> childMap = new HashMap<Integer, TaskObj>();

    // TODO: I dont like this, I think its naff. I think the better option would be to use a sortable Collection like TreeSet
    //  // TODO (cont.) : I can get that TreeSet Collection from a Vector so maybe this is best served as a Vector object ?
    // This is a Map of all the notes
    private HashMap<String, TaskObj> notesMap = new HashMap<String, TaskObj>();

    // Used to store the parent of this task. Will be null if this is the root
    private TaskObj parent;

    // Store the number of all subtasks in the children
    private int subTaskCount;

    // Formatter objects for converting the date time format from TaskCoach stored format and the display format
    private DateTimeFormatter readInDateFormat, dateDisplayFormat;

    /*
     * Constructors
     */

    /**
     * Constructor to initialise this TaskObj
     */
    private TaskObj() {
        // Initialise the attributes HashMap
        attributes = new HashMap<String, String>();
        // Initialise the unsupported attributes HashMap
        unsupportedItems = new HashMap<String, String>();
        setCreationDateTime();
        // Set initial subtasks to 0 (we are new so we have no children yet)
        subTaskCount = 0;
        // Create a formatter for reading in from the file.
        readInDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnn");
        // Create a formater that for display purposes
        dateDisplayFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    /**
     * Constructor of a basic task.
     * This is typically called to create new Tasks.
     * This is also called to create the first (root) TaskObj for the TreeTable to hold all the other tasks in a tree like structure.
     *
     * @param subject - String representing the subject of the Task
     */
    public TaskObj(String subject) {
        this();
        setID(UUID.randomUUID().toString());
        setSubject(subject);
    }

    /*
     * Other methods
     */

    /*
     * Setters
     */

    public void populateAttributes(HashMap<String, String> attributes) {
        //this.attributes = new HashMap<String, String>(attributes);
        for(Map.Entry<String, String> attr: attributes.entrySet()) {
            switch(attr.getKey()) {
                case "subject":
                    setSubject(attr.getValue());
                    break;
                case "id":
                    setID(attr.getValue());
                    break;
                case "creationDateTime":
                    setCreationDateTime(attr.getValue());
                    break;
                case "modificationDateTime":
                    setModificationDateTime(attr.getValue());
                    break;
                case "duedate":
                    setDueDateTime(attr.getValue());
                    break;
                case "plannedstartdate":
                    setPlannedStartDateTime(attr.getValue());
                    break;
                case "completiondate":
                    setCompletionDateTime(attr.getValue());
                    break;
                case "status":
                    setStatus(attr.getValue());
                    break;
                case "expandedContexts":
                    setExpandedContexts(attr.getValue());
                    break;
                case "actualstartdate":
                    setActualStartDateTime(attr.getValue());
                    break;
                case "percentageComplete":
                    setPercentageComplete(attr.getValue());
                    break;
                case "bgColor":
                    setBGColor(attr.getValue());
                    break;
                case "priority":
                    setPriority(attr.getValue());
                    break;
                default:
                    // If we don't match something we know about then add it to the list of unsupported items until we get code to support it
                    unsupportedItems.put(attr.getKey(), attr.getValue());
            }
        }
    }

    // TODO: This is private for now until I find out what this does
    /**
     * Set the status attribute of this Task.
     *
     * @param String - value to set as the status
     */
    private void setStatus(String value) {
        attributes.put("status", value);
    }

    /*
     * This is only used within the class when a new task is created.
     */
    private void setCreationDateTime() {
        setCreationDateTime("now");
    }

    public void setCreationDateTime(String value) {
        attributes.put("creationDateTime", value);
    }

    public void setModificationDateTime(String value) {
        attributes.put("modificationDateTime", value);
    }

    public void setDueDateTime(String value) {
        attributes.put("duedate", value);
    }

    public void setPlannedStartDateTime(String value) {
        attributes.put("plannedstartdate", value);
    }

    public void setActualStartDateTime(String value) {
        attributes.put("actualstartdate", value);
    }

    public void setCompletionDateTime(String value) {
        attributes.put("completiondate", value);
    }

    public void setPercentageComplete(String value) {
        attributes.put("percentageComplete", value);
    }

    public void setSubject(String value) {
        attributes.put("subject", value);
    }

    public void setPriority(String value) {
        attributes.put("priority", value);
    }

    /*
     * Set the description for this task
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /* This will set the ID. I dont think this will change so keep it private
     */
    private void setID(String value) {
        attributes.put("id", value);
    }

    // TODO: This may need to accept something else depending on what we decide to the TODO in the global where the variable is declared
    // TODO: Given the above, this may need to be changed from a "set" to an "add" method
    public void setExpandedContexts(String value) {
        this.expandedContexts = value;
        attributes.put("expandedContexts", value);
    }

    public void setBGColor(String value) {
        attributes.put("bgColor", value);
    }

    /**
     * Sets the parent to to TaskObj provided for this TaskObj.
     *
     * @param parent - TaskObj that is the direct parent of this TaskObj
     */
    public void setParent(TaskObj parent) {
        this.parent = parent;
    }

    /*
     * Getters
     */

    /**
     * Method to return an attributes value given an attribute name.
     * This method returns raw values as stored in the datasource as Strings.
     *
     * @param name - String representing an attribute name
     *
     * @return String - value of the attribute requested or null if the attribute is not set
     */
    public String getAttribute(String name) {
        return attributes.get(name);
    }

    // TODO: Do I want to return this as a String or int ?
    /**
     * Return the status attribute of this Task
     *
     * @return int - status attribute of the Task
     */
    public int getStatus() {
        try {
            return Integer.parseInt(getAttribute("status"));
        }
        catch(NumberFormatException nfe) {
            System.out.println("ERROR: Failed to parse status, setting to 1");
        }
        finally {
            setStatus("1");
            return 1;
        }
    }

    public String getSubject() {
        return getAttribute("subject");
    }

    public String getID() {
        return getAttribute("id");
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return getAttribute("priority");
    }

    /**
     * Helper to handle parsing of dates and times to LocalDateTime objects
     *
     * Note: If parsing fails an error will be emitted to standard error and the date and time now will be returned instead.
     *
     * @param dateTime - String representation of the date and time to parse
     * @param inFormat - DateTimeFormatter in the format of dateTime
     * @return LocalDateTime - parsed to the TaskCoach format "yyyy-MM-dd HH:mm:ss.nnnnnn"
     */
    private LocalDateTime parseDateTime(String dateTime, DateTimeFormatter inFormat) {
        try {
            return LocalDateTime.parse(dateTime, inFormat);
        }
        catch (DateTimeParseException e) {
            System.err.println("ERROR: Failed to parse " + dateTime + " - Expected format " + inFormat);
            return LocalDateTime.now();
        }
    }

    /**
     * Helper to handle parsing dates and times to String output format
     *
     * Note: If parsing fails an error will be emitted to standard error and the date and time now will be returned instead.
     *
     * @param dateTime - String representation of the date and time to parse
     * @return String - dateTime parsed to the display format "yyyy-MM-dd HH:mm" or "N/A" when input is null
     */
    private String getFormattedDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, readInDateFormat).format(dateDisplayFormat);
        }
        catch(NullPointerException npe) {
            // This attribute is absent so return N/A which is what formatted field dates look like when they are absent
            return "N/A";
        }
    }

    /**
     * Returns a String representation of the creationDateTime attribute. This is the raw value from the datasource.
     *
     * @return String - the raw creation date and time attribute from the datasource
     */
    public String getCreationDateTime() {
        return getAttribute("creationDateTime");
    }

    /**
     * Returns a String representation of the creationDateTime attribute in the display format.
     *
     * @return String - Formatted Date and Time
     */
    public String getFormattedCreationDateTime() {
        return getFormattedDateTime(getCreationDateTime());
    }

    /**
     * Returns a String representation of the modificationDateTime attribute. This is the raw value from the datasource.
     *
     * @return String - the raw modification date and time attribute from the datasource
     */
    public String getModificationDateTime() {
        return getAttribute("modificationDateTime");
    }

    /**
     * Returns a String representation of the modificationDateTime attribute in the display format.
     *
     * @return String - Formatted Date and Time
     */
    public String getFormattedModificationDateTime() {
        return getFormattedDateTime(getModificationDateTime());
    }

    /**
     * Returns a String represenation of the duedate attribute. This is the raw value from the datasource.
     *
     * @return String - the raw due date and time attribute from the datasource.
     */
    public String getDueDateTime() {
        return getAttribute("duedate");
    }

    /**
     * Returns a String representation of the duedate attribute in the display format.
     * At the moment the stored format and display format is the same we here we do no formatting other than existance checking.
     *
     * @return String - Formatted Date and Time
     */
    public String getFormattedDueDateTime() {
        if (hasAttribute("duedate")) return getAttribute("duedate");
        else return "N/A";
    }

    /**
     * Returns a String representation of the actualStartDateTime attribute. This is the raw value from the datasource.
     *
     * @return String - the raw actual start date and time attribute from the datasource.
     */
    public String getActualStartDateTime() {
        return getAttribute("actualStartDateTime");
    }

    /**
     * Returns a String representation of the actualStartDateTime attribute in the display format.
     *
     * @return String - Formatted Date and Time
     */
    public String getFormattedActualStartDateTime() {
        return getFormattedDateTime(getActualStartDateTime());
    }

    /**
     * Returns a String represenation of the plannedstartdate attribute, This is the raw value from the datasource.
     *
     * @return String - the raw planned start date and time attribute from the datasource.
     */
    public String getPlannedStartDateTime() {
        return getAttribute("plannedstartdate");
    }

    /**
     * Returns a String representation of the plannedstartdate attribute in display format.
     *
     * @return String - Formatted Date and Time
     */
    public String getFormattedPlannedStartDate() {
        return getFormattedDateTime(getPlannedStartDateTime());
    }

    /**
     * Returns a String representation of the completiondate attribute. This is the raw value from the datasource.
     *
     * @return String - the raw completion date and time attribute from the datasource.
     */
    public String getCompletionDateTime() {
        return getAttribute("completiondate");
    }

    /**
     * Returns a String represenation of the completiondate attribute in the display format.
     *
     * @return String - Formatted Date and Time
     */
    public String getFormattedCompletionDateTime() {
        return getFormattedDateTime(getCompletionDateTime());
    }

    /**
     * Returns the percentage completion of this TaskObj
     *
     * @return int - percentage of TaskObj completion.
     */
    public int getPercentageComplete() {
        try {
            return Integer.parseInt(getAttribute("percentageComplete"));
        }
        catch(NumberFormatException nfe) {
            // Attribute doesn't exist, return 0
            return 0;
        }
    }

    /**
     * Returns a copy of the task attributes
     *
     * @return HashMap<String, String> - Attributes for this task
     */
    public HashMap<String, String> getAttributes() {
        return new HashMap<String, String>(attributes);
    }

    // TODO: See the TODOs on the setter
    public String getExpandedContexts() {
        return expandedContexts;
    }

    // TODO: Should we return an object that can represent this so we dont have to handle it anywhere else ?
    public String getBGColor(String value) {
        return getAttribute("bgColor");
    }

    public HashMap<String, String> getUnsupportedAttributes() {
        return new HashMap<String, String>(unsupportedItems);
    }

    /**
     * Returns the number of subtasks for this task (this level only).
     */
    public int getChildCount() {
        return childMap.size();
    }

    /**
     * Get the Index of the specified Child/subtask
     *
     * @param TaskObj child - this is a child object / subtask to fine the index of
     * @return int - index of the specified child
     */
    public int getChildIndex(TaskObj child) {
        for (Map.Entry<Integer, TaskObj> aChild : childMap.entrySet()) {
            if (aChild.getValue() == child) return aChild.getKey();
        }
        System.out.println("Error: Asked for a childs position that does not exist!");
        return 0;
    }

    /**
     * Get the child subtask from the specified index
     *
     * @param index - int position of the child to query
     * @return TaskObj - child/subtask at that location
     */
    public TaskObj getChildAt(int index) {
        return childMap.get(index);
    }

    /**
     * Returns the total number of children over all subtasks
     */
    public int getAllChildCount() {
        return subTaskCount;
    }

    /**
     * Returns the parent TaskObj for this TaskObj. Can be null if this is the root of the tree.
     */
    public TaskObj getParent() {
        return parent;
    }

    public void addChild(TaskObj child) {
        childMap.put(childMap.size()+1, child);
        // Set the given parent
        child.setParent(this);
        // Tell the parent TaskObj that the number of children has changed
        if (getParent() != null) getParent().incrementSubChildCount();
        // Else, this object is the root of the tree so needs to know objects have been added at this level
        else incrementSubChildCount();
    }

    /**
     * Update the number of children in all the subtasks.
     * Typically called to tell the parent that the number of total subtasks in the tree has changed.
     * Can also be called by this object if its the root of the tree (indicated by having null parent)
     * to increment the number of subtasks at this level.
     */
    public void incrementSubChildCount() {
        subTaskCount = subTaskCount + 1;
        // Tell the parent that the number of children has changed
        if (parent != null) parent.incrementSubChildCount();
    }

    // TODO: Need to handle notes better than this
    public void addNote(TaskObj note) {
        // Add the note to the notes map
        notesMap.put(note.getID(), note);
    }
    /**
     * If there are no children (subtasks) in the childMap then return true, otherwise false.
     *
     * @return boolean - true if this object contains children (subtasks), false if not
     */
    public boolean hasChildren() {
        if (childMap.size() == 0) return false;
        return true;
    }

    /**
     * Returns true if the attribute exists for this TaskObj, false otherwise.
     *
     * @param attribute - String name of attribute
     * @return boolean - true if the attribute exists on this TaskObj, false otherwise
     */
    private boolean hasAttribute(String attribute) {
        return attributes.containsKey(attribute);
    }

    public boolean hasDescription() {
        if (getDescription() == null) return false;
        return true;
    }

    public int getUnsupportedAttributeCount() {
        return unsupportedItems.size();
    }

    /**
     * Returns the task subject for the toString method because it will always be the expandable row.
     *
     * @param String - Subject of the task
     */
    @Override
    public String toString() {
        return this.getSubject();
    }
}
