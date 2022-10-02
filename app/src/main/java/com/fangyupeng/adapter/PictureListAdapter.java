package com.fangyupeng.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.fangyupeng.R;
import com.fangyupeng.activity.CaptureHistoryActivity;
import com.fangyupeng.pojo.PictureItem;
import com.fangyupeng.pojo.UserSession;
import com.fangyupeng.service.AddressService;
import com.fangyupeng.service.GlobalSessionService;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


/**
 * @Auther: fyp
 * @Date: 2022/5/27
 * @Description:
 * @Package: com.fangyupeng.adapter
 * @Version: 1.0
 */
public class PictureListAdapter extends ArrayAdapter<PictureItem> {

   private int resource;
   private Context context;
   private static final String TAG = "ListAdapter";

   private boolean removeOperateIsVisible = false;

   public PictureListAdapter(@NonNull Context context, int resource, @NonNull List<PictureItem> objects) {
      super(context, resource, objects);
      this.resource = resource;
      this.context = context;
   }


   @NonNull
   @Override
   public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      PictureItem pi = getItem(position);
      View view = LayoutInflater.from(getContext()).inflate(resource, parent, false);
      TextView imageIdView = view.findViewById(R.id.imageId);
      ImageView imageView = view.findViewById(R.id.image);
      TextView imageNameView = view.findViewById(R.id.imageName);
      TextView imageDescView = view.findViewById(R.id.imageDesc);
      TextView imageUploadTimeView = view.findViewById(R.id.imageUploadTime);
      ImageView moreOperateView = view.findViewById(R.id.moreOperate);
      ImageView removeOperate = view.findViewById(R.id.removeOperate);
      //thumb.setImageBitmap(BitMap.getInstance().returnBitMap(u.getThumb()));

      Item item = new PictureListAdapter.Item(imageView, imageNameView, imageDescView, imageDescView);
      item.setImageId(pi.getPictureId());
      item.setImageUrl(pi.getImageUrl());
      item.setImageName(pi.getPictureName());
      item.setImageDesc(pi.getPictureDescription());
      item.setImageUploadDate(pi.getUploadDate());

      imageIdView.setText(item.getImageId());
      imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.darkgray));
      Glide.with(context).load(item.imageUrl)
              .centerCrop()
              .into(item.imageView);
      imageNameView.setText(item.imageName);
      imageDescView.setText(item.imageDesc);
      imageUploadTimeView.setText(item.imageUploadDate);

      moreOperateView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Log.e("DEBUG", "moreOperateView is be click");
            if (!removeOperateIsVisible) {
               removeOperate.setVisibility(View.VISIBLE);
               removeOperateIsVisible = true;
            } else {
               removeOperate.setVisibility(View.INVISIBLE);
               removeOperateIsVisible = false;
            }
         }
      });

      removeOperate.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Log.e("DEBUG", "删除图片名: " + imageNameView.getText().toString() + " id: " + imageIdView.getText().toString());

            String pictureId = imageIdView.getText().toString().trim();

            String mySessionIp = AddressService.getIP(context);
            UserSession userSession = (UserSession) GlobalSessionService.get(mySessionIp);
            String userId = userSession.getUserId();
            String extraParams =
                    "userId=" + userId +
                    "&pictureId=" + pictureId;

            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    GlobalSessionService.serverPrefix + "/user/picture/removePicture?" +
                            extraParams,
                    res -> {
                       Log.e("DEBUG", res.toString());
                       Intent intent = new Intent();
                       intent.setClass(context, CaptureHistoryActivity.class);
                       context.startActivity(intent);
                    },
                    err -> {
                       Log.e("DEBUG", err.getMessage());
                    }
            );
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(postRequest);
         }
      });

      return view;
   }


   class Item {
      // 组件
      private ImageView imageView;
      private TextView imageNameView;
      private TextView imageDescView;
      private TextView imageUploadTimeView;
      // 组件值
      private String imageId;
      private String imageUrl;
      private String imageDesc;
      private String imageName;
      private String imageUploadDate;

      public Item(ImageView imageView, TextView imageNameView, TextView imageDescView, TextView imageUploadTimeView) {
         this.imageView = imageView;
         this.imageNameView = imageNameView;
         this.imageDescView = imageDescView;
         this.imageUploadTimeView = imageUploadTimeView;
      }

      public String getImageId() {
         return imageId;
      }

      public void setImageId(String imageId) {
         this.imageId = imageId;
      }

      public void setImageUrl(String imageUrl) {
         this.imageUrl = imageUrl;
      }

      public void setImageDesc(String imageDesc) {
         this.imageDesc = imageDesc;
      }

      public void setImageName(String imageName) {
         this.imageName = imageName;
      }

      public void setImageUploadDate(String imageUploadDate) {
         this.imageUploadDate = imageUploadDate;
      }
   }

}
