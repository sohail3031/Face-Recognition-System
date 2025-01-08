package com.example.displaynotificationandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GeneratePassword extends AppCompatActivity {

    CheckBox capitalLetter, smallletter, numbers, special;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button generatePassword, goToSignUp;
    EditText generateStrongPassword;
    TextView titleText;

    private static final String LowerWords = "abcdefghijklmnopqrstuvwxyz";
    private static final String UpperWords = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NumbersWords = "0123456789";
    private static final String SpecialWords = "!@%^&*()_-+={};:,<>?";

    String passwordToBeGenerated;
    int passwordLengthToBeGenerated;

    public static final String TAG = "MyTag";
    private static final int MIN_CODE = 33, MAX_CODE = 126;

    StringBuilder stringBuilder;
    Random random;
//    Bundle extras = getIntent().getExtras();

    String fName, lName, userEmail, pass1, pass2, verifyType;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_generate_password);

        capitalLetter = findViewById(R.id.capitalLetter);
        smallletter = findViewById(R.id.smallLetter);
        numbers = findViewById(R.id.numbers);
        special = findViewById(R.id.special);
        radioGroup = findViewById(R.id.passwordLengthGroup);
        generatePassword = findViewById(R.id.generatePasswordButton);
        generateStrongPassword = findViewById(R.id.generateStrongPassword);
        goToSignUp = findViewById(R.id.goToSignUp);
        titleText = findViewById(R.id.generatePasswordTitle);

        Bundle extras = getIntent().getExtras();
        fName = extras.getString("fName");
        lName = extras.getString("lName");
        userEmail = extras.getString("email");
        pass1 = extras.getString("password1");
        pass2 = extras.getString("password2");
        verifyType = extras.getString("genetaredUserType");

        generatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int getSelectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(getSelectedId);

                if (!capitalLetter.isChecked() && !smallletter.isChecked() && !numbers.isChecked() && !special.isChecked()) {
                    Toast.makeText(GeneratePassword.this, "Please Select At least One Choice", Toast.LENGTH_LONG).show();
                }
                else if (capitalLetter.isChecked() && smallletter.isChecked() && numbers.isChecked() && special.isChecked()) {
                    passwordToBeGenerated = UpperWords + LowerWords + NumbersWords + SpecialWords;
                }
                else if (capitalLetter.isChecked() && smallletter.isChecked() && numbers.isChecked() && !special.isChecked()) {
                    passwordToBeGenerated = UpperWords + LowerWords + NumbersWords;
                }
                else if (capitalLetter.isChecked() && smallletter.isChecked() && !numbers.isChecked() && special.isChecked()) {
                    passwordToBeGenerated = UpperWords + LowerWords + SpecialWords;
                }
                else if (capitalLetter.isChecked() && smallletter.isChecked() && !numbers.isChecked() && !special.isChecked()) {
                    passwordToBeGenerated = UpperWords + LowerWords;
                }
                else if (capitalLetter.isChecked() && !smallletter.isChecked() && numbers.isChecked() && special.isChecked()) {
                    passwordToBeGenerated = UpperWords + NumbersWords + SpecialWords;
                }
                else if (capitalLetter.isChecked() && !smallletter.isChecked() && numbers.isChecked() && !special.isChecked()) {
                    passwordToBeGenerated = UpperWords + NumbersWords;
                }
                else if (capitalLetter.isChecked() && !smallletter.isChecked() && !numbers.isChecked() && special.isChecked()) {
                    passwordToBeGenerated = UpperWords + SpecialWords;
                }
                else if (capitalLetter.isChecked() && !smallletter.isChecked() && !numbers.isChecked() && !special.isChecked()) {
                    passwordToBeGenerated = UpperWords;
                }
                else if (!capitalLetter.isChecked() && smallletter.isChecked() && numbers.isChecked() && special.isChecked()) {
                    passwordToBeGenerated = LowerWords + NumbersWords + SpecialWords;
                }
                else if (!capitalLetter.isChecked() && smallletter.isChecked() && numbers.isChecked() && !special.isChecked()) {
                    passwordToBeGenerated = LowerWords + NumbersWords;
                }
                else if (!capitalLetter.isChecked() && smallletter.isChecked() && !numbers.isChecked() && special.isChecked()) {
                    passwordToBeGenerated = LowerWords + SpecialWords;
                }
                else if (!capitalLetter.isChecked() && smallletter.isChecked() && !numbers.isChecked() && !special.isChecked()) {
                    passwordToBeGenerated = LowerWords;
                }
                else if (!capitalLetter.isChecked() && !smallletter.isChecked() && numbers.isChecked() && special.isChecked()) {
                    passwordToBeGenerated = NumbersWords + SpecialWords;
                }
                else if (!capitalLetter.isChecked() && !smallletter.isChecked() && numbers.isChecked() && !special.isChecked()) {
                    passwordToBeGenerated = NumbersWords;
                }
                else if (!capitalLetter.isChecked() && !smallletter.isChecked() && !numbers.isChecked() && special.isChecked()) {
                    passwordToBeGenerated = SpecialWords;
                }

                if (getSelectedId == -1) {
                    Toast.makeText(GeneratePassword.this, "Please Select Length Of Password", Toast.LENGTH_LONG).show();
                }
                else{
                    switch (getSelectedId){
                        case R.id.sixLength:
                            passwordLengthToBeGenerated = 6;
                            Log.d(TAG, "onClick: " + passwordLengthToBeGenerated);
                            break;
                        case R.id.tenLength:
                            passwordLengthToBeGenerated = 10;
                            Log.d(TAG, "onClick: " + passwordLengthToBeGenerated);
                            break;
                        case R.id.twelveLength:
                            passwordLengthToBeGenerated = 12;
                            Log.d(TAG, "onClick: " + passwordLengthToBeGenerated);
                            break;
                        case R.id.sixteenLength:
                            passwordLengthToBeGenerated = 16;
                            Log.d(TAG, "onClick: " + passwordLengthToBeGenerated);
                            break;
                    }

                    random = new Random();

                     stringBuilder = new StringBuilder(passwordLengthToBeGenerated);

                    for (int i = 0; i<passwordLengthToBeGenerated; i++){
                        stringBuilder.append(passwordToBeGenerated.charAt(random.nextInt(passwordToBeGenerated.length())));
                        Log.d(TAG, "onClick: " + passwordToBeGenerated.charAt(random.nextInt(passwordToBeGenerated.length())));
//                        stringBuilder.append((char) ThreadLocalRandom.current().nextInt(MIN_CODE, MAX_CODE+1));
                        stringBuilder.toString();
                    }
                    Log.d(TAG, "onClick: Password Generated");
                    generateStrongPassword.setText(stringBuilder.toString());
                }
            }
        });

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + stringBuilder);
                Intent nextIntent = new Intent(GeneratePassword.this, SignUpActivityOne.class);
                Log.d(TAG, "onClick: gf" + fName);
                Log.d(TAG, "onClick: gl" + lName);

                nextIntent.putExtra("fName", fName);
                nextIntent.putExtra("lName", lName);
                nextIntent.putExtra("email", userEmail);
                nextIntent.putExtra("generatedPassword", "yes");
                nextIntent.putExtra("genetaredUserType", verifyType);
                nextIntent.putExtra("password1", (Serializable) stringBuilder);
                nextIntent.putExtra("password2", (Serializable) stringBuilder);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(titleText, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(GeneratePassword.this, pairs);
                startActivity(nextIntent, options.toBundle());
            }
        });
    }
}
