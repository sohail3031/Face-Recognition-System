package com.example.displaynotificationandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PasswordResetSuccessActivity extends AppCompatActivity {

    ImageView passwordUpdatedBack;
    TextView titleText;
    Button resetPassword;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_success);

        passwordUpdatedBack = findViewById(R.id.passwordUpdatedBack);
        titleText = findViewById(R.id.successTitle);
        resetPassword = findViewById(R.id.resetPassword);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");

        passwordUpdatedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordResetSuccessActivity.this, LoginActivity.class);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(titleText, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PasswordResetSuccessActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordResetSuccessActivity.this, LoginActivity.class);

                intent.putExtra("email", email);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(titleText, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PasswordResetSuccessActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });
    }
}
