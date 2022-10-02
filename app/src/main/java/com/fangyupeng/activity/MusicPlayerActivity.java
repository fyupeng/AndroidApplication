package com.fangyupeng.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fangyupeng.R;
import com.fangyupeng.receive.AudioBroadCastReceive;
import com.fangyupeng.service.AddressService;
import com.fangyupeng.service.GlobalSessionService;
import com.fangyupeng.service.MultiAudioService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerActivity extends Activity {

    /**
     * 1 - 要播放
     * 2 - 要停止
     */
    private static int AUDIO_STATE = 1;
    private BroadcastReceiver broadcastReceiver;

    private SeekBar seekBar;

    private String username;


    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Button openMusic = findViewById(R.id.openMusic);
        Button closeMusic = findViewById(R.id.closeMusic);

        ImageView comeBack = findViewById(R.id.comeBack);
        TextView currentTimeTextView = findViewById(R.id.currentTime);
        TextView maxTimeTextView = findViewById(R.id.maxTime);
        ListView musicListview = findViewById(R.id.musicListView);
        TextView currentMusic  = findViewById(R.id.currentMusic);
        
        comeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        AssetManager assets = getAssets();
        String[] musics = new String[0];
        List<String> musicNameList = new ArrayList<>();
        try {
            musics = assets.list("");
            for (String music : musics) {
                if (music.endsWith(".mp3")) {
                    musicNameList.add(music);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = new ArrayAdapter(this,
                R.layout.music_list_item,
                musicNameList);
        musicListview.setAdapter(adapter);

        musicListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String musicName = musicNameList.get(position);
                currentMusic.setText(musicName);
            }
        });

        // 音乐播放
        openMusic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // AUDIO_STATE == 2 表示 音乐未 已经在 播放中
                if (AUDIO_STATE == 2) {
                    return;
                }

                AUDIO_STATE = 2;

                Intent intent = new Intent(MusicPlayerActivity.this, MultiAudioService.class);
                /**
                 * 传 1 表示 service 要去 播放音乐
                 * 传 2 表示 service 要去 停止音乐
                 */
                intent.putExtra("music_action", AUDIO_STATE);
                intent.putExtra("music_path_action", currentMusic.getText().toString());
                startService(intent);
            }
        });

        // 音乐停止
        closeMusic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // AUDIO_STATE == 1 表示 音乐未 开始播放
                if (AUDIO_STATE == 1) {
                    return;
                }

                AUDIO_STATE = 1;

                Intent intent = new Intent(MusicPlayerActivity.this, MultiAudioService.class);
                /**
                 * 传 1 表示 service 要去 播放音乐
                 * 传 2 表示 service 要去 停止音乐
                 */
                intent.putExtra("music_action", AUDIO_STATE);
                startService(intent);
            }
        });

        // 监听进度条
        seekBar = findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                System.out.println("onProgressChanged");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println("onStartTrackingTouch");
                isSeekBarChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println("onStopTrackingTouch");
                isSeekBarChanging = false;
                Intent intent = new Intent(MusicPlayerActivity.this, MultiAudioService.class);

                AUDIO_STATE = 4;
                intent.putExtra("music_action", AUDIO_STATE);
                intent.putExtra("music_seek_bar_change_action", seekBar.getProgress());
                startService(intent);
                //mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        /**
         * init 接收器
         */
        AudioBroadCastReceive audioBroadCastReceiver = new AudioBroadCastReceive() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 获取不到则 musicAction 为 -1
                int musicAction = intent.getIntExtra("music_action", -1);

                int musicSeekBarDurationAction = intent.getIntExtra("music_seek_bar_duration_action", -1);
                int musicSeekBarCurrentAction = intent.getIntExtra("music_seek_bar_currentPosition_action", -1);

                switch (musicAction) {
                    // 音乐 已经在 播放中
                    case 2:
                        Toast.makeText(MusicPlayerActivity.this, "播放中...", Toast.LENGTH_SHORT).show();
                        break;
                    // 音乐 未 在 播放
                    case 1:
                        Toast.makeText(MusicPlayerActivity.this, "停止...", Toast.LENGTH_SHORT).show();
                    default:
                        if (3 == musicAction) {
                            Toast.makeText(MusicPlayerActivity.this, "媒体已关闭", Toast.LENGTH_SHORT).show();
                            AUDIO_STATE = 3;
                        }
                        break;
                }

                switch (musicSeekBarDurationAction) {
                    case -1:
                        break;
                    default:

                        musicSeekBarDurationAction = musicSeekBarDurationAction / 1000;
                        String maxTime = musicSeekBarDurationAction / 60 + ":" + musicSeekBarDurationAction % 60;
                        maxTimeTextView.setText(maxTime);
                        break;
                }

                switch (musicSeekBarCurrentAction) {
                    case -1:
                        break;
                    default:

                        musicSeekBarCurrentAction = musicSeekBarCurrentAction / 1000;
                        seekBar.setProgress(musicSeekBarCurrentAction);
                        String currentTime = musicSeekBarCurrentAction / 60 + ":" + musicSeekBarCurrentAction % 60;
                        currentTimeTextView.setText(currentTime);
                        break;
                }
            }
        };
        broadcastReceiver = audioBroadCastReceiver;
        IntentFilter inf = new IntentFilter();
        // 接收 为 audio_service_action_to_receive 的 广播
        inf.addAction("audio_service_action_to_receive");
        registerReceiver(broadcastReceiver, inf);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String mySessionIp = AddressService.getIP(MusicPlayerActivity.this);
        Object sessionVal = GlobalSessionService.get(mySessionIp);
        if (sessionVal == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(MusicPlayerActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        username = sessionVal.toString();
    }
}