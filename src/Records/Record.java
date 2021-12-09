package jtaskui.Records;

import jtaskui.util.DateUtil;

import java.util.UUID;

/**
 * Class implementing attributes common to all Records
 */
public class Record {
    // Status always appears to be "1"
    private String status;
    // TODO: Some bits appear to remain the same in the XMLs I have looked through so I think I should figure this out at some point
    // The id is a UUID
    private String id;
    private String description;
    private boolean hasDescription;

    /**
     * Default constructor to set sane defaults
     */
    public Record() {
        status = "1";
        // Set this to false until the setter is called
        hasDescription = false;
    }

    /**
     *   Setters
     */

    /**
     * Set the ID for this Record
     *
     * @param String - ID (UUID) for this Record to set
     */
    public void setID(String newID) {
        this.id = newID;
    }

    /** 
     * Set the status attribute.
     *
     * @param String - value to set as the status
     */
    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

    /**
     * Set the description for this Record
     *
     * @param String - value to set as the description
     */
    public void setDescription(String newDescription) {
        this.description = newDescription;
        if(newDescription == "" || newDescription == null) hasDescription = false;
        else hasDescription = true;
    }

    /**
     *   Getters
     */

    /**
     * Get the UUID for this Record. If no UUID has been set, a new random UUID will be generated, set and returned.
     *
     * @return String - UUID for this Record
     */
    public String getID() {
        // If the ID is requested but is still null, create a new random UUID
        if(this.id == null) setID(UUID.randomUUID().toString());
        return this.id;
    }

    /**
     * Return the status attribute of this Task
     *
     * @return String - status attribute of the Record
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Return the description for this Record
     *
     * @return String - Description attribute for this Record
     */
    public String getDescription() {
        return this.description;
    }

    /**
     *   Tests
     */

    /**
     * Returns true if a description is set and not an empty String, false otherwise.
     *
     * @return boolean - Returns true if description is not empty String or null, false otherwise
     */
    public boolean hasDescription() {
        return hasDescription;
    }
}
