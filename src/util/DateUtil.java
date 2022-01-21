package jtaskui.util;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    // Fields for Date and Time formats
    public static final String HIGH_PRECISION_FORMAT  = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    public static final String LOW_PRECISION_FORMAT   = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAY_FORMAT         = "yyyy-MM-dd HH:mm";
    // Fields for Date and Time Formatters
    public static final DateTimeFormatter HIGH_PRECISION_FORMATTER  = DateTimeFormatter.ofPattern(HIGH_PRECISION_FORMAT);
    public static final DateTimeFormatter LOW_PRECISION_FORMATTER   = DateTimeFormatter.ofPattern(LOW_PRECISION_FORMAT);
    public static final DateTimeFormatter DISPLAY_FORMATTER         = DateTimeFormatter.ofPattern(DISPLAY_FORMAT);

    // This is a helper class and only contains static methods/fields. No instantiation allowed.
    private DateUtil() { }

    /**
     * Parse Strings containing dates and times into LocalDateTime objects
     *
     * Note: If parsing fails an error will be emitted to standard error and the current date and time now will be returned instead.
     * Because I want the app to continue without having to do excessive work in TaskObj for every use of this helper, I choose to accept
     * a String to be printed if there is a parsing error to help me to see what didn't parse. I have found that TaskCoach doesn't enforce
     * consistent approach to the format of dates and times between the same field across different Tasks etc.
     *
     * @param dateTime - String representation of the date and time to parse
     * @param inFormat - DateTimeFormatter in the format of dateTime
     * @param errMsg   - String error message to print if there is a parsing error
     * @return LocalDateTime - parsed using the inFormat
     *
     */
    public static LocalDateTime parseDateTime(String dateTime, DateTimeFormatter inFormat, String errMsg) {
        try {
            if(dateTime == null) return LocalDateTime.now();
            return parseDateTime(dateTime, inFormat);
        }
        catch (DateTimeParseException e) {
            if(errMsg != null) System.err.println(errMsg);
            System.err.println("ERROR: Failed to parse " + dateTime + " - Expected format " + inFormat);
            return LocalDateTime.now();
        }
    }

    /**
     * Same asparseDateTime(String, DateTimeFormatter, String) version of this method except there is no protection.
     * If the date doesn't parse then a DateTimeParseException will be thrown. This is more useful than the aforementioned
     * method if the caller wants to know if the String parsed or not.
     *
     * @param dateTime - String representation of the date and time to parse
     * @param inFormat - DateTimeFormatter in the format of dateTime
     * @return LocalDateTime - parsed using the inFormat
     * @throws DateTimeParseException
     */
    public static LocalDateTime parseDateTime(String dateTime, DateTimeFormatter inFormat) throws DateTimeParseException {
        return LocalDateTime.parse(dateTime, inFormat);
    }

    /**
     * This method will accept a LocalDateTime and return a String formatted to outFormat. The boolean relativeDates will enable
     * the use of words such as Today and Tomorrow as return Strings if the dateTime LocalDateTime object meets those relative terms.
     * Parsing failures will return the String "N/A" which follows the TaskCoach behaviour. 
     *
     * @param dateTime - LocalDateTime object that needs formatting to a String
     * @param outFormat - DateTimeFormatter object to use to format the return String
     * @param relativeDates - boolean, true enables relative words such as Today and Tomorrow instead of the full date and time in the outFormat
     *
     * @return String - Date formatted using the DateTimeFormatter, N/A if parsing fails or a relative word such as Today if enabled.
     */
    public static String formatToString(LocalDateTime dateTime, DateTimeFormatter outFormat, boolean relativeDates) {
        try {
            // If relative dates have been enabled
            if (relativeDates) {
                // Get the Date now
                LocalDate dateTest = LocalDateTime.now().toLocalDate();
                // Get the Date portion of the passed in Date and Time
                LocalDate datePortion = dateTime.toLocalDate();
                // If these are equal then return Today
                if(dateTest.equals(datePortion)) return "Today";
                // Take a day so to test Yesterday
                dateTest = dateTest.minusDays(1);
                // If its equal now, return Yesterday
                if(dateTest.equals(datePortion)) return "Yesterday";
                // Add two days to test Tomorrow
                dateTest = dateTest.plusDays(2);
                // If its equal, return Tomorrow
                if(dateTest.equals(datePortion)) return "Tomorrow";
            }
            // Return a String formatted Date and Time
            return dateTime.format(outFormat);
        }
        // If the LocalDateTime or DateTimeFormatter passed is null, return "N/A" in keeping with TaskCoach behaviour
        catch(NullPointerException npe) {
            return "N/A";
        }
        // If the date can't be parsed, this is likely due to inconsistencies in the storage format used by the same field in TaskCoach.
        // print an error to the console and return "N/A"
        catch(DateTimeParseException dtpe) {
            // TODO: Make errors like this come up either in a console or as an error message
            System.err.println("Error parsing date/time for " + dateTime + " in " + outFormat + " format");
            return "N/A";
        }   
    }

    /**
     * This method returns the number of seconds between two LocalDateTime objects.
     *
     * @param firstDate - LocalDateTime of the first date to test
     * @param secondDate - LocalDateTime of the second date to test
     * @return long - Number of seconds between the first and the second LocalDateTime objects
     */
    public static long secondsBetween(LocalDateTime firstDate, LocalDateTime secondDate) {
        return ChronoUnit.SECONDS.between(firstDate, secondDate);
    }
}
