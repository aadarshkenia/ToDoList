package com.example.calenderquickstart;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by aadarsh-ubuntu on 8/3/15.
 */
public class MyAdapter extends ArrayAdapter {

    private final Context context;
    private final int resourceId;
    private final ArrayList<String> values;
    private final int event_type;

    public MyAdapter(Context context, int resourceId, ArrayList<String> values, int event_type) {
        super(context, resourceId, values);
        this.context = context;
        this.resourceId = resourceId;
        this.values = values;
        this.event_type=event_type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(resourceId, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.list_item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_image);
        textView.setText((String)values.get(position));

        switch (event_type){
            case MyUtility.event_local:{
                imageView.setImageResource(R.drawable.local_img);
                break;
            }
            case MyUtility.event_calendar:{
                imageView.setImageResource(R.drawable.calendar_img);
                break;
            }
            case MyUtility.event_evernote:{
                imageView.setImageResource(R.drawable.evernote_img);
                break;
            }
        }

        return rowView;
    }


}
