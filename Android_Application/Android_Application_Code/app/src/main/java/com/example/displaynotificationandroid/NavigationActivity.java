package com.example.displaynotificationandroid;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import hotchemi.android.rate.AppRate;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    public static final String TAG = "MyTag";

    String displayTitle, displayMessage, displayName, displayLoss, displayDescription, displayImage,
            str, userEmailAddress, name, displayDateTime, displayFirstTime, displayLastTime, loginAs,
            displayPersonStatus;
    int info = 0;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    HomeFragment homeFragment;
//    Bundle bundle;

    TextView second;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference notifyDatabase;

    int notificationCounter = 0;
    String notificationCounterString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        AppRate.with(this)
                .setInstallDays(1)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);

        second = findViewById(R.id.second);

        Intent intent = getIntent();
        Log.d(TAG, "onCreate: Home to Main Intent: " + intent);
        str = intent.getStringExtra("text");
        displayTitle = intent.getStringExtra("title");
        displayMessage = intent.getStringExtra("message");
        displayName = intent.getStringExtra("name");
        displayLoss = intent.getStringExtra("loss");
        displayDescription = intent.getStringExtra("description");
        displayImage = intent.getStringExtra("image");
        userEmailAddress = intent.getStringExtra("userEmailAddress");
        displayDateTime = intent.getStringExtra("dateTime");
        displayFirstTime = intent.getStringExtra("firstTime");
        displayLastTime = intent.getStringExtra("lastTime");
        displayPersonStatus = intent.getStringExtra("personStatus");
//        Log.d(TAG, "onCreate: Main Intent: " + intent);
        Log.d(TAG, "onCreate: Nav str: " + str);
        Log.d(TAG, "onCreate: Nav displayTitle: " + displayTitle);
        Log.d(TAG, "onCreate: Nav displayMessage: " + displayMessage);
        Log.d(TAG, "onCreate: Nav displayName: " + displayName);
        Log.d(TAG, "onCreate: Nav displayLoss: " + displayLoss);
        Log.d(TAG, "onCreate: Nav displayDescription: " + displayDescription);
        Log.d(TAG, "onCreate: Nav displayImage: " + displayImage);
        Log.d(TAG, "onCreate: Nav userEmailAddress: " + userEmailAddress);
        Log.d(TAG, "onCreate: Nav userEmailAddressFirebase: " + FirebaseAuth.getInstance().getCurrentUser().getEmail());

//        if (!displayTitle.equals("")) {
//            second.setVisibility(View.VISIBLE);
//        }

//        if (displayTitle == "Person Identified") {
//            info = 2;
//        }
//        else {
//            info = 1;
//        }

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_test)
//                .setDrawerLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Welcome");

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        final TextView nav_bar_title = headerView.findViewById(R.id.nav_bar_title);
        final TextView nav_bar_desc = headerView.findViewById(R.id.nav_bar_desc);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser();

        userEmailAddress = FirebaseAuth.getInstance().getCurrentUser().getEmail();

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
                        getSupportActionBar().setTitle("Welcome " + String.valueOf(dataSnapshot1.child("userFirstName").getValue()));
                        name = String.valueOf(dataSnapshot1.child("userFirstName").getValue());
                        nav_bar_title.setText("Welcome " + name);
                        nav_bar_desc.setText("Login As: " + String.valueOf(dataSnapshot1.child("userTypeAccount").getValue()));

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

//        nav_bar_title.setText(name);
//        nav_bar_desc.setText(userEmailAddress);

//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//        homeFragment = new HomNavigationActivityeFragment();
//
//        Bundle bundle = new Bundle();
//        bundle.putString("str", str);
//        homeFragment.setArguments(bundle);
//        fragmentTransaction.commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
//                new HomeFragment()).commit();

        Bundle extras = intent.getExtras();
//        String dt = extras.getString("title").toString();

        if (extras != null) {
            notificationCounter = notificationCounter + 1;
            notificationCounterString = "yes";
        }
        else{
            notificationCounterString = "no";
        }

        HomeFragment homeFragment = HomeFragment.newInstance(displayTitle, displayMessage, displayName,
                displayLoss, displayDescription, displayImage, displayDateTime, displayFirstTime,
                displayLastTime, notificationCounterString, loginAs, displayPersonStatus);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, homeFragment).commit();
        navigationView.setCheckedItem(R.id.nav_home);

//        if (savedInstanceState == null) {
////            fragmentManager = getSupportFragmentManager();
////            fragmentTransaction = fragmentManager.beginTransaction();
////            homeFragment = new HomeFragment();
////
////            bundle = new Bundle();
////            bundle.putString("str", str);
////            homeFragment.setArguments(bundle);
////            fragmentTransaction.commit();
////            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
////                    new HomeFragment()).commit();
//            homeFragment = HomeFragment.newInstance("sohail", 123);
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, homeFragment).commit();
//            navigationView.setCheckedItem(R.id.nav_home);
//        }
    }

//    @Override
//    public void onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START)){
//            drawer.closeDrawer(GravityCompat.START);
//        }
//        else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.navigation, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                HomeFragment homeFragment = HomeFragment.newInstance(displayTitle, displayMessage, displayName,
                        displayLoss, displayDescription, displayImage, displayDateTime, displayFirstTime,
                        displayLastTime, notificationCounterString, loginAs, displayPersonStatus);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, homeFragment).commit();
                break;
            case R.id.nav_notification:
                NotificationFragment notificationFragment = NotificationFragment.newInstance(displayTitle,
                        displayMessage, displayName, displayLoss, displayDescription, displayImage,
                        displayDateTime, displayFirstTime, displayLastTime, loginAs, displayPersonStatus);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, notificationFragment).commit();
                break;
            case R.id.nav_checkNotifications:
                NotificationListFragment notificationListFragment = NotificationListFragment.newInstance(displayTitle,
                        displayMessage, displayName, displayLoss, displayDescription, displayImage,
                        displayDateTime, displayFirstTime, displayLastTime, loginAs, displayPersonStatus);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, notificationListFragment).commit();
                break;
            case R.id.nav_resetPassword:
                ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, resetPasswordFragment).commit();
                break;
            case R.id.nav_chat:
                Toast.makeText(this, "Send Message", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_add_employee:
                if (loginAs.equalsIgnoreCase("admin")) {
                    AddEmployeeFragment addEmployeeFragment = new AddEmployeeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, addEmployeeFragment).commit();
                }
                else {
                    Toast.makeText(this, "You are not allowed to perform this task", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_modify_employee:
                if (loginAs.equalsIgnoreCase("admin")) {
                    ModifyEmployeeFragment modifyEmployeeFragment = new ModifyEmployeeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, modifyEmployeeFragment).commit();
                }
                else {
                    Toast.makeText(this, "You are not allowed to perform this task", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_contactUs:
                ContactUsFragment contactUsFragment = new ContactUsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, contactUsFragment).commit();
                break;
            case R.id.nav_account:
                AccountInfoFragment accountInfoFragment = new AccountInfoFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, accountInfoFragment).commit();
                break;
            case R.id.nav_share:
//                Toast.makeText(this, "share", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plane");

                String shareLink = "Download This Application Now: https://play.google.com/store/apps/details?id=com.android.chrome&h1=en";
                String shareSub = "Face Recognition System App";

                intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                intent.putExtra(Intent.EXTRA_TEXT, shareLink);

                startActivity(Intent.createChooser(intent, "Share Using"));
                break;
            case R.id.nav_rateUs:
                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("market://details?id=" + getPackageName())));
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.android.chrome")));
                }
                catch (ActivityNotFoundException e){
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.android.chrome")));
                }
                break;
            case R.id.nav_logout:
//                Toast.makeText(this, "send", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();

                Intent toLogin = new Intent(NavigationActivity.this, LoginActivity.class);
                startActivity(toLogin);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

//    @Override
//    public void onBackPressed() {
//        List fragmentList = getSupportFragmentManager().getFragments();
//
//        boolean handled = false;
//        for(Fragment f : fragmentList) {
//            if(f instanceof BaseFragment) {
//                handled = ((BaseFragment)f).onBackPressed();
//
//                if(handled) {
//                    break;
//                }
//            }
//        }
//
//        if(!handled) {
//            super.onBackPressed();
//        }
//    }
}
