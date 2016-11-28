package com.thevarunshah.notes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.thevarunshah.notes.data.Reminder;
import com.thevarunshah.notes.internal.Backend;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReminderView extends AppCompatActivity {

    final private String TAG = "ReminderView";
    public static Reminder r = null;
    private TextView dateTV;
    private TextView timeTV;
    private EditText reminderNotes;
    private TextView notificationView;
    private NumberPicker notifTimePicker;
    private NumberPicker notifUnitPicker;

    final String[] minutes = new String[]{"0", "1", "5", "10", "15", "30", "45", "60"};
    final String[] hours = new String[]{"0", "1", "2", "3", "6", "12", "24"};
    final String[] days = new String[]{"0", "1", "2", "3", "4", "5", "6", "7"};
    final String[] units = new String[]{"min(s)", "hour(s)", "day(s)"};

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_view);

        int noteId = getIntent().getBundleExtra("bundle").getInt("noteId");
        r = (Reminder) Backend.getNote(noteId);
        getSupportActionBar().setTitle(Html.fromHtml("<b>" + r.getName() + "</b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateTV = (TextView) findViewById(R.id.reminder_date);
        dateTV.setText((new SimpleDateFormat("MM/dd/yy")).format(r.getDue()));
        timeTV = (TextView) findViewById(R.id.reminder_time);
        timeTV.setText((new SimpleDateFormat("h:mm a")).format(r.getDue()));
        reminderNotes = (EditText) findViewById(R.id.reminder_notes);
        reminderNotes.setText(r.getNotes());

        notificationView = (TextView) findViewById(R.id.reminder_notification_display);
        String notificationDisplay = "";
        if(!r.isNoNotification()) {
            switch (r.getNotificationUnit()) {
                case 0:
                    notificationDisplay = minutes[r.getNotificationTime()] + " " + units[0] + " before";
                    break;
                case 1:
                    notificationDisplay = hours[r.getNotificationTime()] + " " + units[1] + " before";
                    break;
                case 2:
                    notificationDisplay = days[r.getNotificationTime()] + " " + units[2] + " before";
                    break;
                default:
                    notificationDisplay = "None";
                    break;
            }
        }
        else{
            notificationDisplay = "None";
        }
        notificationView.setText(notificationDisplay);

        notificationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //inflate layout with customized alert dialog view
                LayoutInflater layoutInflater = LayoutInflater.from(ReminderView.this);
                View dialog = layoutInflater.inflate(R.layout.notification_dialog, null);
                final AlertDialog.Builder editTitleDialogBuilder = new AlertDialog.Builder(ReminderView.this,
                        R.style.AppCompatAlertDialogStyle);

                //customize alert dialog and set its view
                editTitleDialogBuilder.setTitle("Set a Notification");
                editTitleDialogBuilder.setView(dialog);

                //fetch and set up pickers
                notifUnitPicker = (NumberPicker) dialog.findViewById(R.id.notification_unit_picker);
                notifUnitPicker.setMinValue(0);
                notifUnitPicker.setDisplayedValues(units);
                notifUnitPicker.setMaxValue(units.length-1);
                notifUnitPicker.setValue(r.getNotificationUnit());
                notifTimePicker = (NumberPicker) dialog.findViewById(R.id.notification_time_picker);
                notifTimePicker.setMinValue(0);
                if(r.getNotificationUnit() == 0){
                    notifTimePicker.setMaxValue(minutes.length-1);
                    notifTimePicker.setDisplayedValues(minutes);
                }
                else if(r.getNotificationUnit() == 1){
                    notifTimePicker.setMaxValue(hours.length-1);
                    notifTimePicker.setDisplayedValues(hours);
                }
                else{
                    notifTimePicker.setMaxValue(days.length-1);
                    notifTimePicker.setDisplayedValues(days);
                }
                notifTimePicker.setValue(r.getNotificationTime());

                notifUnitPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int j) {

                        switch(units[j]){
                            case "min(s)":
                                if(notifTimePicker.getMaxValue() < minutes.length-1) {
                                    notifTimePicker.setDisplayedValues(minutes);
                                    notifTimePicker.setMaxValue(minutes.length - 1);
                                }
                                else{
                                    notifTimePicker.setMaxValue(minutes.length - 1);
                                    notifTimePicker.setDisplayedValues(minutes);
                                }
                                break;
                            case "hour(s)":
                                if(notifTimePicker.getMaxValue() < hours.length-1) {
                                    notifTimePicker.setDisplayedValues(hours);
                                    notifTimePicker.setMaxValue(hours.length - 1);
                                }
                                else{
                                    notifTimePicker.setMaxValue(hours.length - 1);
                                    notifTimePicker.setDisplayedValues(hours);
                                }
                                break;
                            case "day(s)":
                                if(notifTimePicker.getMaxValue() < days.length-1) {
                                    notifTimePicker.setDisplayedValues(days);
                                    notifTimePicker.setMaxValue(days.length - 1);
                                }
                                else{
                                    notifTimePicker.setMaxValue(days.length - 1);
                                    notifTimePicker.setDisplayedValues(days);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });

                final CheckBox notifNone = (CheckBox) dialog.findViewById(R.id.notification_none_check);
                notifNone.setChecked(r.isNoNotification());

                //set up actions for dialog buttons
                editTitleDialogBuilder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        r.setNoNotification(notifNone.isChecked());
                        String notificationDisplay = "None";
                        if(!r.isNoNotification()) {
                            r.setNotificationTime(notifTimePicker.getValue());
                            r.setNotificationUnit(notifUnitPicker.getValue());
                            switch (r.getNotificationUnit()) {
                                case 0:
                                    notificationDisplay = minutes[r.getNotificationTime()] + " " + units[0] + " before";
                                    break;
                                case 1:
                                    notificationDisplay = hours[r.getNotificationTime()] + " " + units[1] + " before";
                                    break;
                                case 2:
                                    notificationDisplay = days[r.getNotificationTime()] + " " + units[2] + " before";
                                    break;
                                default:
                                    break;
                            }
                        }
                        notificationView.setText(notificationDisplay);
                        r.updateDate();
                    }
                });
                editTitleDialogBuilder.setNegativeButton("CANCEL", null);

                //create and show the dialog
                AlertDialog editTitleDialog = editTitleDialogBuilder.create();
                editTitleDialog.show();
            }
        });

        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(ReminderView.this);
                View dialog = null;
                //inflate layout with customized alert dialog view
                dialog = layoutInflater.inflate(R.layout.date_dialog, null);
                final AlertDialog.Builder dateDialogBuilder = new AlertDialog.Builder(ReminderView.this,
                        R.style.AppCompatAlertDialogStyle);
                dateDialogBuilder.setView(dialog);

                //fetch and set up date
                final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.date_picker);
                datePicker.setMinDate(Calendar.getInstance().getTimeInMillis()-1000);
                Calendar c = Calendar.getInstance();
                c.setTime(r.getDue());
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                //datePicker.setCalendarViewShown(false);

                //set up actions for dialog buttons
                dateDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(r.getDue());
                        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        calendar.set(Calendar.SECOND, 0);

                        r.setDue(calendar.getTime());
                        r.updateDate();
                        dateTV.setText((new SimpleDateFormat("MM/dd/yy")).format(r.getDue()));
                    }
                });
                dateDialogBuilder.setNegativeButton("CANCEL", null);

                //create and show the dialog
                AlertDialog dateDialog = dateDialogBuilder.create();
                dateDialog.show();
            }
        });
        timeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(ReminderView.this);
                View dialog = null;
                //inflate layout with customized alert dialog view
                dialog = layoutInflater.inflate(R.layout.time_dialog, null);
                final AlertDialog.Builder timeDialogBuilder = new AlertDialog.Builder(ReminderView.this,
                        R.style.AppCompatAlertDialogStyle);
                timeDialogBuilder.setView(dialog);

                //fetch and set up time
                final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.time_picker);
                Calendar c = Calendar.getInstance();
                c.setTime(r.getDue());
                timePicker.setCurrentHour(c.get(Calendar.HOUR));
                timePicker.setCurrentMinute(c.get(Calendar.MINUTE));

                //set up actions for dialog buttons
                timeDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(r.getDue());
                        calendar.set(Calendar.HOUR, timePicker.getCurrentHour());
                        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                        calendar.set(Calendar.SECOND, 0);

                        r.setDue(calendar.getTime());
                        r.updateDate();
                        dateTV.setText((new SimpleDateFormat("MM/dd/yy")).format(r.getDue()));
                        timeTV.setText((new SimpleDateFormat("hh:mm a")).format(r.getDue()));
                    }
                });
                timeDialogBuilder.setNegativeButton("CANCEL", null);

                //create and show the dialog
                AlertDialog timeDialog = timeDialogBuilder.create();
                timeDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reminder_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialog = null;

        switch(item.getItemId()){
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
                input.setText(r.getName());
                input.setFocusableInTouchMode(true);
                input.requestFocus();

                //set up actions for dialog buttons
                editTitleDialogBuilder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        r.setName(input.getText().toString());
                        r.updateDate();
                        getSupportActionBar().setTitle(Html.fromHtml("<b>" + r.getName() + "</b>"));
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
            case R.id.delete_reminder:
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
                message.setText("Are you sure you want to delete this reminder?");

                deleteNoteDialogBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        Backend.getNotes().remove(r);
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

    @Override
    protected void onPause() {

        super.onPause();
        if(!r.getNotes().equals(reminderNotes.getText().toString())){
            r.setNotes(reminderNotes.getText().toString());
            r.updateDate();
        }
        Backend.writeData(this.getApplicationContext()); //backup data
    }

    @Override
    protected void onResume() {

        super.onResume();
        if(r == null){
            this.finish();
        }
    }
}
