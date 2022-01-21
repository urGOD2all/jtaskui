package jtaskui.Records;

import jtaskui.Records.Record;
import jtaskui.util.DateUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This type of Record have Creation and Modification dates and a Subject attribute. This class adds this.
 */
public abstract class DatedRecord extends Record {
    public static final DateTimeFormatter CREATION_DATE_FORMATTER = DateUtil.HIGH_PRECISION_FORMATTER;
    public static final DateTimeFormatter MODIFICATION_DATE_FORMATTER = DateUtil.HIGH_PRECISION_FORMATTER;

    // Subject of this record
    private String subject;
    private LocalDateTime creationDateTime;
    private LocalDateTime modificationDateTime;
    private boolean hasModificationDateTime;

    /**
     * Setup this class
     */
    public DatedRecord() {
        // Call the default constructor for Record
        super();
        // Add the methods that get the String attributes for all the attributes in this class
        super.addAttributeGetter("subject", () -> getSubject());
        super.addAttributeGetter("creationDateTime", () -> DateUtil.formatToString(getCreationDateTime(), CREATION_DATE_FORMATTER, false));
        // Set false unless the setter is called
        this.hasModificationDateTime = false;
    }

    /**
     *   Setters
     */

    /**
     * Sets the subject
     *
     * @param String - The subject to set
     */
    public void setSubject(String newSubject) {
        this.subject = newSubject;
    }

    /**
     * Sets the creation date and time attribute.
     *
     * @param LocalDateTime - Time and date to set as creation date/time
     */
    public void setCreationDateTime(LocalDateTime newCreationDateTime) {
        this.creationDateTime = newCreationDateTime;
    }

    /**
     * Sets the modification date and time attribute.
     *
     * @param LocalDateTime - Time and date to set as modification date/time
     */
    public void setModificationDateTime(LocalDateTime newModificationDateTime) {
        this.modificationDateTime = newModificationDateTime;
        if(hasModificationDateTime() == false) {
            hasModificationDateTime = true;
            super.addAttributeGetter("modificationDateTime", () -> DateUtil.formatToString(getModificationDateTime(), MODIFICATION_DATE_FORMATTER, false));
        }
    }

    /**
     *   Getters
     */

    /**
     * Get the subject
     *
     * @return String - subject of this Record
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * Returns a LocalDateTime object containing the creation date/time
     *
     * @return LocalDateTime - Creation date/time
     */
    public LocalDateTime getCreationDateTime() {
        return this.creationDateTime;
    }

    /**
     * Returns a LocalDateTime object containing the modification date/time
     *
     * @return LocalDateTime - modification date/time
     */
    public LocalDateTime getModificationDateTime() {
        return this.modificationDateTime;
    }

    /**
     *   Parsing
     */

// TODO: These parsing methods should use the method that provides the Exception and deal with the issues here. Otherwise we may get a duff value back and not know! - What will I do about it though?
    /** 
     * Parse a String into a LocalDateTime and store it as the creation date/time. The String must be in the correct format to be parsed as specified by the CREATION_DATE_FORMATTER field
     *
     * @param String - Date and time in CREATION_DATE_FORMATTER format to be parsed
     */
    public void parseCreationDateTime(String newCreationDateTime) {
        this.setCreationDateTime(DateUtil.parseDateTime(newCreationDateTime, CREATION_DATE_FORMATTER, getSubject() + " Creation Date"));
    }

    /**
     * Parse a String into a LocalDateTime and store it as the modification date/time. The String must be in the correct format to be parsed as specified by the MODIFICATION_DATE_FORMATTER field
     *
     * @param String - Date and time in MODIFICATION_DATE_FORMATTER format to be parsed
     */
    public void parseModificationDateTime(String newModificationDateTime) {
        this.setModificationDateTime(DateUtil.parseDateTime(newModificationDateTime, MODIFICATION_DATE_FORMATTER, getSubject() + " Modification Date"));
    }

    /**
     *   Tests
     */

    /**
     * Returns true if this Record has a modification date, false otherwise.
     * This state can only happen when something is created and then not modified
     *
     * @return boolean - Returns true if modification date/time is set, false otherwise
     */
    public boolean hasModificationDateTime() {
        return hasModificationDateTime;
    }
}
