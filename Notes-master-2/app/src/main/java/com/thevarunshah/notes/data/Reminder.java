package com.thevarunshah.notes.data;

import java.util.Calendar;
import java.util.Date;

public class Reminder extends Note {

    private Date due;
    private boolean noNotification;
    private int notificationTime;
    private int notificationUnit;
    private String notes;

    public Reminder(int id, String name){

        super(id, name);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24);
        this.due = cal.getTime();
        this.noNotification = false;
        this.notificationTime = 1;
        this.notificationUnit = 0;
    }

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }

    public boolean isNoNotification() {
        return noNotification;
    }

    public void setNoNotification(boolean noNotification) {
        this.noNotification = noNotification;
    }

    public int getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(int notificationTime) {
        this.notificationTime = notificationTime;
    }

    public int getNotificationUnit() {
        return notificationUnit;
    }

    public void setNotificationUnit(int notificationUnit) {
        this.notificationUnit = notificationUnit;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
