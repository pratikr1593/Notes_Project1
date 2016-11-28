package com.thevarunshah.notes.internal;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thevarunshah.notes.R;
import com.thevarunshah.notes.data.ListNote;

import java.util.ArrayList;

public class ListNoteAdapter extends ArrayAdapter<String> {

    private final ArrayList<String> list; //the list the adapter manages
    private final Context context; //context attached to adapter
    private final ListNote ln;

    public static int currActive = -1;

    /**
     * the list adapter
     * @param context the application context
     * @param ln the listnote
     */
    public ListNoteAdapter(Context context, ListNote ln) {

        super(context, R.layout.listnote_row, ln.getList());
        this.context = context;
        this.list = ln.getList();
        this.ln = ln;
    }

    /**
     * a view holder for each bullet in the row
     */
    private class ViewHolder {
        EditText bulletEdit;
        TextView bulletText;
        ImageView delete;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            //inflate view and link each component to the holder
            LayoutInflater vi = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.listnote_row, null);
            holder.bulletEdit = (EditText) convertView.findViewById(R.id.listnote_bullet);
            holder.bulletText = (TextView) convertView.findViewById(R.id.listnote_bullet_view);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete_listitem);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(position+1 == list.size()){
            holder.delete.setVisibility(View.INVISIBLE);
        }
        else{
            holder.delete.setVisibility(View.VISIBLE);
        }

        final ViewHolder finalHolder = holder;
        holder.bulletText.setText(list.get(position));
        holder.bulletText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = finalHolder.bulletText.getText().toString();
                finalHolder.bulletText.setVisibility(View.GONE);
                finalHolder.bulletEdit.setVisibility(View.VISIBLE);
                if(text.equals("")){
                    finalHolder.bulletEdit.setHint(R.string.list_hint);
                }
                else{
                    finalHolder.bulletEdit.setText(text);
                }
                finalHolder.bulletEdit.setFocusableInTouchMode(true);
                finalHolder.bulletEdit.requestFocus();
                finalHolder.bulletEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus){
                            final EditText et = (EditText) v;
                            if(!list.get(position).equals(et.getText().toString())){
                                list.set(position, et.getText().toString());
                                ln.updateDate();
                                String text = finalHolder.bulletEdit.getText().toString();
                                finalHolder.bulletEdit.setVisibility(View.GONE);
                                finalHolder.bulletText.setVisibility(View.VISIBLE);
                                finalHolder.bulletText.setText(text);
                            }
                        }
                    }
                });
                currActive = position;
                if(position+1 == list.size()){
                    list.add("");
                    notifyDataSetChanged();
                    ln.updateDate();
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String bullet = getItem(position);

                list.remove(bullet);
                notifyDataSetChanged();
                Backend.writeData(getContext());

                Snackbar infoBar = Snackbar.make(view, "Item deleted.", Snackbar.LENGTH_LONG);
                infoBar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //undo deleting
                        list.add(position, bullet);
                        notifyDataSetChanged();
                        ln.updateDate();
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
