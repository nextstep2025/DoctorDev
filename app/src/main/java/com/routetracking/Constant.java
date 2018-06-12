package com.routetracking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Constant {


    public static String STARTTRACKTIME;
    public static String ENDTRACKTIME;


    public static String getCurrentDatenTime() {
//        TimeZone tz = TimeZone.getTimeZone("UTC");
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()); // Quoted "Z" to indicate UTC, no timezone offset
//        df.setTimeZone(tz);
//        String nowAsISO = df.format(new Date());
//
//        System.out.println("nowAsISO = " + nowAsISO);
//
//        return nowAsISO;



        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault());
        Date date = new Date();
        return formatter.format(date);

    }

//    public static void getTimeDifference(){
//
////        String dateStart = Constant.STARTTRACKTIME;
////        String dateStop = Constant.ENDTRACKTIME;
//
//        String dateStart = "01/15/2012 09:29:00";
//        String dateStop = "01/15/2012 09:29:30";
//
//        //HH converts hour in 24 hours format (0-23), day calculation
//        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//
//        Date d1 = null;
//        Date d2 = null;
//
//        try {
//            d1 = format.parse(dateStart);
//            d2 = format.parse(dateStop);
//
//            //in milliseconds
//            long diff = d2.getTime() - d1.getTime();
//
//            long diffSeconds = diff / 1000 % 60;
//            long diffMinutes = diff / (60 * 1000) % 60;
//            long diffHours = diff / (60 * 60 * 1000) % 24;
//            long diffDays = diff / (24 * 60 * 60 * 1000);
//
//            System.out.print(diffDays + " days, ");
//            System.out.print(diffHours + " hours, ");
//            System.out.print(diffMinutes + " minutes, ");
//            System.out.print(diffSeconds + " seconds.");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}
