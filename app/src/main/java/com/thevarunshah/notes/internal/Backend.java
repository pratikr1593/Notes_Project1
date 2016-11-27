package com.thevarunshah.notes.internal;

import android.content.Context;
import android.util.Log;

import com.thevarunshah.notes.data.Note;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Backend {

    private static final String TAG = "Backend";
    private static AtomicInteger idCounter = new AtomicInteger();

    private static ArrayList<Note> notes = new ArrayList<Note>();

    public static Note getNote(int id){

        for(Note n : notes){
            if(n.getId() == id){
                return n;
            }
        }
        return null;
    }

    /**
     * creates a new file in internal memory and writes to it
     *
     * @param context the application context
     */
    public static void writeData(Context context){

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            //open file and and write the last id and notes list to it
            fos = context.openFileOutput("notes.ser", Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeInt(idCounter.getAndIncrement());
            oos.writeObject(notes);
        } catch (Exception e) {
            Log.i(TAG, "could not write to file");
            Log.i(TAG, e.getMessage());
        } finally{
            try{
                if(oos != null) oos.close();
                if(fos != null) fos.close();
            } catch (Exception e){
                Log.i(TAG, "could not close the file");
                Log.i(TAG, e.getMessage());
            }
        }
    }

    /**
     * reads from serialized file in internal memory
     *
     * @param context the application context
     */
    public static void readData(Context context){

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            //open file and read the last id and note lists from it
            fis = context.openFileInput("notes.ser");
            ois = new ObjectInputStream(fis);

            idCounter.set(ois.readInt());
            ArrayList<Note> notesList = (ArrayList<Note>) ois.readObject();
            if(notesList != null){
                notes.clear();
                notes.addAll(notesList);
            }
        } catch (Exception e) {
            Log.i(TAG, "could not read from file");
            Log.i(TAG, e.getMessage());
        } finally{
            try{
                if(ois != null) ois.close();
                if(fis != null) fis.close();
            } catch(Exception e){
                Log.i(TAG, "could not close the file");
                Log.i(TAG, e.getMessage());
            }
        }
    }

    public static int getNextID() {
        return idCounter.incrementAndGet();
    }

    public static ArrayList<Note> getNotes() {
        return notes;
    }
}
