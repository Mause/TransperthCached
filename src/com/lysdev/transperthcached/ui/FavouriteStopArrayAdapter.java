package com.lysdev.transperthcached.ui;

import java.util.List;
import android.widget.TextView;

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

    List<FavouriteStop> items;

    public FavouriteStopArrayAdapter(Context context, int rida, int ridb, List<FavouriteStop> list) {
        super(context, rida, ridb, list);
        this.items = list;
    }

    public FavouriteStopArrayAdapter(Context context, int rida, List<FavouriteStop> list) {
        super(context, rida, list);
        this.items = list;
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

        FavouriteStop item = items.get(position);

        if (item != null) {
            TextView stop_number = (TextView) view.findViewById(R.id.stop_number);
            if (stop_number != null) {
                stop_number.setText(
                    String.valueOf(item.getStopNumber())
                );
            }

            TextView description = (TextView) view.findViewById(R.id.description);
            if (description != null)
                description.setText(item.getDescription());
        }

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
