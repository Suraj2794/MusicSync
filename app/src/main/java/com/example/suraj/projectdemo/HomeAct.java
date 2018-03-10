package com.example.suraj.projectdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class HomeAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //! Set up grid view.
        GridView gridView = findViewById(R.id.grdViewHistory);
        gridView.setAdapter(new ImageAdapter(this));

       /* gridView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            Toast.makeText(this, "Welcome to Station: " + position, Toast.LENGTH_SHORT).show();
        });*/
       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               Toast.makeText(HomeAct.this, "Welcome to Station: " + position, Toast.LENGTH_SHORT).show();
           }
       });
    }

    public void joinFbClickAction(View view) {
        Intent intent = new Intent(getApplicationContext(), JoinAct.class);
        startActivity(intent);
    }

    public void createFbClickAction(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateGroup.class);
        startActivity(intent);
    }
}
