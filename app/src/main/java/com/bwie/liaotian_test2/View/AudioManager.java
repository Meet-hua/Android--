package com.bwie.liaotian_test2.View;

import android.media.MediaRecorder;

import java.io.File;
import java.util.UUID;

/**
 * Created by dell on 2018/4/17.
 */

public class AudioManager {

    private MediaRecorder mMediaRecorder;
    private String mDir;// 保存的目录
    private String mCurrentFilePath;// 保存音频文件的全路径
    private boolean isPrepared = false;// 是否准备完毕
    private static AudioManager mInstance;

    private AudioManager(String dir) {
        mDir = dir;
    }
    public static AudioManager getmInstance(String mDir) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager(mDir);
                }
            }
        }
        return mInstance;
    }

    /**
     * 准备完毕的回调
     */
    public interface AudioStateListener {
        void wellPrepared();
    }

    private AudioStateListener mListener;

    public void setAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }
    /** 准备录制 */
    public void prepareAudio() {
        try {
            isPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = generateName();
            File file = new File(dir, fileName);
            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            // 设置输出文件
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            // 设置音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            // 设置音频编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mMediaRecorder.prepare();
            mMediaRecorder.start();

            isPrepared = true;
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 获取音量大小 */
    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            try {
                //mMediaRecorder.getMaxAmplitude() 1-32767
                //注意此处mMediaRecorder.getMaxAmplitude 只能取一次，如果前面取了一次，后边再取就为0了
                return ((mMediaRecorder.getMaxAmplitude() * maxLevel) / 32768) + 1;
            } catch (Exception e) {
            }

        }
        return 1;
    }

    /** 保存录音，释放资源 */
    public void release() {
        if(mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    /** 取消录制 */
    public void cancel() {
        release();
        if(mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            if(file.exists()) {
                file.delete();
                mCurrentFilePath = null;
            }
        }
    }

    /** 获取录制音频的总路径 */
    public String getmCurrentFilePath(){
        return mCurrentFilePath;
    }

    /**
     * 生成一个随机名字
     */
    private String generateName() {
        return UUID.randomUUID().toString() + ".amr";
    }
}
