package com.ymzz.plat.alibs.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.feilu.utilmy.RecommendItem;
import com.ymzz.plat.alibs.ad.ADSDK;
import com.ymzz.plat.alibs.ad.DownLoadActivity;

/**
 * ����������
 *
 * @ClassName WindowUtils
 */
public class WindowUtils {

    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;
    public static Boolean isShown = false;

    /**
     * ��ʾ������
     */
    public static void showPopupWindow(final Context context, Bitmap bitmapResult, int width, int height, int picwidth, int picheight) {
        if (isShown) {
            return;
        }
        isShown = true;
        // ��ȡӦ�õ�Context
        mContext = context.getApplicationContext();
        // ��ȡWindowManager
        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        mView = setUpView(context, bitmapResult, width, height, picwidth, picheight);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // ����
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // ����flag
        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // ���������WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE��������View�ղ���Back�����¼�
        params.flags = flags;
        // ����������������͸��������ʾΪ��ɫ
        params.format = PixelFormat.TRANSLUCENT;
        // FLAG_NOT_TOUCH_MODAL�������¼����ݵ�����Ĵ���
        // ���� FLAG_NOT_FOCUSABLE �������ڽ�Сʱ�������Ӧ��ͼ���ɲ��ɳ�����Ϊ�ɳ���
        // ���������flag�Ļ���homeҳ�Ļ�����������
        params.width = picwidth;
        params.height = picheight;
        //params.gravity = Gravity.CENTER;
        mWindowManager.addView(mView, params);
    }

    private static View setUpView(final Context context, Bitmap bitmapResult, int width, int height, int picwidth, int picheight) {
        int resourseId = GetResourseIdSelf.getResourseIdByName(
                context.getPackageName(), "layout", "show_picd_layout");
        View menuView = LayoutInflater.from(context).inflate(resourseId, null);
        int picad_layoutId = GetResourseIdSelf.getResourseIdByName(
                context.getPackageName(), "id", "show_picad_layout");
        RelativeLayout show_picad_layout = (RelativeLayout) menuView
                .findViewById(picad_layoutId);
        setImagePosition(show_picad_layout, ADSDK.lo, width, height, picwidth, picheight);
        int picad_picId = GetResourseIdSelf.getResourseIdByName(
                context.getPackageName(), "id", "show_picad_pic");
        ImageView show_AdPic = (ImageView) menuView.findViewById(picad_picId);
        show_AdPic.setImageBitmap(bitmapResult);
        show_AdPic.setScaleType(ImageView.ScaleType.FIT_XY);
        int picad_pic_close = GetResourseIdSelf.getResourseIdByName(
                context.getPackageName(), "id", "show_picad_pic_close");
        final ImageView show_picad_pic_close = (ImageView) menuView
                .findViewById(picad_pic_close);
        show_picad_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ADSDK.ad_list != null && ADSDK.ad_list.size() > 0) {
                    RecommendItem item = ADSDK.ad_list.get(0);
                    if (Integer.parseInt(item.clicktype) == 1) {
                        Uri uri = Uri.parse(item.Pid);
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(it);
                        WindowUtils.hidePopupWindow();
                    } else {
                        if (ADSDK.superDownload == 1) {
                            WindowUtils.hidePopupWindow();
                            Toast.makeText(context, "��������...", Toast.LENGTH_SHORT).show();
                            DownClas.downloadApp(context, ADSDK.ad_list.get(0));
                        } else {
                            Intent intent = new Intent(context, DownLoadActivity.class);
                            intent.putExtra("apkPath", ADSDK.adurl);
                            context.startActivity(intent);
                        }
                    }
                    if (item.ItemId != null) {
                        try {
                            SettingUtil.show_log(context, 2,
                                    Integer.parseInt(item.ItemId));
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    }

                }else{
                    WindowUtils.hidePopupWindow();
                }
            }
        });
        show_picad_pic_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                WindowUtils.hidePopupWindow();

            }
        });
        // ���back��������
        menuView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        WindowUtils.hidePopupWindow();
                        return true;
                    default:
                        return false;
                }
            }
        });

        return menuView;

    }

    /**
     * ���ص�����
     */
    public static void hidePopupWindow() {
        if (isShown && null != mView) {
            mWindowManager.removeView(mView);
            isShown = false;
        }

    }

    /**
     * ���λ������
     */
    private static void setImagePosition(RelativeLayout show_picad_layout, String lo, int width, int height, int picwidth, int picheight) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) show_picad_layout.getLayoutParams();
        String[] los = lo.split(",");
        if (los.length == 1) {
            String[] split = los[0].split(":");
            if (lo.equals("mid:0")) {
                lp.setMargins(width / 2 - picwidth / 2, height / 2 - picheight / 2, 0, 0);
            } else if (split[0].equals("top")) {
                lp.setMargins(width / 2 - picwidth / 2, Integer.parseInt(split[1]), 0, 0);
            } else if (split[0].equals("bom")) {
                lp.setMargins(width / 2 - picwidth / 2, height - picheight - Integer.parseInt(split[1]), 0, 0);
            } else if (split[0].equals("left")) {
                lp.setMargins(Integer.parseInt(split[1]), height / 2 - picheight / 2, 0, 0);
            } else {
                lp.setMargins(width - picwidth - Integer.parseInt(split[1]), height / 2 - picheight / 2, 0, 0);
            }
        } else

        {
            String[] split_1 = los[0].split(":");
            String[] split_2 = los[1].split(":");
            if (split_1[0].equals("top") && split_2[0].equals("left")) {
                lp.setMargins(Integer.parseInt(split_2[1]), Integer.parseInt(split_1[1]), 0, 0);
            } else if (split_1[0].equals("top") && split_2[0].equals("right")) {
                lp.setMargins(width - picwidth - Integer.parseInt(split_2[1]), Integer.parseInt(split_1[1]), 0, 0);
            } else if (split_1[0].equals("btom") && split_2[0].equals("left")) {
                lp.setMargins(Integer.parseInt(split_2[1]), height - picheight - Integer.parseInt(split_1[1]), 0, 0);
            } else {
                lp.setMargins(width - picwidth - Integer.parseInt(split_2[1]), height - picheight - Integer.parseInt(split_1[1]), 0, 0);
            }
        }

        show_picad_layout.setLayoutParams(lp);
    }
}