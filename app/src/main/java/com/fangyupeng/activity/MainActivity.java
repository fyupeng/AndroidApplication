package com.fangyupeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import com.fangyupeng.R;
import com.fangyupeng.pojo.UserSession;
import com.fangyupeng.service.AddressService;
import com.fangyupeng.service.GlobalSessionService;


public class MainActivity extends Activity {

    private TextView myNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myNameView = findViewById(R.id.myName);
        ImageButton musicButton = findViewById(R.id.musicButton);
        ImageButton cameraButton = findViewById(R.id.cameraButton);
        ImageButton myButton = findViewById(R.id.myButton);

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MusicPlayerActivity.class);
                startActivity(intent);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PhotoSpotActivity.class);
                startActivity(intent);
            }
        });

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MyActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String mySessionIp = AddressService.getIP(MainActivity.this);
        UserSession userSession = (UserSession) GlobalSessionService.get(mySessionIp);
        if (userSession == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        myNameView.setText(userSession.getUsername());
    }
}