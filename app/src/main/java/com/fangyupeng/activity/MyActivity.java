package com.fangyupeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.fangyupeng.R;
import com.fangyupeng.pojo.UserSession;
import com.fangyupeng.service.AddressService;
import com.fangyupeng.service.GlobalSessionService;
import org.json.JSONException;
import org.json.JSONObject;

public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        LinearLayout personIntro =  findViewById(R.id.personIntro);
        LinearLayout captureHistory =  findViewById(R.id.captureHistory);
        LinearLayout updatePwd =  findViewById(R.id.updatePwd);
        LinearLayout logout =  findViewById(R.id.logout);
        ImageView comeBack = findViewById(R.id.comeBack);
        LinearLayout goLogin = findViewById(R.id.goLogin);

        ImageView iAvatar = findViewById(R.id.avatar);
        TextView tNickname = findViewById(R.id.nickname);
        TextView tDesc = findViewById(R.id.desc);

        String mySessionIp = AddressService.getIP(MyActivity.this);
        Object userSession = GlobalSessionService.get(mySessionIp);
        String userId = ((UserSession) userSession).getUserId();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                GlobalSessionService.serverPrefix + "/user/query?userId=" + userId,
                response -> {
                        Log.e("DEBUG", response);
                        try {
                            JSONObject res = new JSONObject(response);
                            if ("200".equals(res.getString("status"))) {
                                //Toast.makeText(MyActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                                JSONObject data = (JSONObject) res.get("data");
                                Glide.with(MyActivity.this).load(GlobalSessionService.serverPrefix + data.getString("avatar")).into(iAvatar);
                                tNickname.setText(data.getString("nickname"));
                                tDesc.setText(data.getString("description"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                },
                err -> {
                        Log.e("DEBUG", err.getMessage());
                    //Log.e("DEBUG", err.getMessage());
                }
        );
        RequestQueue queue = Volley.newRequestQueue(MyActivity.this);
        queue.add(request);


        comeBack.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
         Intent intent = new Intent();
         intent.setClass(MyActivity.this, MainActivity.class);
         startActivity(intent);
        }
        });

        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MyActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });



        updatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MyActivity.this, UpdatePwdActivity.class);
                startActivity(intent);
            }
        });

        personIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MyActivity.this, PersonActivity.class);
                startActivity(intent);
            }
        });

        captureHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MyActivity.this, CaptureHistoryActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mySessionIp = AddressService.getIP(MyActivity.this);
                GlobalSessionService.remove(mySessionIp);
                Toast.makeText(MyActivity.this, "注销成功！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(MyActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String mySessionIp = AddressService.getIP(MyActivity.this);
        Object sessionVal = GlobalSessionService.get(mySessionIp);
        if (sessionVal == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(MyActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }
    }
}