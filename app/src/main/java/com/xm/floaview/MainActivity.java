package com.xm.floaview;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static WindowManager wm = null;
    private static WindowManager.LayoutParams wmParams;
    private static View mView = null;
    private Context ctx = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        //initView
        Button btnOpen = findViewById(R.id.btn_open);
        Button btnClose = findViewById(R.id.btn_close);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWindowInTime();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismisWindow();
            }
        });
    }

    /**
     * 打开窗口
     */
    private void showWindowInTime() {
        addFloatView();
    }

    /**
     * 关闭窗口
     */
    private void dismisWindow() {
        if (wm != null && mView != null) {
            wm.removeViewImmediate(mView);
            wm = null;
            mView = null;
            wmParams = null;
        }
    }

    /**
     * 添加悬浮窗口
     */
    private void addFloatView() {
        if (mView == null || wm == null || wmParams == null) {
//            int screenWidth = ctx.getResources().getDisplayMetrics().widthPixels;
//            int screenHeight = ctx.getResources().getDisplayMetrics().heightPixels;
            //create windows manager
            wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            wmParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT > 25) {
                wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            wmParams.format = 1;
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //init location
            wmParams.gravity = Gravity.TOP | Gravity.LEFT;//坐标原点的位置
            wmParams.x = getInfoX();
            wmParams.y = getInfoY();
            //creat view
            mView = LayoutInflater.from(this).inflate(R.layout.layout_incoming, null);
            ImageView iv_close = mView.findViewById(R.id.iv_close);
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismisWindow();
                }
            });
            ImageView iv_user_avatar = mView.findViewById(R.id.iv_user_avatar);
//            iv_user_avatar.setImageResource(R.mipmap.ic_launcher);
            TextView tv_user_name = mView.findViewById(R.id.tv_user_name);
            tv_user_name.setText("李白" + "   " + "17712341234");
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
                        wm.updateViewLayout(mView, wmParams);
                    }
                    return true;
                }
            });
            wm.addView(mView, wmParams);
        }
    }

    private String PHONE_ALRET_VIEW = "PHONE_ALRET_VIEW";
    private String PHONE_ALRET_VIEW_InfoX = "PHONE_ALRET_VIEW_InfoX";
    private String PHONE_ALRET_VIEW_InfoY = "PHONE_ALRET_VIEW_InfoY";
    private int infoValueDefault = 0;

    private int getInfoX() {
        return ctx.getSharedPreferences(PHONE_ALRET_VIEW, Context.MODE_PRIVATE).getInt(PHONE_ALRET_VIEW_InfoX, infoValueDefault);
    }

    private int getInfoY() {
        return ctx.getSharedPreferences(PHONE_ALRET_VIEW, Context.MODE_PRIVATE).getInt(PHONE_ALRET_VIEW_InfoY, infoValueDefault);
    }

    private boolean setInfoX(int infoX) {
        return ctx.getSharedPreferences(PHONE_ALRET_VIEW, Context.MODE_PRIVATE).edit().putInt(PHONE_ALRET_VIEW_InfoX, infoX).commit();
    }

    private boolean setInfoY(int infoY) {
        return ctx.getSharedPreferences(PHONE_ALRET_VIEW, Context.MODE_PRIVATE).edit().putInt(PHONE_ALRET_VIEW_InfoY, infoY).commit();
    }
}
