package com.bwie.liaotian_test2;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bwie.liaotian_test2.View.AudioRecorderButton;
import com.bwie.liaotian_test2.View.MediaManager;
import com.bwie.liaotian_test2.View.RecorderAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder> list=new ArrayList<>();
    private AudioRecorderButton audioRecorderButton;
    private View animView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.lv);
        audioRecorderButton=findViewById(R.id.audio_button);
        audioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float time, String filePath) {
                Recorder recorder=new Recorder(time,filePath);
                list.add(recorder);
                mAdapter.notifyDataSetChanged();
                lv.setSelection(list.size()-1);
            }
        });

        mAdapter=new RecorderAdapter(this,list);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   if(animView!=null){
                       animView.setBackgroundResource(R.drawable.adj);
                       animView=null;
                   }
                  //播放动画
                  animView = view.findViewById(R.id.icon_v);
                  animView.setBackgroundResource(R.drawable.play_anim);
                  AnimationDrawable anim= (AnimationDrawable) animView.getBackground();
                  anim.start();
                  // 播放音频
                MediaManager.playSound(list.get(position).getFilePath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        animView.setBackgroundResource(R.drawable.adj);
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        MediaManager.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        MediaManager.resume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        MediaManager.release();
        super.onDestroy();
    }
}
