package com.example.suraj.projectdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartAct extends AppCompatActivity {

    TextView intro;
    Button login,signup;
    public static boolean filePermission,hotspotPermission,permissionGranted ;
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
        permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        hotspotPermission=ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        filePermission= ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(permissionGranted)
        {

        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 212);
            permissionGranted=ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        if(filePermission)
        {

        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);

            filePermission=ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        }
        /*if(!hotspotPermission)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_SETTINGS}, 121);

            hotspotPermission=ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" +
                         getPackageName()));
                startActivityForResult(intent, 121);
              }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // {Some Code}
                    filePermission=ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                }
                else
                {
                    //finish();
                }
                break;
            }
            case 121:
            {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // {Some Code}
                    //ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
                    //hotspotPermission=ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;

                }
                else
                {
                    //finish();
                }
                break;
            }
            case 212:
            {

                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // {Some Code}
                    permissionGranted=ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                }
                else
                {
                    //finish();
                }
            }
        }
    }
}
