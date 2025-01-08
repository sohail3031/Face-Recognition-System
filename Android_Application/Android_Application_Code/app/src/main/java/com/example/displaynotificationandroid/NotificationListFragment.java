package com.example.displaynotificationandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class NotificationListFragment extends Fragment {

    DatabaseReference notifyDatabase, mDatabase;
    StoreNotificationData store;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    private ArrayAdapter adapter;

    List<StoreNotificationData> storeNotificationData;
    ArrayList<StoreNotificationData> arrayList = new ArrayList<StoreNotificationData>();

    String[] imageArray = new String[30];

    String[] dateArray = new String[30];

    ListView listView;

    public static final String TAG = "MyTag";

    DatabaseHelper mDatabaseHelper;

    int length_count = 0;
    int array_length = imageArray.length;

    private String title, message, personName, loss, description, image, dateTime, firstTime, lastTime, loginAs, gotSelectedKey;
    private int info = 0;
    private int getSelectedPosition = 0;

    private static final String Arg_Title = "title";
    private static final String Arg_Message = "message";
    private static final String Arg_Name = "name";
    private static final String Arg_Loss = "loss";
    private static final String Arg_Description = "description";
    private static final String Arg_Image = "image";
    private static final String Arg_DATE_TIME = "dateTime";
    private static final String Arg_FIRST_TIME = "firstTime";
    private static final String Arg_LAST_TIME = "lastTime";
    private static final String Arg_LOGIN_AS = "login";
    private static final String Arg_PERSON_STATUS = "status";
    private static final String Arg_Number = "number";

    Button logoutButton;
    TextView text, displayPersonName, displayPersonLoss, displayPersonDescription;
    FirebaseAuth mFirebaseAuth;
    ImageView userImage;

    private static int SPLASH_TIME_OUT = 3000;

    public static NotificationListFragment newInstance(String title, String message, String personName,
                                                   String loss, String description, String image, String dateTime,
                                                       String firstTime, String lastTime, String loginAs, String status){
        NotificationListFragment notificationListFragment = new NotificationListFragment();
        Bundle arg = new Bundle();
        arg.putString(Arg_Title, title);
        arg.putString(Arg_Message, message);
        arg.putString(Arg_Name, personName);
        arg.putString(Arg_Loss, loss);
        arg.putString(Arg_Description, description);
        arg.putString(Arg_Image, image);
        arg.putString(Arg_DATE_TIME, dateTime);
        arg.putString(Arg_FIRST_TIME, firstTime);
        arg.putString(Arg_LAST_TIME, lastTime);
        arg.putString(Arg_LOGIN_AS, loginAs);
        arg.putString(Arg_PERSON_STATUS, status);

        notificationListFragment.setArguments(arg);
        return notificationListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notification_list, container, false);

        listView = v.findViewById(R.id.displayNotificationList);

        adapter = new ArrayAdapter(getActivity(), R.layout.listview_row, storeNotificationData);

        final TextView nav_bar_desc = v.findViewById(R.id.nav_bar_desc);
        String type;

        final String userEmailAddress = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        notifyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String firstKey = dataSnapshot1.getKey();
                    Log.d("NewTag", "onDataChange: first: " + firstKey);
                    String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
                    Log.d("NewTag", "onDataChange: second: " + databaseEmail);
                    if (userEmailAddress.equals(databaseEmail)) {
                        loginAs = String.valueOf(dataSnapshot1.child("userTypeAccount").getValue());
                        break;
                    }
                    else {
                        continue;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        notifyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Data");
//        addNotificationData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                RetrieveNotificationData retrieveNotificationData = new RetrieveNotificationData(getActivity(), storeNotificationData);

                String title = retrieveNotificationData.displayData.get(position).getNotifyTitle().toString();
                String message = retrieveNotificationData.displayData.get(position).getNotifyMessage().toString();
                String personName = retrieveNotificationData.displayData.get(position).getNotifyPersonName().toString();
                String loss = retrieveNotificationData.displayData.get(position).getNotifyLoss().toString();
                String description = retrieveNotificationData.displayData.get(position).getNotifyDescription().toString();
                String image = retrieveNotificationData.displayData.get(position).getNotifyImage().toString();
                String dateTime = retrieveNotificationData.displayData.get(position).getNotifyDateTime().toString();
                String firstTime = retrieveNotificationData.displayData.get(position).getNotifyFirstTime().toString();
                String lastTime = retrieveNotificationData.displayData.get(position).getNotifyLastTime().toString();
                String status = retrieveNotificationData.displayData.get(position).getNotifyPersonStatus().toString();

//                dateTime = "date";

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, SPLASH_TIME_OUT);

                NotificationListShowFragment notificationFragment = NotificationListShowFragment.newInstance(title,
                        message, personName, loss, description, image, dateTime, firstTime, lastTime, loginAs, status);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, notificationFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final int selectedItem = position;

                StoreNotificationData storeData = storeNotificationData.get(position);
//                Toast.makeText(getContext(), storeData.toString(), Toast.LENGTH_SHORT).show();
                Log.d("sohail", "onItemLongClick: " + storeData.toString());

                if (loginAs.equalsIgnoreCase("admin")) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Are you sure ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    getSelectedPosition = position;
//                                    Toast.makeText(getContext(), "Please Wait", Toast.LENGTH_SHORT).show();

                                    notifyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Data");
                                    notifyDatabase.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int countKey = 0;
//                                            Log.d("sohail", "onDataChange: please wait: ");
                                            Toast.makeText(getContext(), "Please Wait", Toast.LENGTH_SHORT).show();
                                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                                String firstKey = dataSnapshot1.getKey().toString();
                                                Log.d("sohail", "onDataChange: first key: " + firstKey);
                                                String dateDelete = String.valueOf(dataSnapshot1.child("notifyDateTime").getValue());
                                                String imageDeleteUrl = String.valueOf(dataSnapshot1.child("notifImage").getValue());
                                                gotSelectedKey = firstKey;
                                                String demo = String.valueOf(dataSnapshot1.getKey());
                                                if (countKey == getSelectedPosition && firstKey.equals(gotSelectedKey)){
                                                    Log.d("sohail", "onDataChange: Item deleted: " + dateDelete.toString());
                                                    Log.d("sohail", "onDataChange: key found: " + firstKey);
//                                                    Toast.makeText(getContext(), "key found: " + firstKey, Toast.LENGTH_SHORT).show();

//                                                    RetrieveNotificationData retrieveNotificationData = new RetrieveNotificationData(getActivity(), storeNotificationData);
//                                                    String imageUrl = retrieveNotificationData.displayData.get(position).getNotifyImage().toString();
//
//                                                    Task<Void> deleteValue = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(dataSnapshot1.getKey()).setValue(null);
//                                                    deleteValue.addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            Toast.makeText(getContext(), "Data Deleted", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    });

//                                                    storeNotificationData.remove(position);
//                                                    adapter.notifyDataSetChanged();

//                                                    DatabaseReference deleteDataRefDateTime = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(firstKey).child("notifyDateTime");
//                                                    deleteDataRefDateTime.setValue(null);
//                                                    DatabaseReference deleteDataRefDescription = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(firstKey).child("notifyDescription");
//                                                    deleteDataRefDescription.setValue(null);
//                                                    DatabaseReference deleteDataRefFirstTime = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(firstKey).child("notifyFirstTime");
//                                                    deleteDataRefFirstTime.setValue(null);
//                                                    DatabaseReference deleteDataRefImage = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(firstKey).child("notifyImage");
//                                                    deleteDataRefImage.setValue(null);
//                                                    DatabaseReference deleteDataRefLastTime = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(firstKey).child("notifyLastTime");
//                                                    deleteDataRefLastTime.setValue(null);
//                                                    DatabaseReference deleteDataRefLoss = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(firstKey).child("notifyLoss");
//                                                    deleteDataRefLoss.setValue(null);
//                                                    DatabaseReference deleteDataRefMessage = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(firstKey).child("notifyMessage");
//                                                    deleteDataRefMessage.setValue(null);
//                                                    DatabaseReference deleteDataRefPersonName = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(firstKey).child("notifyPersonName");
//                                                    deleteDataRefPersonName.setValue(null);
//                                                    DatabaseReference deleteDataRefTitle = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(firstKey).child("notifyTitle");
//                                                    deleteDataRefTitle.setValue(null);

//                                                    Query deleteQuery = mDatabase.equalTo("firstKey");
//                                                    deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                            for(DataSnapshot delData: dataSnapshot.getChildren()){
//                                                                delData.getRef().removeValue();
//                                                            }
//                                                            Toast.makeText(getContext(),"Data Deleted",Toast.LENGTH_LONG).show();
//                                                            Log.d("sohail", "onDataChange: Data Deleted");
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                                                            Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
//                                                            Log.d("sohail", "onDataChange: Error: " + databaseError.getMessage());
//                                                        }
//                                                    });

//                                                    if (firstKey.equals(gotSelectedKey)) {
////                                                        Task<Void> deleteValue = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(firstKey).setValue(null);
////                                                        notifyDatabase.child(firstKey).removeValue();
////                                                        notifyDatabase.child(firstKey).setValue(null);
////                                                        notifyDatabase.child("Notification Data").child(firstKey).child("notifyDateTime").removeValue(null);
////                                                        notifyDatabase.child("Notification Data").child(firstKey).child("notifyDescription").removeValue(null);
////                                                        notifyDatabase.child("Notification Data").child(firstKey).child("notifyFirstTime").removeValue(null);
////                                                        notifyDatabase.child("Notification Data").child(firstKey).child("notifyImage").removeValue(null);
////                                                        notifyDatabase.child("Notification Data").child(firstKey).child("notifyLastTime").removeValue(null);
////                                                        notifyDatabase.child("Notification Data").child(firstKey).child("notifyLastLoss").removeValue(null);
////                                                        notifyDatabase.child("Notification Data").child(firstKey).child("notifyMessage").removeValue(null);
////                                                        notifyDatabase.child("Notification Data").child(firstKey).child("notifyPersonName").removeValue(null);
////                                                        notifyDatabase.child("Notification Data").child(firstKey).child("notifyTitle").removeValue(null);
////                                                        FirebaseDatabase.getInstance().getReference().child("Notification Data").child(firstKey).removeValue();
////                                                        FirebaseDatabase.getInstance().getReference().child("Notification Data").child(getRef(position+1).getKey).removeValue();
//                                                        break;
//                                                    }
                                                    break;
                                                }
                                                else{
                                                    countKey = countKey + 1;
                                                    continue;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
//                                            Toast.makeText(getContext(), "ket: " + gotSelectedKey, Toast.LENGTH_SHORT).show();

                                            if (!gotSelectedKey.equals("")) {
                                                Task<Void> deleteValue = FirebaseDatabase.getInstance().getReference().child("Notification Data").child(gotSelectedKey).setValue(null);
                                                deleteValue.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }, SPLASH_TIME_OUT);

//                                    notifyDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
//                                    notifyDatabase.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                                                String firstKey = dataSnapshot1.getKey();
//                                                Log.d("NewTag", "onDataChange: first: " + firstKey);
//                                                String databaseEmail = String.valueOf(dataSnapshot1.child("userEmailId").getValue());
//                                                Log.d("NewTag", "onDataChange: second: " + databaseEmail);
//                                                if (userEmailAddress.equals(databaseEmail)) {
//                                                    getSupportActionBar().setTitle("Welcome " + String.valueOf(dataSnapshot1.child("userFirstName").getValue()));
//                                                    name = String.valueOf(dataSnapshot1.child("userFirstName").getValue());
//                                                    nav_bar_title.setText("Welcome " + name);
//                                                    nav_bar_desc.setText("Login As: " + String.valueOf(dataSnapshot1.child("userTypeAccount").getValue()));
//
//                                                    loginAs = String.valueOf(dataSnapshot1.child("userTypeAccount").getValue());
//                                                    break;
//                                                }
//                                                else {
//                                                    continue;
//                                                }
//                                            }
//                                        }

//                                    Toast.makeText(getContext(), "Position: " + position, Toast.LENGTH_SHORT).show();
//                                    storeNotificationData.remove(position);
//                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                else{
                    Toast.makeText(getContext(), "You are not allowed to perform this task", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        return v;
    }

    private void addNotificationData() {
            store = new StoreNotificationData();

        store.setNotifyTitle(title);
        store.setNotifyMessage(message);
        store.setNotifyPersonName(personName);
        store.setNotifyLoss(loss);
        store.setNotifyDescription(description);
        store.setNotifyImage(image);
        store.setNotifyDateTime(dateTime);
        store.setNotifyFirstTime(firstTime);
        store.setNotifyLastTime(lastTime);

        notifyDatabase.push().setValue(store);

        Log.d(TAG, "addNotificationData: Notification Data Inserted!");
    }


    @Override
    public void onStart() {
        super.onStart();

        notifyDatabase = FirebaseDatabase.getInstance().getReference().child("Notification Data");
        storeNotificationData = new ArrayList<>();

        notifyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                storeNotificationData.clear();

                for (DataSnapshot displayShapshot: dataSnapshot.getChildren()){
                    Log.d("NewTag", "onDataChange: Shapshot: " + displayShapshot);
                    Log.d("NewTag", "onDataChange: Shapshot: " + displayShapshot.getValue());
                    StoreNotificationData storeData = displayShapshot.getValue(StoreNotificationData.class);
                    Log.d("NewTag", "onDa0taChange: Store Data: " + storeData);
                    storeNotificationData.add(storeData);

//                    RetrieveNotificationData retriveData = new RetrieveNotificationData(DisplayDataActivity.this, storeNotificationData);
//                    listView.setAdapter(retriveData);

                }

                if (getActivity() != null) {
                    RetrieveNotificationData retrieveNotificationData = new RetrieveNotificationData(getActivity(), storeNotificationData);
                    listView.setAdapter(retrieveNotificationData);
//                Log.d("NewTag", "onDataChange: " + retrieveNotificationData.displayData.get(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void AddData (String name, String loss, String description, String image, String date, String time) {
        boolean insertingData = mDatabaseHelper.addData(name, loss, description, image, date, time);

        if (insertingData) {
            toastMessage("Data Inserted Successfully!");
        }
        else {
            toastMessage("Something Went Wrong!");
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
