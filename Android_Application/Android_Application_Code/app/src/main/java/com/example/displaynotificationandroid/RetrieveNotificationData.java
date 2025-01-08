package com.example.displaynotificationandroid;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class RetrieveNotificationData extends ArrayAdapter<StoreNotificationData> {

    private static final String TAG = "MyTag";
    private Activity context;
    public List<StoreNotificationData> displayData;

    public RetrieveNotificationData(Activity context, List<StoreNotificationData> displayData){
        super(context, R.layout.listview_row, displayData);
        this.context = context;
        this.displayData = displayData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listItems = inflater.inflate(R.layout.listview_row, null, true);

        ImageView imageView = listItems.findViewById(R.id.imageView1ID);
        TextView text1 = listItems.findViewById(R.id.nameTextViewID);
        TextView text2 = listItems.findViewById(R.id.infoTextViewID);

        StoreNotificationData storeData = displayData.get(position);

        text1.setText(storeData.getNotifyPersonStatus());
        text2.setText(storeData.getNotifyDateTime());
        Picasso.with(getContext())
                .load(storeData.getNotifyImage())
                .placeholder(R.drawable.ic_android_black_24dp)
                .into(imageView);

        return listItems;
    }
}
