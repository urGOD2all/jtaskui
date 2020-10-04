package jtaskui;

import org.w3c.dom.*;

import java.util.HashMap;

// TODO: This can be removed when I'm done looking at unsupported items in notes in this class
import java.util.Map;

import java.util.UUID;

// TODO: Interesting observation, my test setup has Tasks with IDs that are mostly the same, look into this further
// TODO: as we now use this for notes as there is much overlap, we should consider perhaps having an enum to tell us what type of object this is ?
public class TaskObj {
    private Node taskNode;
    private NamedNodeMap taskAttributes;

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

    /*
     * Constructors
     */

    /**
     * Constructor to initialise this TaskObj
     */
    private TaskObj() {
        // Initialise the attributes HashMap
        attributes = new HashMap<String, String>();
        setCreationDateTime();
    }

    // TODO: I would like to remove all the things that tie this object to XML reading. This constructor is one of those things
   /*
    * This is typical when a file is being read and existing tasks are being created
    */
    public TaskObj(Node task) {
        this();
        // Set the local node
        this.taskNode = task;
        // Get the attributes
        this.taskAttributes = taskNode.getAttributes();

        // Populate the attributes of this task on this object
        populateAttributes();

        // Populate the subtasks for this task
        populateSubtasks();
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

    // TODO: Getters and setters for subtasks
    // TODO: Getters and setters for notes
    // TODO: Need standardise the terminology Child or Subtask ?
    private void populateSubtasks() {
        TaskObj childTask;
        TaskObj noteObj;

        // Lets see if we have sub tasks
        if(taskNode.hasChildNodes() == true) {
            //System.out.println(taskNode.getChildNodes().getLength());
            // Get the first subtask and store it
            Node child = taskNode.getFirstChild();

            // If we got a subtask, then lets create a TaskObj for it!
            while(child != null) {
                // TODO: We can probably just ignore text nodes
                // TODO: what happens if this is only 1 charachter ?
                if(child != null && child.getNodeType() == Node.TEXT_NODE && child.getTextContent().length() != 1) {
                    System.out.println("text_node");
                    System.out.println("N: " + child.getNodeName() + " T: " + child.getTextContent() + " V: " + child.getNodeValue() + " Type: " + child.getNodeType() + " L: " + child.getTextContent().length());
                }
                else if(child.getNodeType() == Node.ELEMENT_NODE) {
                    // Lets see what type of node this is
                    switch(child.getNodeName()) {
                        case "task":
//                          // Create a new TaskObj for the child
                            childTask = new TaskObj(child);
                            // Store the child in the map
                            addChild(childTask);
                            break;
                        case "description":
                            if (child.getTextContent() != null) setDescription(child.getTextContent());
                            break;
                        case "note":
                            // TODO: Need to handle notes, there can be multiple notes and they should have attributes. There can also be sub-notes!

                            // Create a TaskObj for this note (used because of the overlap)
                            noteObj = new TaskObj(child);
                            // Add the note to the notes map
                            notesMap.put(noteObj.getID(), noteObj);

/*                            // TODO: This is just diag stuff and needs to be removed along with the import
                            System.out.println("Desc: " + noteObj.getDescription() + " USC: " + noteObj.getUnsupportedAttributeCount());
                            for(Map.Entry<String, String> unsupItem : noteObj.getUnsupportedAttributes().entrySet()) {
                                System.out.println(unsupItem.getKey() + " = " + unsupItem.getValue());
                            }
*/
                            break;
                        default:
                            System.out.println("What is this? " + child.getNodeName());
                    }
                }
                child = child.getNextSibling();
            }
        }
    }

    private void populateAttributes() {
        String key;
        String value;
        unsupportedItems = new HashMap<String, String>();

        for(int attrNo = 0; attrNo < taskAttributes.getLength(); attrNo++) {
            key = taskAttributes.item(attrNo).getNodeName();
            value = taskAttributes.item(attrNo).getNodeValue();

            switch(key) {
                case "subject":
                    setSubject(value);
                    break;
                case "id":
                    setID(value);
                    break;
                case "creationDateTime":
                    setCreationDateTime(value);
                    break;
                case "modificationDateTime":
                    setModificationDateTime(value);
                    break;
                case "status":
                    setStatus(value);
                    break;
                case "expandedContexts":
                    setExpandedContexts(value);
                    break;
                case "actualstartdate":
                    setActualStartDateTime(value);
                    break;
                case "bgColor":
                    setBGColor(value);
                    break;
                default:
                    // If we don't match something we know about then add it to the list of unsupported items until we get code to support it
                    unsupportedItems.put(key, value);
            }
        }
    }
    /*
     * Setters
     */

    // TODO: This is private for now until I find out what this does
    /**
     * Set the status attribute of this Task.
     *
     * @param String - value to set as the status
     */
    private void setStatus(String value) {
        attributes.put("status", value);
    }

    // TODO: Considering it will always be read from the XML file as a String, perhaps a getter should be available to return this as a Date? The only reason I think this might be needed is in the GUI but I might just handle it there...
    // TODO: Need to create a proper dt object
    /*
     * This is only used within the class when a new task is created.
     */
    private void setCreationDateTime() {
        setCreationDateTime("now");
    }

    // TODO: Need to take a proper dt object
    public void setCreationDateTime(String value) {
        attributes.put("creationDateTime", value);
    }

    // TODO: Need to take a proper dt object
    public void setModificationDateTime(String value) {
        attributes.put("modificationDateTime", value);
    }

    // TODO: Need to take a proper dt object
    public void setActualStartDateTime(String value) {
        attributes.put("actualstartdate", value);
    }

    public void setSubject(String value) {
        attributes.put("subject", value);
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

    /*
     * Getters
     */

    /**
     * Method to return an attributes value given an attribute name.
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

    // TODO: Should return some kind of date time object
    public String getCreationDateTime() {
        return getAttribute("creationDateTime");
    }

    // TODO: Should return some kind of date time object
    public String getModificationDateTime() {
        return getAttribute("modificationDateTime");
    }

    public String getActualStartDateTime() {
        return getAttribute("actualStartDateTime");
    }

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

    /*
     * Returns the number of subtasks for this task
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

    public void addChild(TaskObj child) {
        childMap.put(childMap.size()+1, child);
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
