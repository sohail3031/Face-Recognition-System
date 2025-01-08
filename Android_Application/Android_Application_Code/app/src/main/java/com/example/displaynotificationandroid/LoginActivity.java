package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Method;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText editText1, editText2;
    Button button, wifi, mobile;
    Button forgetText, newUserText;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser user;
    boolean checkLogin = false;
    String check = "";
    LinearLayout buttonsLayout;
    Switch switch1, switch2;
//    CheckBox showHide;
//    CheckBox rememberText;
    TextView loginText;
    ProgressBar loginProgressBar;

    NetworkInfo activeNetworkInfo;
    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    String gotSignUpEmail;

    public static final String TAG = "MyTag";
    private static int SPLASH_TIME_OUT = 2000;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    boolean isEmailVerified = false;

    public void visibleUser(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        editText1 = findViewById(R.id.loginEmail);
        editText2 = findViewById(R.id.loginPassword);
        button = findViewById(R.id.loginButton);
        forgetText = findViewById(R.id.forgotPasswordText);
        newUserText = findViewById(R.id.newUserText);
        switch1 = findViewById(R.id.openWifiButton);
        switch2 = findViewById(R.id.openMobileData);
//        showHide = (CheckBox) findViewById(R.id.showPassword);
//        rememberText = findViewById(R.id.rememberText);
        loginText = findViewById(R.id.loginText);
        loginProgressBar = findViewById(R.id.loginProgressBar);

        Log.i("Info", newUserText.getText().toString());

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

//        Bundle extras = getIntent().getExtras();
//        gotSignUpEmail = extras.getString("email");
//
//        editText1.setText(gotSignUpEmail);

        boolean isWifiConn = false;
        boolean isMobileConn = false;

        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
                Log.d(TAG, "onCreate: wifi");
                checkLogin = true;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
                Log.d(TAG, "onCreate: data");
                checkLogin = true;
            }
        }

        Log.d(TAG, "onCreate: Login Internet: " + activeNetworkInfo);

        mFirebaseAuth.getCurrentUser();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null && mFirebaseUser.isEmailVerified()) {
                    Log.d(TAG, "onAuthStateChanged: Login: " + mFirebaseUser);
                    Log.d(TAG, "onAuthStateChanged: Login Activity: " + (getIntent() != null));
                    Log.d(TAG, "onCreate: Login Instance Data: " + (getIntent().hasExtra("key1")));

//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
                            Intent intent = getIntent();
                            Log.d(TAG, "onCreate: Home to Login Intent: " + intent);
                            String str = intent.getStringExtra("text");
                            String displayTitle = intent.getStringExtra("title");
                            String displayMessage = intent.getStringExtra("message");
                            String displayName = intent.getStringExtra("name");
                            String displayLoss = intent.getStringExtra("loss");
                            String displayDescription = intent.getStringExtra("description");
                            String displayImage = intent.getStringExtra("image");
                            String displayDateTime = intent.getStringExtra("dateTime");
                            String displayFirstTime = intent.getStringExtra("firstTime");
                            String displayLastTime = intent.getStringExtra("lastTime");
                            String displayPersonStatus = intent.getStringExtra("personStatus");

                            Intent i = new Intent(LoginActivity.this, NavigationActivity.class);
                            i.putExtra("text", str);
                            i.putExtra("title", displayTitle);
                            i.putExtra("message", displayMessage);
                            i.putExtra("name", displayName);
                            i.putExtra("loss", displayLoss);
                            i.putExtra("description", displayDescription);
                            i.putExtra("image", displayImage);
                            i.putExtra("userEmailAddress", editText1.getText().toString());
                            i.putExtra("dateTime", displayDateTime);
                            i.putExtra("firstTime", displayFirstTime);
                            i.putExtra("lastTime", displayLastTime);
                            i.putExtra("personStatus", displayPersonStatus);
                            startActivity(i);
//                        }
//                    }, SPLASH_TIME_OUT);
                }
                else {
                    Log.d(TAG, "onAuthStateChanged: else Login");
                    Intent signUpIntent = getIntent();
                    String userEmail = signUpIntent.getStringExtra("email");
                    Log.d(TAG, "onCreate: Received Email: " + userEmail);
                    editText1.setText(userEmail);
                }
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editText1.getText().toString().trim();
                String password = editText2.getText().toString().trim();

                Log.d(TAG, "onClick: active status: " + activeNetworkInfo);

                if (email.isEmpty()) {
                    editText1.setError("Please Provide Email");
                    editText1.requestFocus();
                } else if (password.isEmpty()) {
                    editText2.setError("Please Provide Password");
                    editText2.requestFocus();
                } else if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Felds Are Empty!", Toast.LENGTH_LONG).show();
                }
                else if (!checkLogin) {
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle("No Internet Connection");
                    alertDialog.setMessage("It seams you are offline but you need internet connection to login");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    switch1.setVisibility(View.VISIBLE);
                    switch2.setVisibility(View.VISIBLE);
                }
                else if (!(email.isEmpty() && password.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "onComplete: task: " + task);
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                loginProgressBar.setVisibility(View.VISIBLE);
                                if (mFirebaseAuth.getCurrentUser().isEmailVerified()) {
                                    Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_LONG).show();

                                    checkLogin = true;

                                    Intent intent = getIntent();
                                    Log.d(TAG, "onCreate: Home to Login Intent: " + intent);
                                    String str = intent.getStringExtra("text");
                                    String displayTitle = intent.getStringExtra("title");
                                    String displayMessage = intent.getStringExtra("message");
                                    String displayName = intent.getStringExtra("name");
                                    String displayLoss = intent.getStringExtra("loss");
                                    String displayDescription = intent.getStringExtra("description");
                                    String displayImage = intent.getStringExtra("image");
                                    String displayDateTime = intent.getStringExtra("dateTime");
                                    String displayFirstTime = intent.getStringExtra("firstTime");
                                    String displayLastTime = intent.getStringExtra("lastTime");
                                    String displayPersonStatus = intent.getStringExtra("personStatus");

                                    Intent toHome = new Intent(LoginActivity.this, NavigationActivity.class);
                                    toHome.putExtra("text", str);
                                    toHome.putExtra("title", displayTitle);
                                    toHome.putExtra("message", displayMessage);
                                    toHome.putExtra("name", displayName);
                                    toHome.putExtra("loss", displayLoss);
                                    toHome.putExtra("description", displayDescription);
                                    toHome.putExtra("image", displayImage);
                                    toHome.putExtra("dateTime", displayDateTime);
                                    toHome.putExtra("firstTime", displayFirstTime);
                                    toHome.putExtra("lastTime", displayLastTime);
                                    toHome.putExtra("personStatus", displayPersonStatus);
                                    startActivity(toHome);
                                    loginProgressBar.setVisibility(View.GONE);
                                    finish();
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Please Verify Your Email To Login", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(LoginActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgot = new Intent(LoginActivity.this, ForgotPasswordActivity.class);

                forgot.putExtra("email", editText1.getText().toString());

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(loginText, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
            startActivity(forgot, options.toBundle());

//                startActivity(forgot);
            }
        });

        newUserText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent signUp = new Intent(LoginActivity.this, SignUpActivity.class);
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivityOne.class);

                signUpIntent.putExtra("fName", "");
                signUpIntent.putExtra("lName", "");
                signUpIntent.putExtra("email", "");
                signUpIntent.putExtra("password1", "");
                signUpIntent.putExtra("password2", "");
                signUpIntent.putExtra("generatedPassword", "no");
                signUpIntent.putExtra("genetaredUserType", "");

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(loginText, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(signUpIntent, options.toBundle());

//                startActivity(signUpIntent);
            }
        });

//        newUserText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
//                startActivity(i);
//            }
//        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked)
                {
                    Log.d(TAG, "onCheckedChanged: if");
                    EnableWiFi() ;
                }
                else {
                    Log.d(TAG, "onCheckedChanged: else");
                    DisableWiFi();
                }
            }
        });

        switch2.setChecked(getMobileDataState());
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMobileDataState(isChecked);
            }
        });

//        showHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isChecked){
//                    Log.d(TAG, "onCheckedChanged: hidding password");
//                    editText2.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                }
//                else{
//                    Log.d(TAG, "onCheckedChanged: showing password");
//                    editText2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                }
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {

    }

    private void setMobileDataState(boolean isChecked) {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Method setMobileDataEnabledMethod = Objects.requireNonNull(telephonyService).getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            Log.d(TAG, "setMobileDataState: mobile data set data method");
            setMobileDataEnabledMethod.invoke(telephonyService, isChecked);
        }
        catch (Exception ex) {
            Log.e("MainActivity", "Error setting mobile data state", ex);
        }
    }

    public boolean getMobileDataState() {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = Objects.requireNonNull(telephonyService).getClass().getDeclaredMethod("getDataEnabled");
            Log.d(TAG, "getMobileDataState: mobile data get data method ");
            return (boolean) (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
        }
        catch (Exception ex) {
            Log.e("MainActivity", "Error getting mobile data state", ex);
        }
        return false;
    }

    public void EnableWiFi(){
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Toast.makeText(this, "Your now connected to internet", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "EnableWiFi: active status wifi:" + activeNetworkInfo);

        checkLogin = true;
    }

    public void DisableWiFi(){
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Toast.makeText(this, ":-( connection lost", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "EnableWiFi: active status wifi:" + activeNetworkInfo);

        checkLogin = false;
    }
}