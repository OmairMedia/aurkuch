package com.basitobaid.youtubeapp.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class DateUtil {

    public static String calculateTimeAgo(String lastTime){
        System.out.println("POST DATE: "+lastTime);
        String stDate = getFormattedDateWithTime(lastTime);
        Log.d("TAG", "calculateTimeAgo: " + stDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        String ago ="";
        try {
            Date date = sdf.parse(Objects.requireNonNull(stDate));
            ago = (String) android.text.format.DateUtils.getRelativeTimeSpanString(Objects.requireNonNull(date).getTime() ,
                    Calendar.getInstance().getTimeInMillis(), android.text.format.DateUtils.MINUTE_IN_MILLIS);
            System.out.println("Time Ago: "+ago);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ago;
    }

    private static String getFormattedDateWithTime(String oldDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsedOld = dateFormat.parse(oldDate);

            TimeZone tz = TimeZone.getTimeZone("GMT+10");
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            destFormat.setTimeZone(tz);

            return destFormat.format(Objects.requireNonNull(parsedOld));

//            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
//                    .parse(oldDate);
//            SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
//            return dt1.format(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
