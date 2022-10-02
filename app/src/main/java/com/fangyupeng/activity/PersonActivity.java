package com.fangyupeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.fangyupeng.R;
import com.fangyupeng.pojo.UserSession;
import com.fangyupeng.service.AddressService;
import com.fangyupeng.service.GlobalSessionService;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PersonActivity extends Activity {

    private final static int ITEM0_MALE_0 = Menu.FIRST;
    private final static int ITEM0_MALE_1 = Menu.FIRST + 1;

    private ImageView comeBack;
    private TextView tUserid;
    private ImageView iAvatar;
    private EditText eNickname;
    private EditText eSex;
    private EditText eEmail;
    private EditText eTelephone;
    private EditText eDescription;

    private List<EditText> editTextList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        comeBack = findViewById(R.id.comeBack);
        tUserid = findViewById(R.id.userid);
        iAvatar = findViewById(R.id.avatar);
        eNickname = findViewById(R.id.nickname);
        eSex = findViewById(R.id.sex);
        eEmail = findViewById(R.id.email);
        eTelephone = findViewById(R.id.telephone);
        eDescription = findViewById(R.id.description);

        editTextList.add(eNickname);
        editTextList.add(eSex);
        editTextList.add(eEmail);
        editTextList.add(eTelephone);
        editTextList.add(eDescription);

        comeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(PersonActivity.this, MyActivity.class);
                startActivity(intent);
            }
        });

        String mySessionIp = AddressService.getIP(this);
        UserSession userSession = (UserSession) GlobalSessionService.get(mySessionIp);
        String userId = userSession.getUserId();

        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                GlobalSessionService.serverPrefix + "/user/query?userId=" + userId,
                response -> {
                        Log.e("DEBUG", response);
                        try {
                            JSONObject res = new JSONObject(response);
                            if ("200".equals(res.getString("status"))) {
                                Toast.makeText(PersonActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                                JSONObject data = (JSONObject) res.get("data");
                                Glide.with(PersonActivity.this).load(GlobalSessionService.serverPrefix + data.getString("avatar")).into(iAvatar);
                                tUserid.setText(data.getString("userId"));
                                eNickname.setText(data.getString("nickname"));
                                eTelephone.setText(data.getString("tel"));
                                String sex = data.getString("sex");
                                eSex.setText("0".equals(sex) ? "女" : "男");
                                eEmail.setText(data.getString("email"));
                                eDescription.setText(data.getString("description"));
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
        RequestQueue queue = Volley.newRequestQueue(PersonActivity.this);
        queue.add(postRequest);


        for (EditText eElement : editTextList) {
            eElement.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    JSONObject params = new JSONObject();
                    completeParams(params);
                    updateInfo(params);
                }
            });
        }
        this.registerForContextMenu(eSex);
    }

    public void setReadOnly(View v){
        // 设置KeyListener为null, 变为不可输入状态
        eSex.setKeyListener(null);
        // 如果需要,设置文字可选
        eSex.setTextIsSelectable(true);
    }

    private void completeParams(JSONObject params) {
        try {
            String userId = tUserid.getText().toString().trim();
            String nickname = eNickname.getText().toString().trim();
            String telPhone = eTelephone.getText().toString().trim();
            String sex = eSex.getText().toString().trim();
            String email = eEmail.getText().toString().trim();
            String description = eDescription.getText().toString().trim();

            params.put("userId",userId);
            params.put("nickname",nickname);
            params.put("tel",telPhone);
            params.put("sex","女".equals(sex) ? "0" : "1");
            params.put("email",email);
            params.put("description",description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String mySessionIp = AddressService.getIP(PersonActivity.this);
        Object sessionVal = GlobalSessionService.get(mySessionIp);
        if (sessionVal == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(PersonActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }
    }

    private void updateInfo(JSONObject params) {
        JsonObjectRequest request = new JsonObjectRequest(
                GlobalSessionService.serverPrefix + "/user/completeUserInfo",
                params,
                res -> {
                    System.out.println(res);
                    Log.e("DEBUG", res.toString());
                },
                err -> {
                    Log.e("DEBUG", err.toString());
                    System.out.println(err);
                }
        );
        RequestQueue queue = Volley.newRequestQueue(PersonActivity.this);
        queue.add(request);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(01, ITEM0_MALE_0, 0, "女");
        menu.add(02, ITEM0_MALE_1, 1, "男");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case ITEM0_MALE_0:
                eSex.setText("女");
                break;
            case ITEM0_MALE_1:
                eSex.setText("男");
                break;
        }
        return true;
    }

}