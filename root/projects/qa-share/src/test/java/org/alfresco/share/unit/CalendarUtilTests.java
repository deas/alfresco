package org.alfresco.share.unit;

import java.util.Calendar;
import java.util.Map;

import org.alfresco.share.util.CalendarUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CalendarUtilTests
{

    public static String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
            "December" };

    @BeforeClass
    public void setUp() throws Exception
    {

    }

    @AfterClass(groups = { "alfresco-one" })
    public void deleteSite()
    {

    }

    // positive test unit for addValuesToCurrent()
    @Test
    public void addValuesToCurrentDatePositiveTest()
    {
        Map<String, String> calendarUtilValues = CalendarUtil.addValuesToCurrentDate(2, 2, "7:00 AM", "9:00 AM", false);
        Calendar calendar = Calendar.getInstance();

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        Assert.assertEquals(calendarUtilValues.get("endDay"), String.valueOf(currentDay + 2));
        Assert.assertEquals(calendarUtilValues.get("endMonth"), monthNames[currentMonth + 2]);
        Assert.assertEquals(calendarUtilValues.get("endYear"), String.valueOf(currentYear));
    }

    // negative test unit for addValuesToCurrent()
    @Test
    public void addValuesToCurrentDateNegativeTest()
    {
        Map<String, String> calendarUtilValues = CalendarUtil.addValuesToCurrentDate(2, 2, "7:00 AM", "9:00 AM", false);
        Calendar calendar = Calendar.getInstance();

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);

        Assert.assertNotEquals(calendarUtilValues.get("endDay"), String.valueOf(currentDay));
        Assert.assertNotEquals(calendarUtilValues.get("endMonth"), monthNames[currentMonth]);
    }

    // positive test unit for setTimeForSingleDay()
    @Test
    public void setTimeForSingleDayPositiveTest()
    {
        Map<String, String> currentDateTimes = CalendarUtil.setTimeForSingleDay("7:00 AM", "9:00 AM", false);

        Calendar calendar = Calendar.getInstance();

        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        Assert.assertEquals(currentDateTimes.get("endDay"), String.valueOf(currentDay));
        Assert.assertEquals(currentDateTimes.get("endMonth"), monthNames[currentMonth]);
        Assert.assertEquals(currentDateTimes.get("endYear"), String.valueOf(currentYear));
        Assert.assertEquals(currentDateTimes.get("startTime"), "7:00 AM");
        Assert.assertEquals(currentDateTimes.get("endTime"), "9:00 AM");

    }

    // negative test unit for setTimeForSingleDay()
    @Test
    public void setTimeForSingleDayNegativeTest()
    {
        Map<String, String> currentDateTimes = CalendarUtil.setTimeForSingleDay("5:00 AM", "7:00 AM", false);

        Assert.assertNotEquals(currentDateTimes.get("startTime"), "7:00 AM");
        Assert.assertNotEquals(currentDateTimes.get("endTime"), "9:00 AM");

    }

    // positive test unit for getDateInFormat()
    @Test
    public void getDateInFormatPositiveTest() throws Exception
    {
        String actualDateFormat = CalendarUtil.getDateInFormat("Monday, 11 July, 2014 3:40 AM", "dd MMMM, yyyy h:mm a", false);

        Assert.assertEquals(actualDateFormat, "11 July, 2014 3:40 AM");
    }

    // positive test unit for getDateInFormat() with AllDay option
    @Test
    public void getDateInFormatAllDayPositiveTest() throws Exception
    {
        String actualDateFormat = CalendarUtil.getDateInFormat("Friday, 11 July, 2014 3:40 AM", "dd MMMM, yyyy", true);

        Assert.assertEquals(actualDateFormat, "11 July, 2014");
    }

    // negative test unit for getDateInFormat()
    @Test
    public void getDateInFormatNegativeTest() throws Exception
    {
        String actualDateFormat = CalendarUtil.getDateInFormat("Friday, 11 July, 2014 3:40 AM", "dd MMMM, yyyy h:mm a", false);

        Assert.assertNotEquals(actualDateFormat, "11 July, 2014");
    }

    @Test
    public void getHoursDifferencesPositiveTest()
    {
        int diffHours = CalendarUtil.getHoursDifferences("GMB Standard Time", "GTB Standard Time");

        Assert.assertEquals(diffHours, 2);
    }

    @Test
    public void getHoursDifferencesNegativeTest()
    {
        int diffHours = CalendarUtil.getHoursDifferences("GMB Standard Time", "GTB Standard Time");

        Assert.assertNotEquals(diffHours, -2);
    }

    @Test
    public void convertDateToNewTimeZonePositiveTest() throws Exception
    {
        String convertedDate = CalendarUtil.convertDateToNewTimeZone("Friday, 11 July, 2014 3:40 AM", "GMB Standard Time", "GTB Standard Time", false);

        Assert.assertEquals(convertedDate, "Friday, 11 July, 2014 5:40 AM");
    }

    @Test
    public void convertDateToNewTimeZoneAllDayPositiveTest() throws Exception
    {
        String convertedDate = CalendarUtil.convertDateToNewTimeZone("Friday, 11 July, 2014 3:40 AM", "GMB Standard Time", "GTB Standard Time", true);

        Assert.assertNotEquals(convertedDate, "11 July, 2014");
    }

    @Test
    public void convertDateToNewTimeZoneNegativeTest() throws Exception
    {
        String convertedDate = CalendarUtil.convertDateToNewTimeZone("Friday, 11 July, 2014 3:40 AM", "GMB Standard Time", "GTB Standard Time", false);

        Assert.assertNotEquals(convertedDate, "Friday, 11 July, 2014 1:40 AM");
    }

    @Test
    public void getTimeFromDatePozitiveTest() throws Exception
    {
        String timeOfDate = CalendarUtil.getTimeFromDate("Friday, 11 July, 2014 5:40 AM");

        Assert.assertEquals(timeOfDate, "5:40 AM");
    }

    @Test
    public void getTimeFromDateNegativeTest() throws Exception
    {
        String timeOfDate = CalendarUtil.getTimeFromDate("Friday, 11 July, 2014 5:40 AM");

        Assert.assertNotEquals(timeOfDate, "11 July, 2014 5:40 AM");
        Assert.assertNotEquals(timeOfDate, "5:40 PM");
    }

}
