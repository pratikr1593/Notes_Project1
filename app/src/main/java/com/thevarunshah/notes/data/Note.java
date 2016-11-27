package com.thevarunshah.notes.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class Note implements Serializable{

    private static final long serialVersionUID = 1L; //for serializing data

    private int id;
    private String name = "";
    private Date date;

    public static Comparator<Note> BY_NAME = new Comparator<Note>() {
        @Override
        public int compare(Note one, Note two) {
            return one.getName().compareTo(two.getName());
        }
    };

    public static Comparator<Note> BY_DATE = new Comparator<Note>() {
        @Override
        public int compare(Note one, Note two) {
            return two.date.compareTo(one.date);
        }
    };

    public Note(int id, String name){

        this.id = id;
        this.name = name;
        this.date = Calendar.getInstance().getTime();
    }

    public int getId(){
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateDate() {
        this.date = Calendar.getInstance().getTime();
    }

    public String getFormattedDate(){
        if(date == null){
            updateDate();
        }
        return (new SimpleDateFormat("MMM dd, h:mm a")).format(this.date);
    }
}
