package com.thevarunshah.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.thevarunshah.notes.data.TextNote;
import com.thevarunshah.notes.internal.Backend;

public class TextNoteView extends AppCompatActivity {

    final private String TAG = "TextNoteView";
    private Menu menu = null;
    private EditText et = null;
    private TextView tv = null;
    private TextNote tn = null;
    private boolean edit = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.textnote_view);

        int noteId = getIntent().getBundleExtra("bundle").getInt("noteId");
        tn = (TextNote) Backend.getNote(noteId);
        getSupportActionBar().setTitle(Html.fromHtml("<b>"+tn.getName()+"</b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et = (EditText) findViewById(R.id.textnote_edittext);
        tv = (TextView) findViewById(R.id.textnote_textview);
        if(tn.getNotes().equals("")){
            ((ViewGroup)tv.getParent()).removeView(tv);
            edit = false;
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        else{
            ((ViewGroup)et.getParent()).removeView(et);
            tv.setText(tn.getNotes());
            tv.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.textnote_menu, menu);
        if(edit){
            menu.findItem(R.id.save_note).setVisible(false);
        }
        else{
            menu.findItem(R.id.edit_note).setVisible(false);
        }
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialog = null;

        switch(item.getItemId()){
            case R.id.edit_note:
                ((ViewGroup)tv.getParent()).addView(et);
                et.setText(tv.getText());
                ((ViewGroup)tv.getParent()).removeView(tv);
                menu.findItem(R.id.edit_note).setVisible(false);
                menu.findItem(R.id.save_note).setVisible(true);
                if(imm != null){
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                }
                et.setSelection(et.getText().length());
                et.requestFocus();
                return true;
            case R.id.save_note:
                ((ViewGroup)et.getParent()).addView(tv);
                tv.setText(et.getText().toString());
                ((ViewGroup)et.getParent()).removeView(et);
                menu.findItem(R.id.save_note).setVisible(false);
                menu.findItem(R.id.edit_note).setVisible(true);
                if(imm != null){
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
                tv.setMovementMethod(new ScrollingMovementMethod());
                tn.setNotes(tv.getText().toString());
                tn.updateDate();
                Backend.writeData(this.getApplicationContext()); //backup data
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
                input.setText(tn.getName());
                input.setFocusableInTouchMode(true);
                input.requestFocus();

                //set up actions for dialog buttons
                editTitleDialogBuilder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        tn.setName(input.getText().toString());
                        tn.updateDate();
                        Backend.writeData(getApplicationContext()); //backup data
                        getSupportActionBar().setTitle(Html.fromHtml("<b>" + tn.getName() + "</b>"));
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
                final TextView message = (TextView) dialog.findViewById(R.id.infodialog_text);
                message.setText("Are you sure you want to delete this note?");

                deleteNoteDialogBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        Backend.getNotes().remove(tn);
                        finish();
                    }
                });
                deleteNoteDialogBuilder.setNegativeButton("CANCEL", null);

                //create and show the dialog
                AlertDialog deleteNoteDialog = deleteNoteDialogBuilder.create();
                deleteNoteDialog.show();
                return true;
            case android.R.id.home:
                if(menu.findItem(R.id.save_note).isVisible()){
                    tn.setNotes(et.getText().toString());
                    tn.updateDate();
                    Backend.writeData(this.getApplicationContext()); //backup data
                }
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();
        if(tn == null){
            this.finish();
        }
    }
}
