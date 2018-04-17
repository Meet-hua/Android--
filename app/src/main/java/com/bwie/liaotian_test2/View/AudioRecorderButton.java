package com.bwie.liaotian_test2.View;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bwie.liaotian_test2.R;

/**
 * Created by dell on 2018/4/16.
 */

public class AudioRecorderButton extends android.support.v7.widget.AppCompatButton implements AudioManager.AudioStateListener {
    private static final int STATE_NORMAL = 1;//正常状态
    private static final int STATE_RECORDING = 2;//录音状态
    private static final int STATE_WANT_TO_CANCEL = 3;//取消状态
    private int mCurState = STATE_NORMAL;//当前状态
    private boolean isRecording = false;//是否正在录音
    private static final int DISTANXE_Y_CANCEL = 50;
    private AudioManager mAudioManager;
    private DialogManager mDialogManger;
    private boolean mReady = false;//是否触发longClick
    private float mTime;//计时
    public AudioRecorderButton(Context context) {
        super(context,null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        String dir = Environment.getExternalStorageDirectory() + "/my_recorder_audios";
        mAudioManager = AudioManager.getmInstance(dir);
        mAudioManager.setAudioStateListener(this);
        mDialogManger=new DialogManager(context);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAudioManager.prepareAudio();
                mReady = true;
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x= (int) event.getX();
        int y= (int) event.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                changeSate(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    if (isCancelRecorder(x, y)) {
                        changeSate(STATE_WANT_TO_CANCEL);
                    } else {
                        changeSate(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!mReady){
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime < 0.6f) {
                    mDialogManger.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_LODING_DISMISS, 1000);
                } else if (mCurState == STATE_RECORDING) {//正常录制结束
                    mDialogManger.dimissDialog();
                    mAudioManager.release();
                    if (mListener != null) {
                        mListener.onFinish(mTime, mAudioManager.getmCurrentFilePath());
                    }
                } else if (mCurState == STATE_WANT_TO_CANCEL) {
                    mDialogManger.dimissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);
    }
    /**
     * 根据移动后的位置，判断是否取消录音
     */
    private boolean isCancelRecorder(int x, int y) {
         if(x<0||x>getWidth()){
             return true;
         }
         if(y<-DISTANXE_Y_CANCEL||y>getHeight()+DISTANXE_Y_CANCEL){
             return true;
         }
        return false;
    }
    /**
     * 根据不同状态，更改不同的文字和显示的背景
     */
    private void changeSate(int state) {
        if (mCurState != state) {
            mCurState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_normal);
                    setText(R.string.str_recorder_normal);
                    break;

                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.str_recorder_recording);
                    if (isRecording) {
                        mDialogManger.recording();
                    }
                    break;

                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.str_recorder_want_cancel);
                       mDialogManger.wantToCancel();
                    break;
            }
        }
    }

    /**
     * 重置标识位
     */
    private void reset() {
        changeSate(STATE_NORMAL);
        isRecording = false;
        mReady = false;
        mTime = 0;
    }
    /**
     * 开始播放时回调此方法
     */
    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHAGE = 0x111;
    private static final int MSG_LODING_DISMISS = 0x112;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    mDialogManger.showRecordeingDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;

                case MSG_VOICE_CHAGE:
                    mDialogManger.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;

                case MSG_LODING_DISMISS:
                    mDialogManger.dimissDialog();
                    break;
            }
        }
    };


    /**
     * 获取音量大小，并计时
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                SystemClock.sleep(100);
                mTime += 0.1f;
                mHandler.sendEmptyMessage(MSG_VOICE_CHAGE);
            }
        }
    };

    /**
     * 完成录制后的回调接口
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float time, String filePath);
    }

    private AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        mListener = listener;
    }
}
