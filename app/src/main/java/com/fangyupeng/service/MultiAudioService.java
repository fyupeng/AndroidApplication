package com.fangyupeng.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Auther: fyp
 * @Date: 2022/5/20
 * @Description:
 * @Package: com.fyp.service
 * @Version: 1.0
 */
public class MultiAudioService extends Service {

   private static volatile int musicAction = -1;
   private static volatile int seekBarProgress = -1;

   private static boolean first = true;

   private String currentMusic;

   /**
    * 指定为 媒体播放器
    */
   private MediaPlayer mediaPlayer;

   /**
    * 指定一个 通道唯一 ID
    */
   private static String CHANNEL_ID = "1";


   public MultiAudioService() {
   }

   @Override
   public void onCreate() {
      super.onCreate();
      mediaPlayer = new MediaPlayer();
   }



   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      musicAction = intent.getIntExtra("music_action", 0);
      seekBarProgress = intent.getIntExtra("music_seek_bar_change_action", -1);
      seekBarProgress *= 1000;
      String musicPathAction = intent.getStringExtra("music_path_action");
      // 准备播放
      System.out.println("onStartCommand musicAction " + musicAction);
      System.out.println("onStartCommand seekBarProgress " + seekBarProgress);
      if (musicAction == 2) {
         if (null == mediaPlayer) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
               @Override
               public void onCompletion(MediaPlayer mediaPlayer) {
                  stopSelf();
               }
            });
         }
         System.out.println("musicPathAction" + musicPathAction);

         try {
            if (first) {
               mediaPlayer.setDataSource(getAssets().openFd(musicPathAction));
               mediaPlayer.prepare();
            } else {
               if (mediaPlayer != null && !mediaPlayer.isPlaying() && !currentMusic.equals(musicPathAction)) {
                  mediaPlayer.stop();
                  mediaPlayer = new MediaPlayer();
                  mediaPlayer.setDataSource(getAssets().openFd(musicPathAction));
                  mediaPlayer.prepare();
               }
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
         currentMusic = musicPathAction;
         mediaPlayer.start();
         //准备停止
      } else if (musicAction == 1) {
         if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
         }
      // 用户 拖动了 进度条
      } else if (musicAction == 4) {
         if (seekBarProgress != -1) {
            mediaPlayer.seekTo(seekBarProgress);
         }
      }
      // 发给 接收器，说我已经开启服务了
      Intent in = new Intent("audio_service_action_to_receive");
      // 转发 音乐 状态 给 接收器 2 - 已经播放了 1 - 已经停止了
      in.putExtra("music_action", musicAction);
      sendBroadcast(in);

      // 首次 打开 service 并且 得是 通过 开启播放，停止和拖动 进度条都不会 打开计时器
      if (first && musicAction == 2) {
         first = false;
         int duration = mediaPlayer.getDuration();
         in.putExtra("music_seek_bar_duration_action", duration);
         sendBroadcast(in);

         Timer timer = new Timer();
         timer.schedule(new TimerTask() {
            @Override
            public void run() {
               // 发给 接收器，说我已经开启服务了
               Intent in = new Intent("audio_service_action_to_receive");
               // 转发 音乐 状态 给 接收器 2 - 已经播放了 1 - 已经停止了
               int currentPosition = mediaPlayer.getCurrentPosition();
               System.out.println("run musicAction " + musicAction);
               if (musicAction == 4) {
                  int seekBarProgress = intent.getIntExtra("music_seek_bar_change_action", -1);
                  if (seekBarProgress != -1) {
                     System.out.println("seekBarProgress" + seekBarProgress);
                     seekBarProgress = -1;
                     //in.putExtra("music_seek_bar_currentPosition_action", currentPosition);
                  }
                  musicAction = 2;
               }
               in.putExtra("music_seek_bar_currentPosition_action", currentPosition);
               //System.out.println("currentPosition" + currentPosition);

               sendBroadcast(in);
            }
         }, 0, 10);
      }


      return super.onStartCommand(intent, flags, startId);
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
      mediaPlayer.stop();
      Intent in = new Intent("audio_service_action_to_receive");
      in.putExtra("music_action", 3);
      sendBroadcast(in);
   }

   @Override
   public IBinder onBind(Intent intent) {
      // TODO: Return the communication channel to the service.
      throw new UnsupportedOperationException("Not yet implemented");
   }
}
