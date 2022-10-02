package com.fangyupeng.activity;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fangyupeng.R;
import com.fangyupeng.service.AddressService;
import com.fangyupeng.service.GlobalSessionService;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class UpdatePwdActivity extends Activity {

    private static boolean eye1IsOpen = false;
    private static boolean eye2IsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);

        System.out.println(getResources().getAssets());
        System.out.println(getResources().getDrawable(R.drawable.closeyes));

        ImageView comeBack = findViewById(R.id.comeBack);
        EditText eUsername = findViewById(R.id.updateUsername);
        EditText eOldPwd = findViewById(R.id.oldPwd);
        EditText eNewPwd = findViewById(R.id.newPwd);
        Button certainUpdate = findViewById(R.id.certainUpdate);
        ImageView eyes1 = findViewById(R.id.eyes1);
        ImageView eyes2 = findViewById(R.id.eyes2);

        eyes1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!eye1IsOpen) {
                    eye1IsOpen = true;
                    eyes1.setImageResource(R.drawable.openeyes);
                    eOldPwd.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    eye1IsOpen = false;
                    eyes1.setImageResource(R.drawable.closeyes);
                    eOldPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        eyes2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!eye2IsOpen) {
                    eye2IsOpen = true;
                    eyes2.setImageResource(R.drawable.openeyes);
                    eNewPwd.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    eye2IsOpen = false;
                    eyes2.setImageResource(R.drawable.closeyes);
                    eNewPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        comeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UpdatePwdActivity.this, MyActivity.class);
                startActivity(intent);
            }
        });

        certainUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = eUsername.getText().toString().trim();
                String oldPwd = eOldPwd.getText().toString().trim();
                String newPwd = eNewPwd.getText().toString().trim();
                if ("".equals(username) || "".equals(oldPwd) || "".equals(newPwd)) {
                    Toast.makeText(UpdatePwdActivity.this, "输入有误，请重新输入!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject params = new JSONObject();
                        params.put("username", username);
                        params.put("oldPassword", oldPwd);
                        params.put("newPassword", newPwd);

                        JsonObjectRequest request = new JsonObjectRequest(
                                    "http://47.107.63.171:8083/user/updatePassword",
                                    params,
                                    res -> {
                                        System.out.println(res);
                                        try {
                                            if ("200".equals(res.get("status"))) {
                                                eUsername.setText("");
                                                eOldPwd.setText("");
                                                eNewPwd.setText("");
                                                Toast.makeText(UpdatePwdActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
                                                String mySessionId = AddressService.getIP(UpdatePwdActivity.this);
                                                GlobalSessionService.remove(mySessionId);
                                                Intent intent = new Intent();
                                                intent.setClass(UpdatePwdActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(UpdatePwdActivity.this, res.get("msg").toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    err -> {
                                        Log.e("DEBUG", err.getMessage());
                                        Toast.makeText(UpdatePwdActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                        );
                        RequestQueue queue = Volley.newRequestQueue(UpdatePwdActivity.this);
                        queue.add(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


    }
}