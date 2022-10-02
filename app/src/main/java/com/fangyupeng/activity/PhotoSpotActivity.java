package com.fangyupeng.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpCall;
import com.fangyupeng.R;
import com.fangyupeng.pojo.SpotResult;
import com.fangyupeng.pojo.UserSession;
import com.fangyupeng.service.AddressService;
import com.fangyupeng.service.GlobalSessionService;
import com.fangyupeng.thread.RequestHandlerThread;
import com.fangyupeng.utils.BitmapUtil;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PhotoSpotActivity extends Activity {

    private Thread newThread;

    private final static int REQUEST_IMAGE_CAPTURE = 1;
    private final static int REQUEST_TAKE_PHOTO = 1;

    private String defaultImgBaikeUrl1 = "https://baike.baidu.com/item/%E5%A2%A8%E5%85%B0/770508";
    private String defaultImgBaikeUrl2 = "http://baike.baidu.com/item/%E5%BB%BA%E5%85%B0/1068570";
    private String defaultImgBaikeUrl3 = "http://baike.baidu.com/item/%E5%A2%A8%E5%85%B0/770508";

    // 识别的图片
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    // 识别的名字
    private TextView name1;
    private TextView name2;
    private TextView name3;

    // 识别的描述
    private TextView description1;
    private TextView description2;
    private TextView description3;

    // 识别度 0 ~ 1 的 进度条
    private ProgressBar score1;
    private ProgressBar score2;
    private ProgressBar score3;

    private List<SpotResult> spotResultList = new ArrayList<>();

    private volatile static Boolean isOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_spot);

        ImageButton captureButton = findViewById(R.id.capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        imageView1 = findViewById(R.id.image1);
        imageView2 = findViewById(R.id.image2);
        imageView3 = findViewById(R.id.image3);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(defaultImgBaikeUrl1));
                startActivity(intent);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(defaultImgBaikeUrl2));
                startActivity(intent);
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(defaultImgBaikeUrl3));
                startActivity(intent);
            }
        });

        name1 = findViewById(R.id.name1);
        name2 = findViewById(R.id.name2);
        name3 = findViewById(R.id.name3);

        description1 = findViewById(R.id.description1);
        description2 = findViewById(R.id.description2);
        description3 = findViewById(R.id.description3);

        score1 = findViewById(R.id.score1);
        score2 = findViewById(R.id.score2);
        score3 = findViewById(R.id.score3);

        SpotResult spotResult1 = new SpotResult();
        SpotResult spotResult2 = new SpotResult();
        SpotResult spotResult3 = new SpotResult();

        spotResult1.setName(name1);
        spotResult1.setScore(score1);
        spotResult1.setImage(imageView1);
        spotResult1.setDescription(description1);

        spotResult2.setName(name2);
        spotResult2.setScore(score2);
        spotResult2.setImage(imageView2);
        spotResult2.setDescription(description2);

        spotResult3.setName(name3);
        spotResult3.setScore(score3);
        spotResult3.setImage(imageView3);
        spotResult3.setDescription(description3);

        spotResultList.add(spotResult1);
        spotResultList.add(spotResult2);
        spotResultList.add(spotResult3);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView uploadPhoto = findViewById(R.id.uploadPhoto);
            uploadPhoto.setImageBitmap(imageBitmap);

            if (null == imageBitmap) {
                Log.i("debug", "savePicture: ------------------图片为空------");
                return;
            }
            /**
             *
             *     将图片添加到图库
             *
             * String fileName = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
             *
             *             FileOutputStream fos = null;
             *             File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
             *             File myCaptureFile = new File(storageDir, fileName);
             *             //String fileName = storageDir + "/" + name;
             *             try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile))) {
             *                 if (!myCaptureFile.exists()) {
             *                     myCaptureFile.createNewFile();
             *                 }
             *                 imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 把数据写入文件
             *
             *
             *                  galleryAddPic(myCaptureFile, fileName);
             *
             *              } catch(IOException e){
             *                  e.printStackTrace();
             *              }
             */

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new RequestHandlerThread(imageBitmap, spotResultList, getApplicationContext()));



        }
    }

    private void galleryAddPic(File myCaptureFile, String fileName) {
        // 把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    myCaptureFile.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + myCaptureFile.getPath())));
    }

    @Override
    protected void onStart() {
        super.onStart();
        String mySessionIp = AddressService.getIP(getApplicationContext());
        Object sessionVal = GlobalSessionService.get(mySessionIp);
        if (sessionVal == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            return;
        }
    }


}