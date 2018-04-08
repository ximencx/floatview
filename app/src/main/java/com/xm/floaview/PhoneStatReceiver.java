package com.xm.floaview;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ximencx on 2015/10/29.
 */
public class PhoneStatReceiver extends BroadcastReceiver {

    private static WindowManager wManager = null;
    private static WindowManager.LayoutParams wmParams = null;
    private static View mView = null;
    private Context mContext = null;
    private String userPhone = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 通话状态回调
     */
    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            userPhone = incomingNumber;
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e("ximencx", "CALL_STATE_RINGING: " + userPhone);
                    showWindowInTime();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.e("ximencx", "CALL_STATE_OFFHOOK:");
//                    showWindowInTime();
//                    dismisWindow();
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.e("ximencx", "CALL_STATE_IDLE:");
                    dismisWindow();
                    break;
            }

        }
    };

    /**
     * 打开窗口
     */
    private void showWindowInTime() {
        addFloatView();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                addFloatView();
//            }
//        }, 1000);
    }

    /**
     * 关闭窗口
     */
    private void dismisWindow() {
        if (wManager != null && mView != null) {
            wManager.removeViewImmediate(mView);
            wmParams = null;
            wManager = null;
            mView = null;
        }
    }

    /**
     * 添加悬浮窗口
     */
    private void addFloatView() {
        if (mView == null || wManager == null || wmParams == null) {
//            int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
//            int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
            //create windows manager
            wManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            wmParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT > 25) {
                wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            wmParams.format = PixelFormat.TRANSPARENT;
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //init location
            wmParams.gravity = Gravity.TOP | Gravity.LEFT;//坐标原点的位置
            wmParams.x = getInfoX();
            wmParams.y = getInfoY();
            //creat view
            mView = LayoutInflater.from(mContext).inflate(R.layout.phone_show, null);
            TextView userNameTextView = (TextView) mView.findViewById(R.id.phone_user_name_tv);
            TextView userPhoneTextView = (TextView) mView.findViewById(R.id.phone_user_num);
            TextView userDeptTextView = (TextView) mView.findViewById(R.id.phone_user_dept);
            ImageView closeBtn = (ImageView) mView.findViewById(R.id.phone_call_close);
            if (userNameTextView != null) {
                userNameTextView.setText("name");
            }
            if (userPhoneTextView != null) {
                userPhoneTextView.setText("110");
            }
            if (userDeptTextView != null) {
                userDeptTextView.setText("depte");
            }
            //touch phoneStateListener
            mView.setOnTouchListener(new View.OnTouchListener() {
                float lastX, lastY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    float x = event.getX();
                    float y = event.getY();
                    if (action == MotionEvent.ACTION_DOWN) {
                        lastX = x;
                        lastY = y;
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        wmParams.x += (int) (x - lastX) / 5; // 减小偏移量,防止过度抖动
                        wmParams.y += (int) (y - lastY) / 5; // 减小偏移量,防止过度抖动
                        //save location
                        setInfoX(wmParams.x);
                        setInfoY(wmParams.y);
                        wManager.updateViewLayout(mView, wmParams);
                    }
                    return true;
                }
            });
            wManager.addView(mView, wmParams);
        }
    }

    private String PHONE_ALRET_VIEW = "PHONE_ALRET_VIEW";
    private String PHONE_ALRET_VIEW_InfoX = "PHONE_ALRET_VIEW_InfoX";
    private String PHONE_ALRET_VIEW_InfoY = "PHONE_ALRET_VIEW_InfoY";
    private int infoValueDefault = 0;

    private int getInfoX() {
        return mContext.getSharedPreferences(PHONE_ALRET_VIEW, Context.MODE_PRIVATE).getInt(PHONE_ALRET_VIEW_InfoX, infoValueDefault);
    }

    private int getInfoY() {
        return mContext.getSharedPreferences(PHONE_ALRET_VIEW, Context.MODE_PRIVATE).getInt(PHONE_ALRET_VIEW_InfoY, infoValueDefault);
    }

    private boolean setInfoX(int infoX) {
        return mContext.getSharedPreferences(PHONE_ALRET_VIEW, Context.MODE_PRIVATE).edit().putInt(PHONE_ALRET_VIEW_InfoX, infoX).commit();
    }

    private boolean setInfoY(int infoY) {
        return mContext.getSharedPreferences(PHONE_ALRET_VIEW, Context.MODE_PRIVATE).edit().putInt(PHONE_ALRET_VIEW_InfoY, infoY).commit();
    }
}
