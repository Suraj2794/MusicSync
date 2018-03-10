package com.example.suraj.projectdemo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashAct extends AppCompatActivity implements Runnable,Animation.AnimationListener {

    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        iv=(ImageView)findViewById(R.id.splash_image);
        Handler handler = new Handler();
        handler.postDelayed(this, 500);

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        Animation am= AnimationUtils.loadAnimation(this, R.anim.splah_anim);
        am.setAnimationListener(this);
        iv.startAnimation(am);
        iv.setVisibility(View.VISIBLE);
	/*	finish();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);*/

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub
        finish();
        Intent intent = new Intent(this, StartAct.class);
        startActivity(intent);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }
}
