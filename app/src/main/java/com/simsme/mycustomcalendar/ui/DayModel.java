package com.simsme.mycustomcalendar.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DayModel {
    private DateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private int type = 102031; //뷰타입
    private Calendar CalendarModel = null;

    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDate() {

        Date d = new Date(getCalendarModel().getTimeInMillis()); //현재날짜..?
        return defaultDateFormat.format(d);
    }

    public Calendar getCalendarModel() {
        return CalendarModel;
    }

    public int getType() {
        return type;
    }

    public void setCalendarModel(Calendar calendarModel) {
        CalendarModel = calendarModel;
    }

    public void setType(int type) {
        this.type = type;
    }
}
