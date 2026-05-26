package com.josephb.personaleventplannerapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat DISPLAY_FORMATTER =
            new SimpleDateFormat("EEE, d MMM yyyy · h:mm a", Locale.getDefault());

    public static String format(long millis) {
        return DISPLAY_FORMATTER.format(new Date(millis));
    }
}
