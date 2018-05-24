package com.empoluboyarov.reminder;

import java.text.SimpleDateFormat;


public class Utils {

    public static String getData(long date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(date);
    }

    public static String getTime(long time){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(time);
    }

    public static String getFullDate(long date){
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return fullDateFormat.format(date);
    }
}
