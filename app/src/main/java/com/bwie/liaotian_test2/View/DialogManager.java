package com.bwie.liaotian_test2.View;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bwie.liaotian_test2.R;

/**
 * Created by dell on 2018/4/16.
 */

public class DialogManager {
    private Dialog mDialog;
    private ImageView mIcon;
    private ImageView mVoice;
    private TextView mLabel;

    private Context mContext;

    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }
    /**
     * 显示对话框
     */
    public void  showRecordeingDialog(){
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog, null);
        mDialog.setContentView(view);
        //获取控件
        mIcon = (ImageView) mDialog.findViewById(R.id.iv1);
        mVoice = (ImageView) mDialog.findViewById(R.id.iv2);
        mLabel = (TextView) mDialog.findViewById(R.id.tv);
        //显示
        mDialog.show();
    }
    /**
     * 正在录制提示
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.recorder);
            mLabel.setText(R.string.ss);
        }
    }
    /**
     * 取消录制对话框提示
     */
    public void wantToCancel(){
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.cancel);
            mLabel.setText("松开手指,取消发送");
        }
    }
    /**
     * 录音时间过短提示
     */
    public void tooShort(){
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.voice_to_short);
            mLabel.setText("录音时间过短");
        }
    }
    /**
     * 取消对话框
     */
    public void dimissDialog(){
        if (mDialog != null && mDialog.isShowing()) {
           mDialog.dismiss();
           mDialog=null;
        }
    }
    /**
     * 显示音量大小
     */
    public void updateVoiceLevel(int level){
        if (mDialog != null && mDialog.isShowing()) {
            int resId = mContext.getResources().getIdentifier("v" + level, "drawable", mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }
}
