package com.thevarunshah.notes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.thevarunshah.notes.data.Checklist;
import com.thevarunshah.notes.internal.Backend;
import com.thevarunshah.notes.internal.ChecklistAdapter;

import java.util.ArrayList;

public class ChecklistView extends AppCompatActivity {

    final private String TAG = "ChecklistView";
    public static Checklist cl = null;

    private ListView listView = null; //main view of items
    private ChecklistAdapter listAdapter = null; //adapter for items display

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_view);

        int noteId = getIntent().getBundleExtra("bundle").getInt("noteId");
        cl = (Checklist) Backend.getNote(noteId);
        getSupportActionBar().setTitle(Html.fromHtml("<b>" + cl.getName() + "</b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //obtain list view and create new bucket list custom adapter
        listView = (ListView) findViewById(R.id.checklist_listview);
        listAdapter = new ChecklistAdapter(this, cl);
        listView.setAdapter(listAdapter); //attach adapter to list view

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checklist_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialog = null;

        switch(item.getItemId()){
            case R.id.clear_checked:
                //inflate layout with customized alert dialog view
                dialog = layoutInflater.inflate(R.layout.info_dialog, null);
                final AlertDialog.Builder clearCheckedDialogBuilder = new AlertDialog.Builder(this,
                        R.style.AppCompatAlertDialogStyle);
                clearCheckedDialogBuilder.setView(dialog);

                //fetch textview and set its text
                final TextView message = (TextView) dialog.findViewById(R.id.infodialog_text);
                message.setText("This will delete all checked items from the checklist. Are you sure?");

                clearCheckedDialogBuilder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        deleteChecked();
                    }
                });
                clearCheckedDialogBuilder.setNegativeButton("CANCEL", null);

                //create and show the dialog
                AlertDialog clearCheckedDialog = clearCheckedDialogBuilder.create();
                clearCheckedDialog.show();
                return true;
            case R.id.edit_title:
                //inflate layout with customized alert dialog view
                dialog = layoutInflater.inflate(R.layout.input_dialog, null);
                final AlertDialog.Builder editTitleDialogBuilder = new AlertDialog.Builder(this,
                        R.style.AppCompatAlertDialogStyle);

                //customize alert dialog and set its view
                editTitleDialogBuilder.setTitle("Edit Title");
                editTitleDialogBuilder.setIcon(R.drawable.ic_edit_black_24dp);
                editTitleDialogBuilder.setView(dialog);

                //fetch and set up edittext
                final EditText input = (EditText) dialog.findViewById(R.id.input_dialog_text);
                input.setText(cl.getName());
                input.setFocusableInTouchMode(true);
                input.requestFocus();

                //set up actions for dialog buttons
                editTitleDialogBuilder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        cl.setName(input.getText().toString());
                        cl.updateDate();
                        getSupportActionBar().setTitle(Html.fromHtml("<b>" + cl.getName() + "</b>"));
                    }
                });
                editTitleDialogBuilder.setNegativeButton("CANCEL", null);

                //create and show the dialog
                AlertDialog editTitleDialog = editTitleDialogBuilder.create();
                editTitleDialog.show();

                //show keyboard
                editTitleDialog.getWindow()
                        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return true;
            case R.id.delete_note:
                //inflate layout with customized alert dialog view
                dialog = layoutInflater.inflate(R.layout.info_dialog, null);
                final AlertDialog.Builder deleteNoteDialogBuilder = new AlertDialog.Builder(this,
                        R.style.AppCompatAlertDialogStyle);

                //customize alert dialog and set its view
                deleteNoteDialogBuilder.setTitle("Confirm Delete");
                deleteNoteDialogBuilder.setIcon(R.drawable.ic_delete_black_24dp);
                deleteNoteDialogBuilder.setView(dialog);

                //fetch textview and set its text
                final TextView message2 = (TextView) dialog.findViewById(R.id.infodialog_text);
                message2.setText("Are you sure you want to delete this checklist?");

                deleteNoteDialogBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        Backend.getNotes().remove(cl);
                        finish();
                    }
                });
                deleteNoteDialogBuilder.setNegativeButton("CANCEL", null);

                //create and show the dialog
                AlertDialog deleteNoteDialog = deleteNoteDialogBuilder.create();
                deleteNoteDialog.show();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean deleteEmpty(){

        boolean changed = false;
        View v = listView.getChildAt(ChecklistAdapter.currActive);
        if(v != null){
            EditText et = (EditText) v.findViewById(R.id.checklist_text);
            if(et.getVisibility() == View.VISIBLE) {
                cl.getList().get(ChecklistAdapter.currActive).setItemText(et.getText().toString());
                changed = true;
            }
        }

        ArrayList<Integer> removeIndices = new ArrayList<Integer>();
        for(int i = 0; i < cl.getList().size()-1; i++){
            if(cl.getList().get(i).getItemText().equals("")){
                removeIndices.add(i);
            }
        }

        for(int i = 0; i < removeIndices.size(); i++){
            cl.getList().remove(removeIndices.get(i)-i);
        }

        if(removeIndices.size() > 0){
            changed = true;
        }

        return changed;
    }

    public void deleteChecked(){

        ArrayList<Integer> removeIndices = new ArrayList<Integer>();
        for(int i = 0; i < cl.getList().size()-1; i++){
            if(cl.getList().get(i).isDone()){
                removeIndices.add(i);
            }
        }

        for(int i = 0; i < removeIndices.size(); i++){
            cl.getList().remove(removeIndices.get(i)-i);
        }

        if(removeIndices.size() > 0){
            cl.updateDate();
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        if(deleteEmpty()){
            cl.updateDate();
            Backend.writeData(this.getApplicationContext()); //backup data
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        if(cl == null){
            this.finish();
        }
    }
}
