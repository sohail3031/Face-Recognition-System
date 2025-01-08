package com.example.displaynotificationandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OnBoardingActivity extends AppCompatActivity {

    private static final String TAG = "MyTag";

    ViewPager viewPager;
    LinearLayout dotsLayout;
    TextView[] dots;
    Button getStartedButton;
    RelativeLayout relativeLayout;

    SliderAdapter sliderAdapter;
    Animation animation;

    int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_on_boarding);

        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        getStartedButton = findViewById(R.id.get_started_button);
        relativeLayout = findViewById(R.id.relativeLayout);

        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }

    public void skip(View view){
        Intent homeIntent = new Intent(OnBoardingActivity.this, LoginActivity.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(relativeLayout, "logo_text");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(OnBoardingActivity.this, pairs);
        startActivity(homeIntent, options.toBundle());

        finish();
    }

    public void next(View view){
        viewPager.setCurrentItem(currentPosition + 1);
    }

    public void gotoLogin(View view){
        Intent homeIntent = new Intent(OnBoardingActivity.this, LoginActivity.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(relativeLayout, "logo_text");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(OnBoardingActivity.this, pairs);
        startActivity(homeIntent, options.toBundle());

        finish();
    }

    private void addDots(int position){
        dots = new TextView[4];
        dotsLayout.removeAllViews();

        for (int i=0; i<dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
//            dots[position].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            dots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);

            currentPosition = position;

            if (position == 0){
                getStartedButton.setVisibility(View.INVISIBLE);
            }
            else if (position == 1){
                getStartedButton.setVisibility(View.INVISIBLE);
            }
            else if (position == 2){
                getStartedButton.setVisibility(View.INVISIBLE);
            }
            else{
                animation = AnimationUtils.loadAnimation(OnBoardingActivity.this, R.anim.bottom_animation);
                getStartedButton.setAnimation(animation);
                getStartedButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
