package com.example.tejas.track;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openUser(View view){
        Intent userAuthIntent = new Intent(MainActivity.this, UserAuthActivity.class);
        startActivity(userAuthIntent);
    }

    public void openClient(View view){
        Intent clientIntent = new Intent(MainActivity.this, ClientActivity.class);
        startActivity(clientIntent);
    }
}
