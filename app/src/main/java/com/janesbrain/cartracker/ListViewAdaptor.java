package com.janesbrain.cartracker;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.janesbrain.cartracker.model.Location;

import java.util.List;

//http:www.vogella.com/tutorials/AndroidListView/article.html#arrayAdapter
public class ListViewAdaptor extends ArrayAdapter <Location>{

    private Context context;
    private List<Location> values;

    public ListViewAdaptor(Context context, List<Location> values){
        super(context, -1, values);

        this.context = context;
        this.values = values;
    }


}
