package com.janesbrain.cartracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.janesbrain.cartracker.model.absLocation;

import java.util.List;

//http:www.vogella.com/tutorials/AndroidListView/article.html#arrayAdapter
public class ListViewAdaptor extends ArrayAdapter <absLocation>{

    private Context context;
    private List<absLocation> values;

    public ListViewAdaptor(Context context, List<absLocation> values){
        super(context, -1, values);

        this.context = context;
        this.values = values;
    }

    //@NonNull
    //@Override
    //public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    //https://stackoverflow.com/questions/8166497/custom-adapter-for-list-view
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if(rowView != null){
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.list_view, null);
        }

        TextView bigTV = (TextView)rowView.findViewById(R.id.bigTextTV);
        TextView medTV = (TextView)rowView.findViewById(R.id.medTextTV);

        absLocation location = values.get(position);

        if(location != null){
            Log.d("ADAPTOR", "Location");
            bigTV.setText(""); //TODO get data
            medTV.setText(""); //TODO get data
        }
        return rowView;
    }
}
