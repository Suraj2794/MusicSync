package com.example.suraj.projectdemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartAct extends AppCompatActivity {

    TextView intro;
    Button login,signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();
        Typeface tf=Typeface.createFromAsset(getApplicationContext().getAssets(), "georgia.ttf");
        login=(Button)findViewById(R.id.login);
        signup=(Button)findViewById(R.id.signup);
        intro=(TextView)findViewById(R.id.intro);
        login.setTypeface(tf);
        signup.setTypeface(tf);
        intro.setTypeface(tf);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginAct.class);
                startActivity(intent);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpAct.class);
                startActivity(intent);
            }
        });

    }
}
