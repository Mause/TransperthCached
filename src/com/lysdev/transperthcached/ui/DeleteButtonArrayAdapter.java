package com.lysdev.transperthcached.ui;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;


public class DeleteButtonArrayAdapter<T> extends ArrayAdapter<T> {
    public interface OnDeleteListener<T> {
        void onDelete(T t);
    }

    public DeleteButtonArrayAdapter(Context context, int rida, int ridb, List<T> list) {
        super(context, rida, ridb, list);
    }

    public DeleteButtonArrayAdapter(Context context, int rida, List<T> list) {
        super(context, rida, list);
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    private OnDeleteListener<T> deleteListener = null;
    public void setOnDeleteListener(OnDeleteListener<T> listener) {
        this.deleteListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (deleteListener == null)
                    throw new IllegalArgumentException("you must set the delete listener");

                deleteListener.onDelete(getItem(position));
            }
        });

        return view;
    }
}
