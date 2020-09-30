package jtaskui;

import org.w3c.dom.*;

import java.util.HashMap;

// TODO: This can be removed when I'm done looking at unsupported items in notes in this class
import java.util.Map;

// TODO: as we now use this for notes as there is much overlap, we should consider perhaps having an enum to tell us what type of object this is ?
public class TaskObj {
    private Node taskNode;
    private NamedNodeMap taskAttributes;

    // TODO: Do we get any value from not having all this just in a single HashMap? I don't know if its worth storing a date object in here. Could just return a date object after converting the String, its going to be a String anyway
    // TODO: these need to some kind of date time object
    // This is the date that the task was created
    private String creationDateTime;
    // This is the date that the task was last modified
    private String modificationDateTime;
    // This is the date that the task begun
    private String actualStartDateTime;
    // Store the description of this task
    private String description;

    // TODO: Interesting observation, my test setup has IDs that are mostly the same, look into this further
    // TODO: Should this be represented as a better object or is String OK here ?
    // This is a task GUID, one would assume this is unique in the file
    private String ID;
    // This is the name of the task
    private String subject;
    // TODO: This can have multiple entries, I think it needs storing as an array or something so it can be interogated better
    // TODO: Understand what this can get populated with and what it means
    // This variable keeps state for when something is expanded or not and where
    private String expandedContexts;

    // TODO: what does this represent ? Is it if its expanded or if its in progress / complete ?
    private int status;

    // This is a map of all the attributes that the task has
    private HashMap<String, String> attributes;
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

    /*
     * TODO: Does this constructor serve any purpose?
     */
    private TaskObj() {
        // Initialise the attributes HashMap
        attributes = new HashMap<String, String>();
        setCreationDateTime();
    }

   /*
    * This is typical when a file is being read and existing tasks are being created
    */
    public TaskObj(Node task) {
        this();
        // Set the local node
        this.taskNode = task;
        // Get the attributes
        this.taskAttributes = taskNode.getAttributes();
        // Set a default description
        setDescription("");

        // Populate the attributes of this task on this object
        populateAttributes();

        // Populate the subtasks for this task
        populateSubtasks();
    }

    // TODO: Perhaps this is not typical of that, where did the ID come from? derp
    // TODO: Given the above TODO I think is refering to creating new tasks, it would infer the job of creating the GUID be that of the caller which I dont think is write. We should generate the GUID here so this needs re-work.
    /**
     * Construction of a basic task
     * This is typical when new tasks are created.
     * It is also used to create the first (root) object for the TreeTable to hold all the other tasks.
     *
     * @param ID - String object containing this tasks ID. This is typically a GUID.
     */
    public TaskObj(String ID, String subject) {
        this();
        setupTask(ID, subject);
    }

    /*
     * Other methods
     */

    /*
     * User to perform some common task setup functions. This is typical when a new task is created.
     */
    private void setupTask(String ID, String subject) {
        setID(ID);
        setSubject(subject);
        setCreationDateTime();
        setDescription("");
    }

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
                            childMap.put(childMap.size()+1, childTask);
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
    /*
     * Set the status of the task.
     * This is read from the XML as a String so this takes a String but sets an int.
     * If this becomes a problem in the future I will change this to a String.
     */
    private void setStatus(String value) {
        try {
            this.status = Integer.parseInt(value);
        }
        catch(NumberFormatException nfe) {
            System.out.println("ERROR: Failed to parse status, setting to 1");
        }
        finally {
            this.status = 1;
        }
        attributes.put("status", value);
    }

    // TODO: Need to create a proper dt object
    /*
     * This is only used within the class when a new task is created.
     */
    private void setCreationDateTime() {
        setCreationDateTime("now");
    }

    // TODO: Need to take a proper dt object
    public void setCreationDateTime(String value) {
        creationDateTime = value;
        attributes.put("creationDateTime", value);
    }

    // TODO: Need to take a proper dt object
    public void setModificationDateTime(String value) {
        modificationDateTime = value;
        attributes.put("modificationDateTime", value);
    }

    // TODO: Need to take a proper dt object
    public void setActualStartDateTime(String value) {
        actualStartDateTime = value;
        attributes.put("actualstartdate", value);
    }

    public void setSubject(String value) {
        this.subject = value;
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
        this.ID = value;
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

    public int getStatus() {
        return status;
    }

    public String getSubject() {
        return subject;
    }

    public String getID() {
        return ID;
    }

    public String getDescription() {
        return description;
    }

    // TODO: Should return some kind of date time object
    public String getCreationDateTime() {
        return creationDateTime;
    }

    // TODO: Should return some kind of date time object
    public String getModificationDateTime() {
        return modificationDateTime;
    }

    public String getActualStartDateTime() {
        return actualStartDateTime;
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
        return attributes.get("bgColor");
    }

    // TODO: Can this be modified outside this object if a caller gets a handle on this ?
    public HashMap<String, String> getUnsupportedAttributes() {
        return unsupportedItems;
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
     * @return boolean - true if childMap has no entried, otherwise false
     */
    public boolean hasChildren() {
        if (childMap.size() == 0) return true;
        return false;
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
