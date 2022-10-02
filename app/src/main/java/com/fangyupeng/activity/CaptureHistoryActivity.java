package com.fangyupeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fangyupeng.R;
import com.fangyupeng.adapter.PictureListAdapter;
import com.fangyupeng.pojo.PictureItem;
import com.fangyupeng.pojo.UserSession;
import com.fangyupeng.service.AddressService;
import com.fangyupeng.service.GlobalSessionService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CaptureHistoryActivity extends Activity {

    private Integer page = 1;
    private Integer pageSize = 5;
    private Integer records = 0;

    private TextView totalHistory;

    private final static int ITEM0_MALE_0 = Menu.FIRST;
    private final static int ITEM0_MALE_1 = Menu.FIRST + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_history);

        ImageView comeBack = findViewById(R.id.comeBack);
        TextView getMorePicture = findViewById(R.id.getMorePicture);
        totalHistory = findViewById(R.id.totalHistory);
        ListView pictureList = findViewById(R.id.pictureList);

        comeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(CaptureHistoryActivity.this, MyActivity.class);
                startActivity(intent);
            }
        });

        pictureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        getMorePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageSize += 5;
                getPictureToAdapter(page,pageSize);
            }
        });
        getPictureToAdapter(page,pageSize);
    }

    private void getPictureToAdapter(Integer page, Integer pageSize) {
        String mySessionIp = AddressService.getIP(getApplicationContext());
        UserSession userSession = (UserSession) GlobalSessionService.get(mySessionIp);

        String userId = userSession.getUserId();
        String extraParams =
                "userId=" + userId +
                "&page=" + page +
                "&pageSize=" + pageSize;

        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                GlobalSessionService.serverPrefix + "/user/picture/getAllPictures?" +
                        extraParams,
                res -> {
                    Log.e("DEBUG", res.toString());

                    JSONObject jsonObject = null;
                    List<PictureItem> list = new ArrayList<>();
                    try {
                        jsonObject = new JSONObject(res);
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray resultList = data.getJSONArray("rows");
                        Integer dataRecords = data.getInt("records");
                        System.out.println(dataRecords);

                        records = dataRecords;
                        totalHistory.setText("共有"+ records +"条记录");

                        for (int i = 0; i <resultList.length(); i++) {
                            JSONObject elementJsonObject = resultList.getJSONObject(i);
                            String pictureId = elementJsonObject.getString("id");
                            String picturePath = elementJsonObject.getString("picturePath");
                            String pictureDesc = elementJsonObject.getString("pictureDesc");
                            String uploadDateTime = elementJsonObject.getString("uploadTime");

                            String pictureName = picturePath.substring(picturePath.indexOf("picture") + 8,picturePath.indexOf("."));

                            String uploadDate = uploadDateTime.substring(0, uploadDateTime.indexOf("T"));
                            String uploadTime = uploadDateTime.substring(uploadDateTime.indexOf("T") + 1, uploadDateTime.indexOf("."));

                            PictureItem item = new PictureItem();
                            item.setPictureId(pictureId);
                            item.setImageUrl(GlobalSessionService.serverPrefix + picturePath);
                            item.setPictureName(pictureName);
                            item.setPictureDescription(pictureDesc);
                            item.setUploadDate(uploadDate);
                            item.setUploadTime(uploadTime);
                            list.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ListView listView = findViewById(R.id.pictureList);
                    PictureListAdapter pictureListAdapter = new PictureListAdapter(CaptureHistoryActivity.this, R.layout.picture_list_item, list);
                    listView.setAdapter(pictureListAdapter);
                },
                err -> {
                    Log.e("DEBUG", err.getMessage());
                }
        );
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(postRequest);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String mySessionIp = AddressService.getIP(CaptureHistoryActivity.this);
        Object sessionVal = GlobalSessionService.get(mySessionIp);
        if (sessionVal == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(CaptureHistoryActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }
    }




}