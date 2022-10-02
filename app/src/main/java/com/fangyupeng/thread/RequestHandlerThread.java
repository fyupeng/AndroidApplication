package com.fangyupeng.thread;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.fangyupeng.R;
import com.fangyupeng.pojo.SpotResult;
import com.fangyupeng.pojo.UserSession;
import com.fangyupeng.service.AddressService;
import com.fangyupeng.service.GlobalSessionService;
import com.fangyupeng.utils.BitmapUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @Auther: fyp
 * @Date: 2022/5/28
 * @Description:
 * @Package: com.fangyupeng.thread
 * @Version: 1.0
 */
public class RequestHandlerThread implements Runnable {

    private String base64Img;
    private Bitmap imageBitmap;
    private List<SpotResult> spotResultList;
    private Context context;

    public RequestHandlerThread(Bitmap imageBitmap, List<SpotResult> spotResultList, Context context) {
        this.base64Img = base64Img;
        this.spotResultList = spotResultList;
        this.context = context;
        this.imageBitmap = imageBitmap;
        // 将 Bitmap 转成 Base64
        base64Img = BitmapUtil.bitmapToBase64(imageBitmap);
    }

    @Override
    public void run() {
        String baidu_server = "https://aip.baidubce.com/oauth/2.0/token?";
        String grant_type = "client_credentials";
        String client_id = "4wCZa9Vp3GUztFjG3VtYsjEY";
        String client_secret = "iSMYjUURnZM0eyn4w6SSinXMV5Y0PrU7";
        String getUrl = baidu_server +
                "grant_type=" + grant_type +
                "&client_id=" + client_id +
                "&client_secret=" + client_secret;

        StringRequest getRequest = new StringRequest(
                Request.Method.GET,
                getUrl,
                response -> {
                    try {
                        JSONObject res = new JSONObject(response);
                        String access_token = res.getString("access_token");
                        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                        RequestParams requestParams = new RequestParams();
                        requestParams.add("image", base64Img);
                        requestParams.add("baike_num", "3");
                        asyncHttpClient.post("https://aip.baidubce.com/rest/2.0/image-classify/v1/plant?access_token=" + access_token, requestParams, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    String response = new String(responseBody); // this is your response string
                                    Log.e("DEBUG", response);
                                    JSONObject jsonResponse = new JSONObject(response);

                                    JSONArray result = jsonResponse.getJSONArray("result");
                                    for (int index = 0; index < result.length(); index++) {
                                        JSONObject element = (JSONObject) result.get(index);

                                        SpotResult spotResult = null;
                                        try {
                                            spotResult = spotResultList.get(index);
                                            String name = element.getString("name");
                                            double score = element.getDouble("score");

                                            if ("非植物".equals(name)) {
                                                for (SpotResult srs : spotResultList) {
                                                    srs.getImage().setBackgroundColor(ContextCompat.getColor(context, R.color.darkgray));
                                                    srs.getImage().setImageResource(R.drawable.flower);
                                                    srs.getName().setText("非植物");
                                                    srs.getScore().setProgress(0);
                                                    srs.getDescription().setText("");
                                                }
                                                return;
                                            }
                                            Log.e("DEBUG", name);
                                            Log.e("DEBUG", String.valueOf(score));

                                            JSONObject baikeInfo = (JSONObject) element.get("baike_info");

                                            Log.e("DEBUG", baikeInfo.toString());

                                            String baikeUrl = baikeInfo.getString("baike_url");
                                            String imageUrl = baikeInfo.getString("image_url");
                                            String description = baikeInfo.getString("description");


                                            spotResult.getName().setText(name);
                                            spotResult.getScore().setProgress((int) (score * 100));
                                            spotResult.getDescription().setText(description);
                                            Glide.with(context).load(imageUrl)
                                                    .centerCrop()
                                                    .into(spotResult.getImage());

                                            spotResult.getImage().setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                 public void onClick(View v) {
                                                    Intent intent = new Intent();
                                                     intent.setData(Uri.parse(baikeUrl));
                                                     context.startActivity(intent);
                                                 }
                                             });


                                        } catch (JSONException e) {
                                            // 主要是对于识别为空做处理，清除之前默认图片链接跳转
                                            e.printStackTrace();
                                            //spotResult.getImage().setBackgroundColor(getResources().getColor(R.color.darkgray));
                                            spotResult.getImage().setBackgroundColor(ContextCompat.getColor(context, R.color.darkgray));
                                            spotResult.getImage().setImageResource(R.drawable.loadimageerr);
                                            spotResult.getName().setText("");
                                            spotResult.getScore().setProgress(0);
                                            spotResult.getDescription().setText("");
                                            // 跳过本次循环，继续下次循环
                                            continue;
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                saveImage();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                // Here you write code if there's error
                                Log.e("DEBUG", error.getMessage());
                                for (SpotResult srs : spotResultList) {
                                    srs.getImage().setBackgroundColor(ContextCompat.getColor(context, R.color.darkgray));
                                    srs.getImage().setImageResource(R.drawable.flower);
                                    srs.getName().setText("未知错误");
                                    srs.getScore().setProgress(0);
                                    srs.getDescription().setText("");
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("DEBUG", error.getMessage());
                }
        );
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(getRequest);
    }

    private void saveImage() {

        // 获取 userId
        String mySessionIp = AddressService.getIP(context);
        UserSession userSession = (UserSession) GlobalSessionService.get(mySessionIp);
        // 获取 识别度最高的图片描述
        int score = 0;
        int index = 0;
        for (int i = 0; i < spotResultList.size(); i++) {
            if ("非植物".equals(spotResultList.get(i).getName().getText().toString().trim()) ||
                    "未知错误".equals(spotResultList.get(i).getName().getText().toString().trim())) {
                Log.e("DEBUG", "非植物，停止存储图片到数据库!");
                break;
            }
            ProgressBar progressBar = spotResultList.get(i).getScore();
            int elementScore = progressBar.getProgress();
            score = score > elementScore ? score : elementScore;
            index = score > elementScore ? index : i;
        }
        String pictureName = spotResultList.get(index).getName().getText().toString().trim();
        String pictureDesc = spotResultList.get(index).getDescription().getText().toString().trim();
        // 存储图片
        if (score != 0) {
            Log.e("DEBUG", "正在存储图片到服务器中...");
            Log.e("DUBUG", String.valueOf("最高识别度：" + score));
            File outputDir = null;
            File outputFile = null;
            try {
                outputDir = context.getCacheDir(); // context being the Activity pointer
                outputFile = File.createTempFile("tempImage", ".jpg", outputDir);
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 把数据写入文件


                    MediaType mediaType = MediaType.parse("application/octet-stream");
                    RequestBody requestBody = RequestBody.create(mediaType, outputFile);
                    RequestBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("userId", userSession.getUserId())
                            .addFormDataPart("pictureDesc", pictureDesc)
                            .addFormDataPart("file", pictureName + ".jpg", requestBody)
                            .build();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(GlobalSessionService.serverPrefix + "/user/picture/upload").post(multipartBody).build();

                    OkHttpClient okHttpClient = new OkHttpClient();

                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("onFailure :", e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.e("DEBUG", response.toString());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
