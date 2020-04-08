package net.cararea.inspector.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.Calendar;

import jp.co.recruit_mp.android.lightcalendarview.Month;
import jp.co.recruit_mp.android.lightcalendarview.WeekDay;

public class Converters {
    public static int pxToDp(float pixels, DisplayMetrics metrics) {
        float logicalDensity = metrics.density;
        return (int) (pixels / logicalDensity + 0.5f);
    }

    public static int dpToPx(float dp, Resources res) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                res.getDisplayMetrics()) + 0.5f);
    }

    public static WeekDay calendarIntToWeekDay(int calendarWeekDay) {
        switch (calendarWeekDay) {
            case Calendar.MONDAY:
                return WeekDay.MONDAY;
            case Calendar.TUESDAY:
                return WeekDay.TUESDAY;
            case Calendar.WEDNESDAY:
                return WeekDay.WEDNESDAY;
            case Calendar.THURSDAY:
                return WeekDay.THURSDAY;
            case Calendar.FRIDAY:
                return WeekDay.FRIDAY;
            case Calendar.SATURDAY:
                return WeekDay.SATURDAY;
            default:
                return WeekDay.SUNDAY;
        }
    }

    public static Month calendarIntToMonth(int calendarMonth) {
        switch (calendarMonth) {
            case Calendar.FEBRUARY:
                return Month.FEBRUARY;
            case Calendar.MARCH:
                return Month.MARCH;
            case Calendar.APRIL:
                return Month.APRIL;
            case Calendar.MAY:
                return Month.MAY;
            case Calendar.JUNE:
                return Month.JUNE;
            case Calendar.JULY:
                return Month.JULY;
            case Calendar.AUGUST:
                return Month.AUGUST;
            case Calendar.SEPTEMBER:
                return Month.SEPTEMBER;
            case Calendar.OCTOBER:
                return Month.OCTOBER;
            case Calendar.NOVEMBER:
                return Month.NOVEMBER;
            case Calendar.DECEMBER:
                return Month.DECEMBER;
            default:
                return Month.JANUARY;
        }
    }
}
