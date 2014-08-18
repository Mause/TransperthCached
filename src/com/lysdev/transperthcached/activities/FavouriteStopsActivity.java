package com.lysdev.transperthcached.activities;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import android.text.InputFilter;
import android.text.InputType;

import android.view.Gravity;
import android.view.View;

import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lysdev.transperthcached.R;



class FavouriteStop {
    private String stop_number;
    private Integer sid = null;

    public FavouriteStop(String stop_number) {
        this.stop_number = stop_number;
    }

    public FavouriteStop(Integer sid, String stop_number) {
        this(stop_number);
        this.sid = sid;
    }

    public String getStopNumber() {
        return this.stop_number;
    }

    public String toString() {
        return this.stop_number;
    }
}



class FavouriteStopDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "FavouriteStopDatabase";
    private SQLiteDatabase db;

    public FavouriteStopDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        db = getWritableDatabase();
    }
    public void onUpgrade(SQLiteDatabase db, int a, int b) {}
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS favourite_stops (" +
            "    sid INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    stop_number TEXT" +
            ");"
        );
    }

    public void addFavouriteStop(FavouriteStop toAdd) {
        ContentValues values = new ContentValues();
        values.put("stop_number", toAdd.getStopNumber());

        db.insert("favourite_stops", null, values);
    }

    public boolean stopExists(FavouriteStop stop) {
        return stopExists(stop.getStopNumber());
    }

    public boolean stopExists(String stop_number) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
            "favourite_stops",
            new String[] { "count(*)" },
            "stop_number=?",
            new String[] { stop_number },
            null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return false;
        }

        return cursor.getInt(0) > 0;
    }

    public void deleteStop(FavouriteStop stop) {
        deleteStop(stop.getStopNumber());
    }

    public void deleteStop(String stop_number) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(
            "favourite_stops",
            "stop_number=?",
            new String[] { stop_number }
        );
    }

    public ArrayList<FavouriteStop> getFavouriteStops() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
            "favourite_stops",    // table
            new String[] { "*" }, // selected fields
            null, // where clause
            null, // where fields
            null, // having
            null, // orderBy
            null  // limit
        );

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }

        ArrayList<FavouriteStop> stops = new ArrayList<FavouriteStop>();

        while (!cursor.isAfterLast()) {
            stops.add(
                new FavouriteStop(
                    cursor.getInt(0),
                    cursor.getString(1)
                )
            );

            cursor.moveToNext();
        }

        return stops;
    }
}


interface OnDeleteListener<T> {
    void onDelete(T t);
}


class ArrayAdapterWrapper<T> extends ArrayAdapter<T> {
    public ArrayAdapterWrapper(Context context, int rida, int ridb, List<T> list) {
        super(context, rida, ridb, list);
    }

    public ArrayAdapterWrapper(Context context, int rida, List<T> list) {
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


public class FavouriteStopsActivity extends FragmentActivity
                                 implements DialogInterface.OnClickListener,
                                            AdapterView.OnItemClickListener,
                                            OnDeleteListener<FavouriteStop> {

    private EditText stopInput;
    private ListView stops;
    private ArrayAdapterWrapper<FavouriteStop> stops_adapter;
    private Cursor stops_cursor;
    private FavouriteStopDatabaseHelper db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_stops);

        db = new FavouriteStopDatabaseHelper(this);

        stops = (ListView) findViewById(R.id.favourite_stops);
        stops_adapter = new ArrayAdapterWrapper<FavouriteStop>(
            this,
            R.layout.favourite_stop_item,
            R.id.text1,
            db.getFavouriteStops()
        );
        stops.setAdapter(stops_adapter);
        stops.setOnItemClickListener(this);
        stops_adapter.setOnDeleteListener(this);
    }

    public void onDelete(FavouriteStop fav) {
        Log.d("TransperthCached", "Delete: " + fav.toString());
        db.deleteStop(fav);
        updateStops();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FavouriteStop selected_stop = (FavouriteStop)parent.getItemAtPosition(position);

        Log.d("TransperthCached", "Selected stop: " + selected_stop.toString());

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("stop_num", selected_stop.getStopNumber());
        startActivity(intent);
    }

    public void deleteButtonClicked(View view) {}
    public void    addButtonClicked(View view) {
        stopInput = new EditText(this);
        stopInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
        stopInput.setHint(R.string.stop_number_hint);
        stopInput.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        stopInput.setGravity(
            Gravity.CENTER_VERTICAL |
            Gravity.CENTER
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add favourite stop")
            .setCancelable(true)
            .setView(stopInput)
            .setPositiveButton("Add", this)
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        builder.create().show();
    }

    public void onClick(DialogInterface dialog, int id) {
        String stop_number = stopInput.getText().toString();

        if (stop_number.length() == 5) {
            Log.d("TransperthCached", "Stop number: " + stop_number);

            db.addFavouriteStop(new FavouriteStop(stop_number));

            stops_adapter.clear();
            for (FavouriteStop stop : db.getFavouriteStops()) {
                stops_adapter.add(stop);
            }
            stops_adapter.notifyDataSetChanged();

            dialog.dismiss();
        } else {
            Log.d("TransperthCached", "Not long enough");

            Toast.makeText(
                this,
                "Stop numbers must be five digits long",
                Toast.LENGTH_LONG
            ).show();
        }
    }

    // protected void onSaveInstanceState(Bundle outState) {
    //     outState.putString("stop_number", stop_num_widget.getText().toString());
    // }
}
