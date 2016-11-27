package com.thevarunshah.notes.internal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thevarunshah.notes.R;
import com.thevarunshah.notes.data.Checklist;
import com.thevarunshah.notes.data.ChecklistItem;

import java.util.ArrayList;

public class ChecklistAdapter extends ArrayAdapter<ChecklistItem> {

    private final ArrayList<ChecklistItem> list; //the list the adapter manages
    private final Context context; //context attached to adapter
    private final Checklist cl;

    public static int currActive = -1;

    /**
     * the checklist adapter
     * @param context the application context
     * @param cl the checklist
     */
    public ChecklistAdapter(Context context, Checklist cl) {

        super(context, R.layout.checklist_row, cl.getList());
        this.context = context;
        this.cl = cl;
        this.list = cl.getList();
    }

    /**
     * a view holder for each item in the row
     */
    private class ViewHolder {

        CheckBox done;
        EditText itemEdit;
        TextView itemText;
        ImageView delete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            //inflate view and link each component to the holder
            LayoutInflater vi = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.checklist_row, null);
            holder.done = (CheckBox) convertView.findViewById(R.id.checklist_check);
            holder.itemEdit = (EditText) convertView.findViewById(R.id.checklist_text);
            holder.itemText = (TextView) convertView.findViewById(R.id.checklist_text_view);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete_checklistitem);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(position+1 == list.size()){
            holder.delete.setVisibility(View.INVISIBLE);
            holder.done.setClickable(false);
        }
        else{
            holder.delete.setVisibility(View.VISIBLE);
            holder.done.setClickable(true);
        }

        final ViewHolder finalHolder = holder;
        //attach a check listener to the checkbox
        holder.done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //get item and set as done/undone
                ChecklistItem item = getItem(position);
                item.setDone(isChecked);

                //apply/remove strikethrough effect and disable/enable edit functionality
                if(isChecked){
                    finalHolder.itemText.setPaintFlags(finalHolder.itemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    finalHolder.itemText.setFocusable(false);
                }
                else{
                    finalHolder.itemText.setPaintFlags(finalHolder.itemText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    finalHolder.itemText.setFocusable(true);
                }

                cl.updateDate();
                Backend.writeData(getContext());
            }
        });

        //get item and link references to holder
        ChecklistItem item = list.get(position);
        holder.itemText.setText(item.getItemText());
        holder.done.setChecked(item.isDone());
        //holder.done.setTag(item);

        holder.itemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = finalHolder.itemText.getText().toString();
                finalHolder.itemText.setVisibility(View.GONE);
                finalHolder.itemEdit.setVisibility(View.VISIBLE);
                if(text.equals("")){
                    finalHolder.itemEdit.setHint(R.string.list_hint);
                }
                else{
                    finalHolder.itemEdit.setText(text);
                }
                finalHolder.itemEdit.setFocusableInTouchMode(true);
                finalHolder.itemEdit.requestFocus();
                finalHolder.itemEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if(!hasFocus){
                            final EditText et = (EditText) view;
                            if(!list.get(position).equals(et.getText().toString())){
                                list.get(position).setItemText(et.getText().toString());
                                cl.updateDate();
                                String text = finalHolder.itemEdit.getText().toString();
                                finalHolder.itemEdit.setVisibility(View.GONE);
                                finalHolder.itemText.setVisibility(View.VISIBLE);
                                finalHolder.itemText.setText(text);
                            }
                        }
                    }
                });
                currActive = position;
                if(position+1 == list.size()){
                    list.add(new ChecklistItem(""));
                    notifyDataSetChanged();
                    cl.updateDate();
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ChecklistItem item = getItem(position);

                list.remove(item);
                notifyDataSetChanged();
                Backend.writeData(getContext());

                Snackbar infoBar = Snackbar.make(view, "Item deleted.", Snackbar.LENGTH_LONG);
                infoBar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //undo deleting
                        list.add(position, item);
                        notifyDataSetChanged();
                        cl.updateDate();
                        Backend.writeData(getContext());
                    }
                });
                infoBar.setActionTextColor(Color.WHITE);
                infoBar.show();
            }
        });

        return convertView;
    }
}
