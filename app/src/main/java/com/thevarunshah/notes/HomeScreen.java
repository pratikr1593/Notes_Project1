package com.thevarunshah.notes;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.thevarunshah.notes.data.Checklist;
import com.thevarunshah.notes.data.ListNote;
import com.thevarunshah.notes.data.Note;
import com.thevarunshah.notes.data.Reminder;
import com.thevarunshah.notes.data.TextNote;
import com.thevarunshah.notes.internal.Backend;
import com.thevarunshah.notes.internal.DividerItemDecoration;
import com.thevarunshah.notes.internal.NoteAdapter;
import com.thevarunshah.notes.internal.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    final private String TAG = "HomeScreen";
    private NoteAdapter notesAdapter = null;
    private RecyclerView notesList = null;
    private FloatingActionButton addNote = null;

    private RadioButton tnButton;
    private RadioButton lnButton;
    private RadioButton clButton;
    private RadioButton rButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        getSupportActionBar().setTitle(Html.fromHtml("<b>"+getSupportActionBar().getTitle()+"</b>"));

        notesList = (RecyclerView) findViewById(R.id.notes_list);
        notesList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        RecyclerView.LayoutManager notesLayoutManager = new LinearLayoutManager(this);
        notesList.setLayoutManager(notesLayoutManager);
        notesList.setItemAnimator(new DefaultItemAnimator());

        notesAdapter = new NoteAdapter(Backend.getNotes());
        notesList.setAdapter(notesAdapter);

        addNote = (FloatingActionButton) findViewById(R.id.add_note);

        setTouchListeners();
    }

    private void setTouchListeners(){

        notesList.addOnItemTouchListener(
                new RecyclerItemClickListener(HomeScreen.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Note note = notesAdapter.getItem(position);
                        Intent i = null;
                        if(note instanceof TextNote){
                            i = new Intent(HomeScreen.this, TextNoteView.class);
                        }
                        else if(note instanceof ListNote){
                            i = new Intent(HomeScreen.this, ListNoteView.class);
                        }
                        else if(note instanceof Checklist){
                            i = new Intent(HomeScreen.this, ChecklistView.class);
                        }
                        else if(note instanceof Reminder){
                            i = new Intent(HomeScreen.this, ReminderView.class);
                        }
                        Bundle extra = new Bundle();
                        extra.putInt("noteId", note.getId());
                        i.putExtra("bundle", extra);
                        startActivity(i);
                    }
                })
        );

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(HomeScreen.this);
                final View dialog = layoutInflater.inflate(R.layout.new_note_dialog, null);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeScreen.this,
                        R.style.AppCompatAlertDialogStyle);
                alertDialog.setTitle("New Note");

                tnButton = (RadioButton) dialog.findViewById(R.id.text_note);
                lnButton = (RadioButton) dialog.findViewById(R.id.list_note);
                clButton = (RadioButton) dialog.findViewById(R.id.checklist);
                rButton = (RadioButton) dialog.findViewById(R.id.reminder);
                tnButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked){
                            return;
                        }
                        changeRadioButtonChecks("text");
                    }
                });
                lnButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked){
                            return;
                        }
                        changeRadioButtonChecks("list");
                    }
                });
                clButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked){
                            return;
                        }
                        changeRadioButtonChecks("checklist");
                    }
                });
                rButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(!isChecked){
                            return;
                        }
                        changeRadioButtonChecks("reminder");
                    }
                });

                alertDialog.setView(dialog);

                final EditText input = (EditText) dialog.findViewById(R.id.input_dialog_text);
                input.setHint("Enter Title");
                input.setFocusableInTouchMode(true);
                input.requestFocus();

                alertDialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {

                        String noteTitle = input.getText().toString();
                        if(noteTitle.equals("")){
                            noteTitle = "Untitled Note";
                        }

                        Intent i = null;
                        int nextId = Backend.getNextID();
                        if(tnButton.isChecked()){
                            TextNote tn = new TextNote(nextId, noteTitle);
                            Backend.getNotes().add(tn);
                            i = new Intent(HomeScreen.this, TextNoteView.class);
                        }
                        else if(lnButton.isChecked()){
                            ListNote ln = new ListNote(nextId, noteTitle);
                            Backend.getNotes().add(ln);
                            i = new Intent(HomeScreen.this, ListNoteView.class);
                        }
                        else if(clButton.isChecked()){
                            Checklist cl = new Checklist(nextId, noteTitle);
                            Backend.getNotes().add(cl);
                            i = new Intent(HomeScreen.this, ChecklistView.class);
                        }
                        else if(rButton.isChecked()){
                            Reminder r = new Reminder(nextId, noteTitle);
                            Backend.getNotes().add(r);
                            i = new Intent(HomeScreen.this, ReminderView.class);
                        }
                        Bundle extra = new Bundle();
                        extra.putInt("noteId", nextId);
                        i.putExtra("bundle", extra);
                        Backend.writeData(getApplicationContext()); //backup data
                        startActivity(i);
                    }
                });
                alertDialog.setNegativeButton("CANCEL", null);

                AlertDialog alert = alertDialog.create();
                alert.show();

                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
    }

    private void changeRadioButtonChecks(String type){

        tnButton.setChecked(false);
        lnButton.setChecked(false);
        clButton.setChecked(false);
        rButton.setChecked(false);

        switch(type){
            case "text":
                tnButton.setChecked(true);
                break;
            case "list":
                lnButton.setChecked(true);
                break;
            case "checklist":
                clButton.setChecked(true);
                break;
            case "reminder":
                rButton.setChecked(true);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homescreen_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialog = null;

        switch (item.getItemId()) {
            case R.id.menu_sort_date:
                Collections.sort(notesAdapter.getList(), Note.BY_DATE);
                notesAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_sort_name:
                Collections.sort(notesAdapter.getList(), Note.BY_NAME);
                notesAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_filter_none:
                notesAdapter = new NoteAdapter(Backend.getNotes());
                notesList.setAdapter(notesAdapter);
                return true;
            case R.id.menu_filter_text:
                List<Note> textNotes = new ArrayList<Note>();
                for(Note note : Backend.getNotes()){
                    if(note instanceof TextNote){
                        textNotes.add(note);
                    }
                }
                notesAdapter = new NoteAdapter(textNotes);
                notesList.setAdapter(notesAdapter);
                return true;
            case R.id.menu_filter_list:
                List<Note> listNotes = new ArrayList<Note>();
                for(Note note : Backend.getNotes()){
                    if(note instanceof ListNote){
                        listNotes.add(note);
                    }
                }
                notesAdapter = new NoteAdapter(listNotes);
                notesList.setAdapter(notesAdapter);
                return true;
            case R.id.menu_filter_checklist:
                List<Note> checklists = new ArrayList<Note>();
                for(Note note : Backend.getNotes()){
                    if(note instanceof Checklist){
                        checklists.add(note);
                    }
                }
                notesAdapter = new NoteAdapter(checklists);
                notesList.setAdapter(notesAdapter);
                return true;
            case R.id.menu_filter_reminder:
                List<Note> reminders = new ArrayList<Note>();
                for(Note note : Backend.getNotes()){
                    if(note instanceof Reminder){
                        reminders.add(note);
                    }
                }
                notesAdapter = new NoteAdapter(reminders);
                notesList.setAdapter(notesAdapter);
                return true;
            case R.id.about:
                displayAboutDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayAboutDialog(){

        //inflate layout with customized alert dialog view
        LayoutInflater layoutInflater = LayoutInflater.from(HomeScreen.this);
        final View dialog = layoutInflater.inflate(R.layout.info_dialog, null);
        final AlertDialog.Builder infoDialogBuilder = new AlertDialog.Builder(HomeScreen.this,
                R.style.AppCompatAlertDialogStyle);

        //customize alert dialog and set its view
        infoDialogBuilder.setTitle("About");
        infoDialogBuilder.setIcon(R.drawable.ic_info_black_24dp);
        infoDialogBuilder.setView(dialog);

        //fetch textview and set its text
        final TextView message = (TextView) dialog.findViewById(R.id.infodialog_text);
        message.setText(R.string.about_message);

        //set up actions for dialog buttons
        infoDialogBuilder.setPositiveButton("RATE APP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {

                String appPackageName = getApplicationContext().getPackageName();
                Intent i = new Intent(Intent.ACTION_VIEW);
                try{
                    i.setData(Uri.parse("market://details?id=" + appPackageName));
                    startActivity(i);
                } catch(ActivityNotFoundException e){
                    try{
                        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                        startActivity(i);
                    } catch (ActivityNotFoundException e2){
                        Snackbar errorBar = Snackbar.make(findViewById(R.id.coordLayout),
                                "Could not launch the Google Play app.", Snackbar.LENGTH_SHORT);
                        errorBar.show();
                    }
                }
            }
        });
        infoDialogBuilder.setNegativeButton("DISMISS", null);

        //create and show the dialog
        AlertDialog infoDialog = infoDialogBuilder.create();
        infoDialog.show();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if(Backend.getNotes().isEmpty()){
            Backend.readData(this.getApplicationContext()); //read data from backup
        }
        Collections.sort(notesAdapter.getList(), Note.BY_DATE);
        notesAdapter.notifyDataSetChanged();
    }
}
