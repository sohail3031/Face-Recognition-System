package com.example.displaynotificationandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Random;

public class GeneratePasswordFragment extends Fragment {

    private static final String LowerWords = "abcdefghijklmnopqrstuvwxyz";
    private static final String UpperWords = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NumbersWords = "0123456789";
    private static final String SpecialWords = "!@%^&*()_-+={};:,<>?";

    StringBuilder stringBuilder;
    Random random;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_generate_password, container, false);

        final CheckBox capitalLetter = view.findViewById(R.id.mobileCapitalLetterReset);
        final CheckBox smallletter = view.findViewById(R.id.mobileSmallLetterReset);
        final CheckBox numbers = view.findViewById(R.id.mobileNumbersReset);
        final CheckBox special = view.findViewById(R.id.mobileSpecialReset);
        final RadioGroup radioGroup = null;
        RadioButton radioButton;
        ImageView mobileBackButtonReset = view.findViewById(R.id.mobileBackButtonReset);
        Button mobileGeneratePasswordButtonReset = view.findViewById(R.id.mobileGeneratePasswordButtonReset);
        final Button mobilePasswordReset = view.findViewById(R.id.mobilePasswordReset);

        mobileBackButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, resetPasswordFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

//        mobileGeneratePasswordButtonReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int getSelectedId = radioGroup.getCheckedRadioButtonId();
//                radioButton = view.findViewById(getSelectedId);
//
//                if (!capitalLetter.isChecked() && !smallletter.isChecked() && !numbers.isChecked() && !special.isChecked()) {
//                    Toast.makeText(GeneratePassword.this, "Please Select At least One Choice", Toast.LENGTH_LONG).show();
//                }
//                else if (capitalLetter.isChecked() && smallletter.isChecked() && numbers.isChecked() && special.isChecked()) {
//                    passwordToBeGenerated = UpperWords + LowerWords + NumbersWords + SpecialWords;
//                }
//                else if (capitalLetter.isChecked() && smallletter.isChecked() && numbers.isChecked() && !special.isChecked()) {
//                    passwordToBeGenerated = UpperWords + LowerWords + NumbersWords;
//                }
//                else if (capitalLetter.isChecked() && smallletter.isChecked() && !numbers.isChecked() && special.isChecked()) {
//                    passwordToBeGenerated = UpperWords + LowerWords + SpecialWords;
//                }
//                else if (capitalLetter.isChecked() && smallletter.isChecked() && !numbers.isChecked() && !special.isChecked()) {
//                    passwordToBeGenerated = UpperWords + LowerWords;
//                }
//                else if (capitalLetter.isChecked() && !smallletter.isChecked() && numbers.isChecked() && special.isChecked()) {
//                    passwordToBeGenerated = UpperWords + NumbersWords + SpecialWords;
//                }
//                else if (capitalLetter.isChecked() && !smallletter.isChecked() && numbers.isChecked() && !special.isChecked()) {
//                    passwordToBeGenerated = UpperWords + NumbersWords;
//                }
//                else if (capitalLetter.isChecked() && !smallletter.isChecked() && !numbers.isChecked() && special.isChecked()) {
//                    passwordToBeGenerated = UpperWords + SpecialWords;
//                }
//                else if (capitalLetter.isChecked() && !smallletter.isChecked() && !numbers.isChecked() && !special.isChecked()) {
//                    passwordToBeGenerated = UpperWords;
//                }
//                else if (!capitalLetter.isChecked() && smallletter.isChecked() && numbers.isChecked() && special.isChecked()) {
//                    passwordToBeGenerated = LowerWords + NumbersWords + SpecialWords;
//                }
//                else if (!capitalLetter.isChecked() && smallletter.isChecked() && numbers.isChecked() && !special.isChecked()) {
//                    passwordToBeGenerated = LowerWords + NumbersWords;
//                }
//                else if (!capitalLetter.isChecked() && smallletter.isChecked() && !numbers.isChecked() && special.isChecked()) {
//                    passwordToBeGenerated = LowerWords + SpecialWords;
//                }
//                else if (!capitalLetter.isChecked() && smallletter.isChecked() && !numbers.isChecked() && !special.isChecked()) {
//                    passwordToBeGenerated = LowerWords;
//                }
//                else if (!capitalLetter.isChecked() && !smallletter.isChecked() && numbers.isChecked() && special.isChecked()) {
//                    passwordToBeGenerated = NumbersWords + SpecialWords;
//                }
//                else if (!capitalLetter.isChecked() && !smallletter.isChecked() && numbers.isChecked() && !special.isChecked()) {
//                    passwordToBeGenerated = NumbersWords;
//                }
//                else if (!capitalLetter.isChecked() && !smallletter.isChecked() && !numbers.isChecked() && special.isChecked()) {
//                    passwordToBeGenerated = SpecialWords;
//                }
//
//                if (getSelectedId == -1) {
//                    Toast.makeText(GeneratePassword.this, "Please Select Length Of Password", Toast.LENGTH_LONG).show();
//                }
//                else{
//                    switch (getSelectedId){
//                        case R.id.sixLength:
//                            passwordLengthToBeGenerated = 6;
//                            Log.d(TAG, "onClick: " + passwordLengthToBeGenerated);
//                            break;
//                        case R.id.tenLength:
//                            passwordLengthToBeGenerated = 10;
//                            Log.d(TAG, "onClick: " + passwordLengthToBeGenerated);
//                            break;
//                        case R.id.twelveLength:
//                            passwordLengthToBeGenerated = 12;
//                            Log.d(TAG, "onClick: " + passwordLengthToBeGenerated);
//                            break;
//                        case R.id.sixteenLength:
//                            passwordLengthToBeGenerated = 16;
//                            Log.d(TAG, "onClick: " + passwordLengthToBeGenerated);
//                            break;
//                    }
//
//                    random = new Random();
//
//                    stringBuilder = new StringBuilder(passwordLengthToBeGenerated);
//
//                    for (int i = 0; i<passwordLengthToBeGenerated; i++){
//                        stringBuilder.append(passwordToBeGenerated.charAt(random.nextInt(passwordToBeGenerated.length())));
//                        Log.d(TAG, "onClick: " + passwordToBeGenerated.charAt(random.nextInt(passwordToBeGenerated.length())));
////                        stringBuilder.append((char) ThreadLocalRandom.current().nextInt(MIN_CODE, MAX_CODE+1));
//                        stringBuilder.toString();
//                    }
//                    Log.d(TAG, "onClick: Password Generated");
//                    generateStrongPassword.setText(stringBuilder.toString());
//                }
//            }
//        });

        mobilePasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, resetPasswordFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
