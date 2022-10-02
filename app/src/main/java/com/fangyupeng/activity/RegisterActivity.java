package com.fangyupeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fangyupeng.R;
import com.fangyupeng.service.GlobalSessionService;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends Activity {

    private boolean userNameIsPass = false;
    private boolean passwordIsPass = false;

    private String userName;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText registerUserName = findViewById(R.id.registerUserName);
        EditText registerPassword1 = findViewById(R.id.registerPassword1);
        EditText registerPassword2 = findViewById(R.id.registerPassword2);

        registerUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Editable text = ((EditText) view).getText();
                String value = text.toString();
                System.out.println(value);
                String regEx = "^[A-Za-z0-9@.]{6,18}$";
                boolean isPass = value.matches(regEx);
                if(hasFocus) {
                } else {
                    if (!isPass) {
                        System.out.println("2222");
                        userNameIsPass = false;
                        Toast.makeText(RegisterActivity.this, "用户名只允许带@.的6-18数字字母", Toast.LENGTH_SHORT).show();
                    } else {
                        userNameIsPass = true;
                        userName = value;
                    }
                }
            }
        });

        registerPassword1.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Editable text = ((EditText) view).getText();
                String value = text.toString();
                System.out.println(value);
                String  regEx  =  "^[A-Za-z0-9_]{6,18}$";
                boolean isPass = value.matches(regEx);
                if(hasFocus) {
                } else {
                    if (!isPass) {
                        System.out.println("11111");
                        passwordIsPass = false;
                        Toast.makeText(RegisterActivity.this, "密码只允许带下划线的6-18位数字字母组合", Toast.LENGTH_SHORT).show();
                    } else {
                        passwordIsPass = true;
                        password = value;
                    }
                }
            }
        });

        registerPassword2.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Editable text = ((EditText) view).getText();
                String value = text.toString();
                System.out.println(value);
                if(hasFocus) {
                } else {
                    EditText ePassword = findViewById(R.id.registerPassword1);
                    if (!ePassword.getText().toString().equals(value)) {
                        System.out.println("3333333333");
                        passwordIsPass = false;
                        Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    } else {
                        passwordIsPass = true;
                    }
                }
            }
        });

        Button registerButton = findViewById(R.id.register);
        Button goLoginButton = findViewById(R.id.goLogin);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userNameIsPass || !passwordIsPass) {
                    Toast.makeText(RegisterActivity.this, "未通过校验，请重试！", Toast.LENGTH_SHORT).show();
                } else {
                    EditText eUserName = findViewById(R.id.registerUserName);
                    EditText ePassword1 = findViewById(R.id.registerPassword1);
                    EditText ePassword2 = findViewById(R.id.registerPassword2);

                    eUserName.setText("");
                    ePassword1.setText("");
                    ePassword2.setText("");

                    JSONObject jsonParams = new JSONObject();
                    try {
                        jsonParams.put("username", userName);
                        jsonParams.put("password", password);

                        JsonObjectRequest request = new JsonObjectRequest(
                                GlobalSessionService.serverPrefix + "/user/regist",
                                jsonParams,
                                response ->{
                                    Object status = null;
                                    Object msg = null;
                                    try {
                                        status = response.get("status");
                                        msg = response.get("msg");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if ("200".equals(status.toString())) {
                                        Toast.makeText(RegisterActivity.this, "注册成功！\n用户：" + userName, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setClass(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(RegisterActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                },
                                err -> {
                                    Toast.makeText(RegisterActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        );
                        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                        queue.add(request);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        goLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}