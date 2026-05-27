package com.josephb.lostandfoundapp;

public final class TimeFormatter {

    private TimeFormatter() {}

    public static String relative(long timestampMillis) {
        long now = System.currentTimeMillis();
        long diff = now - timestampMillis;

        if (diff < 0) return "Just now";

        long sec = diff / 1000L;
        long min = sec / 60L;
        long hr = min / 60L;
        long day = hr / 24L;

        if (sec < 60) return "Just now";
        if (min < 60) return min + (min == 1 ? " minute ago" : " minutes ago");
        if (hr < 24) return hr + (hr == 1 ? " hour ago" : " hours ago");
        if (day < 7) return day + (day == 1 ? " day ago" : " days ago");
        if (day < 30) {
            long weeks = day / 7L;
            return weeks + (weeks == 1 ? " week ago" : " weeks ago");
        }
        if (day < 365) {
            long months = day / 30L;
            return months + (months == 1 ? " month ago" : " months ago");
        }
        long years = day / 365L;
        return years + (years == 1 ? " year ago" : " years ago");
    }
}