package com.bwie.liaotian_test2.View;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bwie.liaotian_test2.R;
import com.bwie.liaotian_test2.Recorder;

import java.util.List;

/**
 * Created by dell on 2018/4/17.
 */

public class RecorderAdapter extends ArrayAdapter<Recorder> {

    private List<Recorder> mDatas;
    private Context mContext;
    private int mMinItemWidhth;
    private int mMaxItemWidhth;
    public RecorderAdapter(Context context, List<Recorder> datas) {
        super(context, -1, datas);
        mDatas = datas;
        mContext = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mMaxItemWidhth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidhth = (int) (outMetrics.widthPixels * 0.15f);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(holder==null) {
            holder=new ViewHolder();
            if (convertView == null) {
                convertView = View.inflate(mContext,R.layout.item_re, null);
                holder.length=convertView.findViewById(R.id.fragme);
                holder.time=convertView.findViewById(R.id.icon_time);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
        }
        holder.time.setText(Math.round(getItem(position).getTime())+ "\"");
        ViewGroup.LayoutParams layoutParams = holder.length.getLayoutParams();
        layoutParams.width = (int) (mMinItemWidhth + (mMaxItemWidhth / 60f * getItem(position).getTime()));
        return convertView;
    }

    private class ViewHolder{
       public TextView time;
       private View length;

        public TextView getTime() {
            return time;
        }

        public void setTime(TextView time) {
            this.time = time;
        }

        public View getLength() {
            return length;
        }

        public void setLength(View length) {
            this.length = length;
        }
    }
}