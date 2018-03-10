package com.example.suraj.projectdemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;

public class SignUpAct extends AppCompatActivity {

    AutoCompleteTextView fname,lname,mno,email,age;
    Button submit;
    RadioGroup rg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        fname=(AutoCompleteTextView)findViewById(R.id.firstName);
        lname=(AutoCompleteTextView)findViewById(R.id.lastName);
        mno=(AutoCompleteTextView)findViewById(R.id.number);
        email=(AutoCompleteTextView)findViewById(R.id.email);
        age=(AutoCompleteTextView)findViewById(R.id.age);
        rg=(RadioGroup)findViewById(R.id.gender);
        submit=(Button)findViewById(R.id.submitSignup);

        Typeface tf=Typeface.createFromAsset(getApplicationContext().getAssets(), "georgia.ttf");
        fname.setTypeface(tf);
        lname.setTypeface(tf);
        mno.setTypeface(tf);
        email.setTypeface(tf);
        age.setTypeface(tf);
        submit.setTypeface(tf);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUpAct.this,HomeAct.class);
                startActivity(intent);
            }
        });
    }
}
