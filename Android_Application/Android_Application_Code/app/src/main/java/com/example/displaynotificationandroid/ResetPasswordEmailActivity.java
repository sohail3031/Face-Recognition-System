package com.example.displaynotificationandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ResetPasswordEmailActivity extends AppCompatActivity {

    TextView title;
    Button goToLogin, resendEmailButton;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_reset_password_email);

        title = findViewById(R.id.emailSuccess);
        goToLogin = findViewById(R.id.goToLogin);
        resendEmailButton = findViewById(R.id.resendEmailButton);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordEmailActivity.this, LoginActivity.class);

                intent.putExtra("email", email);

                Pair[] pairs = new Pair[1];

                pairs[0] = new Pair<View, String>(title, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResetPasswordEmailActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

//        resendEmailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firebaseAuth.sendPasswordResetEmail(email_desc.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()){
//                            Intent intent = new Intent(ForgotPasswordSelectionActivity.this, ResetPasswordEmailActivity.class);
//
//                            intent.putExtra("email", email);
//
//                            Pair[] pairs = new Pair[1];
//
//                            pairs[0] = new Pair<View, String>(title, "logo_text");
//
//                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordSelectionActivity.this, pairs);
//                            startActivity(intent, options.toBundle());
//                        }
//                        else{
//                            Toast.makeText(ForgotPasswordSelectionActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//            }
//        });
    }

    @Override
    public void onBackPressed() {

    }
}
