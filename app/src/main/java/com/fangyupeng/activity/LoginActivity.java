package com.fangyupeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fangyupeng.R;
import com.fangyupeng.pojo.UserSession;
import com.fangyupeng.service.AddressService;
import com.fangyupeng.service.GlobalSessionService;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("移动开发");

        Button loginButton = findViewById(R.id.login);
        Button goRegisterButton = findViewById(R.id.goRegister);
        TextView goUpdatePwd = findViewById(R.id.goUpdatePwd);
        TextView goWebSide = findViewById(R.id.goWebSide);
        TextView goEmail = findViewById(R.id.goEmail);
        EditText eUsername = findViewById(R.id.loginUserName);
        EditText ePassword = findViewById(R.id.loginPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = eUsername.getText().toString().trim();
                String password = ePassword.getText().toString().trim();

                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("username", username);
                    jsonParams.put("password", password);

                    JsonObjectRequest request = new JsonObjectRequest(
                            GlobalSessionService.serverPrefix + "/user/login",
                            jsonParams,
                            response ->{
                                Object status = null;
                                Object msg = null;
                                JSONObject data = null;
                                try {
                                    Log.e("DEBUG", response.toString());
                                    status = response.get("status");
                                    msg = response.get("msg");
                                    if ("200".equals(status.toString())) {
                                        data = (JSONObject) response.get("data");
                                        // key
                                        String mySessionIp = AddressService.getIP(LoginActivity.this);
                                        // val
                                        UserSession userSession = new UserSession();
                                        userSession.setUserId(data.getString("id"));
                                        userSession.setUserToken(data.getString("userToken"));
                                        userSession.setUsername(username);
                                        // key - val -set
                                        GlobalSessionService.set(mySessionIp, userSession);

                                        Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setClass(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(LoginActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            err -> {
                                Toast.makeText(LoginActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    );
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(request);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        goRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        goUpdatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, UpdatePwdActivity.class);
                startActivity(intent);
            }
        });

        goWebSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(GlobalSessionService.webSitePrefix + "/home"));
                startActivity(intent);
            }
        });

        goEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_SEND_MULTIPLE);
                it.putExtra(Intent.EXTRA_EMAIL, new String[] {GlobalSessionService.developEmail});
                it.putExtra(Intent.EXTRA_SUBJECT, "Write your feedback briefly");
                it.putExtra(Intent.EXTRA_TEXT, "Write down the email you would like feedback to the developer");
                //it.setType("text/plain");
                it.setType("message/rfc822");
                startActivity(it);
            }
        });


    }
}