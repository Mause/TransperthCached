package com.lysdev.transperthcached.ui;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;

import com.lysdev.transperthcached.R;
import com.lysdev.transperthcached.models.FavouriteStop;


public class FavouriteStopArrayAdapter extends ArrayAdapter<FavouriteStop> {
    public interface OnDeleteListener {
        void onDelete(FavouriteStop t);
    }

    public FavouriteStopArrayAdapter(Context context, int rida, int ridb, List<FavouriteStop> list) {
        super(context, rida, ridb, list);
    }

    public FavouriteStopArrayAdapter(Context context, int rida, List<FavouriteStop> list) {
        super(context, rida, list);
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    private OnDeleteListener deleteListener = null;
    public void setOnDeleteListener(OnDeleteListener listener) {
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
