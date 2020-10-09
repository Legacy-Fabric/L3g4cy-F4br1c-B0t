package io.github.boogiemonster1o1.legacyfabricbot;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

public class TimeTest {
    @Test
    public void testTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        String month = "" + cal.get(Calendar.MONTH);
        if (month.length() == 1) {
            month = "0" + month;
        }
        String day = "" + cal.get(Calendar.DAY_OF_MONTH);
        if (day.length() == 1) {
            day = "0" + day;
        }
        String date = "" + cal.get(Calendar.YEAR) + "-" + month + "-" + day;
        System.out.println(date);
    }
}
