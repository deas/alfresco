package org.alfresco.share.util;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.activiti.engine.impl.cmd.SetExecutionVariablesCmd;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * Methods that generates valid data type values
 * 
 * @author Corina.Nechifor
 */

public class CalendarUtil
{

    public static String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
            "December" };
    public static String[] weekDays = { "", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    private static Map<String, String> valuesForCalendar = new HashMap<String, String>();

    /**
     * Return valid startTime and endTime for a single day based on current day
     * 
     * @param fieldForCalendar
     *            - the calendar field
     * @param timeToAdd
     *            - specified amount of time
     * @return
     */
    public static Map<String, String> setTimeForSingleDay(String startDateTime, String endDateTime, boolean

    allDay)
    {

        setDefaultValues();

        valuesForCalendar.put("startTime", startDateTime);
        valuesForCalendar.put("endTime", endDateTime);

        setStartDateAndEndDateValues(allDay);

        return valuesForCalendar;

    }

    /**
     * Adds months,days on the current date
     * 
     * @param addMonths
     * @param addDays
     * @param addHours
     * @param allDay
     * @return
     */
    public static Map<String, String> addValuesToCurrentDate(int addMonths, int addDays, String startTime,

    String endTime, boolean allDay)
    {

        setTimeForSingleDay(startTime, endTime, allDay);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, addDays);
        calendar.add(Calendar.MONTH, addMonths);

        valuesForCalendar.put("endYear", String.valueOf(calendar.get(Calendar.YEAR)));
        valuesForCalendar.put("endMonth", monthNames[calendar.get(Calendar.MONTH)]);
        valuesForCalendar.put("endDay", String.valueOf(calendar.get(Calendar.DATE)));

        int dateWeekUpdated = calendar.get(Calendar.DAY_OF_WEEK);
        valuesForCalendar.put("endDayOfWeek", weekDays[dateWeekUpdated]);

        setStartDateAndEndDateValues(allDay);
        return valuesForCalendar;

    }

    /***
     * Set startTime, startDay, startMonth, startYear, endTime, endDay,
     * endMonth, endYear of current date of current date
     */
    private static void setDefaultValues()
    {

        Calendar calendar = Calendar.getInstance();

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int currentMonth = calendar.get(Calendar.MONTH);

        valuesForCalendar.clear();
        valuesForCalendar.put("startDay", String.valueOf(currentDay));
        valuesForCalendar.put("endDay", String.valueOf(currentDay));

        valuesForCalendar.put("startDayOfWeek", weekDays[weekDay]);
        valuesForCalendar.put("endDayOfWeek", weekDays[weekDay]);

        valuesForCalendar.put("startMonth", monthNames[currentMonth]);
        valuesForCalendar.put("endMonth", monthNames[currentMonth]);

        valuesForCalendar.put("startYear", String.valueOf(currentYear));
        valuesForCalendar.put("endYear", String.valueOf(currentYear));

        DateFormat sdf = DateFormat.getTimeInstance(DateFormat.SHORT);
        String time = sdf.format(calendar.getTime());

        valuesForCalendar.put("startTime", time);
        valuesForCalendar.put("endTime", time);

    }

    /**
     * Converts the date value to new time zone
     * 
     * @param dateValue
     * @param hours
     * @return
     * @throws Exception
     */
    public static String convertDateToNewTimeZone(String dateValue, String initialTZ, String finalTZ, boolean allday) throws Exception
    {
        SimpleDateFormat simpleDateFormat;
        String convertedDate;

        if (allday)
        {
            simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM, yyyy");
        }
        else
        {
            simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM, yyyy hh:mm a");
        }
        Date date = simpleDateFormat.parse(dateValue);

        // creates calendar
        Calendar calendar = Calendar.getInstance();

        // sets calendar date
        calendar.setTime(date);

        int hours = getHoursDifferences(initialTZ, finalTZ);

        calendar.add(Calendar.HOUR_OF_DAY, hours);

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int currentMonth = calendar.get(Calendar.MONTH);
        DateFormat sdf = DateFormat.getTimeInstance(DateFormat.SHORT);
        String newtime = sdf.format(calendar.getTime());

        if (allday)
        {
            convertedDate = String.format("%s, %s %s, %s", weekDays[weekDay], String.valueOf(currentDay),

            monthNames[currentMonth], String.valueOf(currentYear));
        }
        else
        {
            convertedDate = String.format("%s, %s %s, %s %s", weekDays[weekDay], String.valueOf

            (currentDay), monthNames[currentMonth], String.valueOf(currentYear), newtime);
        }
        return convertedDate;

    }

    /**
     * Returns the time in format h:mm a
     * 
     * @param dateValue
     * @param hours
     * @return
     * @throws Exception
     */
    public static String getTimeFromDate(String dateValue) throws Exception
    {
        SimpleDateFormat actualFormat = new SimpleDateFormat("EEEE, dd MMMM, yyyy hh:mm a");
        Date date = actualFormat.parse(dateValue);

        DateFormat sdf = DateFormat.getTimeInstance(DateFormat.SHORT);
        String newtime = sdf.format(date);
        return newtime;

    }

    /**
     * Returns the date value in new format
     * 
     * @param dateValue
     * @param hours
     * @return
     * @throws Exception
     */
    public static String getDateInFormat(String dateValue, String newFormatValue, boolean allDay) throws Exception
    {
        String formatDate;
        if (allDay)
            formatDate = "EEEE, dd MMMM, yyyy";
        else
            formatDate = "EEEE, dd MMMM, yyyy hh:mm a";

        SimpleDateFormat actualFormat = new SimpleDateFormat(formatDate);
        Date date = actualFormat.parse(dateValue);

        SimpleDateFormat newFormat = new SimpleDateFormat(newFormatValue);
        String newValue = newFormat.format(date);
        return newValue;

    }

    /**
     * Returns the difference between time zones
     * 
     * @param initialTZ
     * @param finalTZ
     * @return
     */
    public static int getHoursDifferences(String initialTZ, String finalTZ)
    {

        initialTZ = initialTZ.replace("Bucharest", "GMT+02:00").replace("London", "GMT");
        finalTZ = finalTZ.replace("Bucharest", "GMT+02:00").replace("London", "GMT");

        TimeZone tz1 = TimeZone.getTimeZone(initialTZ);

        TimeZone tz2 = TimeZone.getTimeZone(finalTZ);

        long timeDifference = tz2.getRawOffset() - tz1.getRawOffset() + tz2.getDSTSavings() -

        tz1.getDSTSavings();

        int hours = (int) TimeUnit.MILLISECONDS.toHours(timeDifference);

        return hours;

    }

    
    /**
     * Change the time zone of the system
     * @param name
     * @throws IOException
     */
    public static void changeTimeZone(String name) throws IOException
    {

        String winTimeZone = "";
        String linuxTimeZone = "";

        switch (name)
        {
            case "London":
                winTimeZone = "tzutil /s \"GMT Standard Time\"";
                linuxTimeZone = "ln -s /usr/share/zoneinfo/Europe/London /etc/localtime";
                break;
            case "Bucharest":
                winTimeZone = "tzutil /s \"GTB Standard Time\"";
                linuxTimeZone = "ln -s /usr/share/zoneinfo/Europe/Bucharest /etc/localtime";
                break;
            default:
                winTimeZone = "tzutil /s \"GMT Standard Time\"";
                linuxTimeZone = "ln -s /usr/share/zoneinfo/Europe/London /etc/localtime";
                break;
        }

        if (System.getProperty("os.name").contains("Windows"))
        {
            Runtime.getRuntime().exec(winTimeZone);
        }
        else
        {
            Runtime.getRuntime().exec(linuxTimeZone);
        }

    }

    /**
     * Sets startDate and endDate in specified format
     * 
     * @param allDay
     */

    private static void setStartDateAndEndDateValues(boolean allDay)
    {

        String startDateValue;
        String endDateValue;

        if (allDay)
        {
            startDateValue = String.format("%s, %s %s, %s", valuesForCalendar.get("startDayOfWeek"),

            valuesForCalendar.get("startDay"), valuesForCalendar.get("startMonth"), valuesForCalendar.get("startYear"));
            endDateValue = String.format("%s, %s %s, %s", valuesForCalendar.get("endDayOfWeek"),

            valuesForCalendar.get("endDay"), valuesForCalendar.get("endMonth"), valuesForCalendar.get("endYear"));

        }
        else
        {
            startDateValue = String.format("%s, %s %s, %s %s", valuesForCalendar.get("startDayOfWeek"),

            valuesForCalendar.get("startDay"), valuesForCalendar.get("startMonth"), valuesForCalendar.get("startYear"),

            valuesForCalendar.get("startTime"));
            endDateValue = String.format("%s, %s %s, %s %s", valuesForCalendar.get("endDayOfWeek"),

            valuesForCalendar.get("endDay"), valuesForCalendar.get("endMonth"), valuesForCalendar.get("endYear"),

            valuesForCalendar.get("endTime"));
        }
        valuesForCalendar.put("startDateValue", startDateValue);
        valuesForCalendar.put("endDateValue", endDateValue);

    }

}
