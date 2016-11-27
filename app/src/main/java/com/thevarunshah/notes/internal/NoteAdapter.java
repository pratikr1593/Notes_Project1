package com.thevarunshah.notes.internal;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thevarunshah.notes.data.Checklist;
import com.thevarunshah.notes.data.ListNote;
import com.thevarunshah.notes.data.Note;
import com.thevarunshah.notes.data.Reminder;
import com.thevarunshah.notes.data.TextNote;
import com.thevarunshah.notes.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{

    private List<Note> items;

    public NoteAdapter(List<Note> items) {
        this.items = items;
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_textview, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.txtViewTitle.setText(items.get(position).getName());
        Object note = items.get(position);
        if(note instanceof TextNote){
            viewHolder.txtViewType.setText("Text");
            viewHolder.txtViewModified.setText(((TextNote) note).getFormattedDate());
        }
        else if(note instanceof ListNote){
            viewHolder.txtViewType.setText("List");
            viewHolder.txtViewModified.setText(((ListNote) note).getFormattedDate());
        }
        else if(note instanceof Checklist){
            viewHolder.txtViewType.setText("Checklist");
            viewHolder.txtViewModified.setText(((Checklist) note).getFormattedDate());
        }
        else if(note instanceof Reminder){
            viewHolder.txtViewType.setText("Reminder");
            viewHolder.txtViewModified.setText(((Reminder) note).getFormattedDate());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public TextView txtViewType;
        public TextView txtViewModified;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.notes_list_textview);
            txtViewType = (TextView) itemLayoutView.findViewById(R.id.notes_list_type_textview);
            txtViewModified = (TextView) itemLayoutView.findViewById(R.id.notes_list_modified_textview);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Note getItem(int position){
        return items.get(position);
    }

    public List<Note> getList(){
        return this.items;
    }

    public void add(Note item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void remove(Note item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }
}
