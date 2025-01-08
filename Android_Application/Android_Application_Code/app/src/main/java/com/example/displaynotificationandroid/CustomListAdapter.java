package com.example.displaynotificationandroid;

import android.app.Activity;
import android.content.ClipData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

public class CustomListAdapter extends ArrayAdapter<String> {

    private static final String TAG = "MyTag";
    //to reference the Activity
    private final Activity context;

    //to store the animal images
//    private final String[] imageIDarray;
    private final String[] imageIDarray;

    //to store the list of countries
//    private final String[] nameArray;
    private final String[] nameArray;

    //to store the list of countries
//    private final String[] infoArray;
    private final String[] dateArray;

//    public CustomListAdapter(Activity context, String[] nameArrayParam, String[] infoArrayParam, String[] imageIDArrayParam){
//
//        super(context, R.layout.listview_row , nameArrayParam);
//
//        this.context = context;
//        this.imageIDarray = imageIDArrayParam;
//        this.nameArray = nameArrayParam;
//        this.infoArray = infoArrayParam;
//
//    }

//    public CustomListAdapter(Activity context, String[] imageIDArrayParam, String[] nameArrayParam, String[] dateArrayParam){
//
//        super(context, R.layout.listview_row , imageIDArrayParam);
//
//        this.context = context;
//        this.imageIDarray = imageIDArrayParam;
//        this.nameArray = nameArrayParam;
//        this.dateArray = dateArrayParam;
//
//    }

    public CustomListAdapter(Activity context, String[] nameArray, String[] imageIDarray, String[] dateArray) {
        super(context, R.layout.listview_row, nameArray);

        this.context = context;
        this.imageIDarray = imageIDarray;
        this.nameArray = nameArray;
        this.dateArray = dateArray;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_row, null,true);

        String it = imageIDarray[position];

            Log.d(TAG, "getView: Get position: " + position);
            //this code gets references to objects in the listview_row.xml file
            TextView nameTextField = (TextView) rowView.findViewById(R.id.nameTextViewID);
            TextView infoTextField = (TextView) rowView.findViewById(R.id.infoTextViewID);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1ID);

            //this code sets the values of the objects to values from the arrays
            nameTextField.setText(nameArray[position]);
            infoTextField.setText(dateArray[position]);
//            imageView.setImageResource(Integer.parseInt(imageIDarray[position]));
//        imageView.setImageDrawable();
        Picasso.with(context)
                .load(imageIDarray[position])
                .into(imageView);

        return rowView;
    };
}
