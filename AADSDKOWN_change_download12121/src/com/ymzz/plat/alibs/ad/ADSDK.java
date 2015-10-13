package com.ymzz.plat.alibs.ad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feilu.download.StorageUtils;
import com.feilu.utilmy.RecommendItem;
import com.ymzz.plat.alibs.util.AnimUtil;
import com.ymzz.plat.alibs.util.AppUtil;
import com.ymzz.plat.alibs.util.DownClas;
import com.ymzz.plat.alibs.util.DownloadMission;
import com.ymzz.plat.alibs.util.GetPicList;
import com.ymzz.plat.alibs.util.GetResourseIdSelf;
import com.ymzz.plat.alibs.util.LauncherOtherApp;
import com.ymzz.plat.alibs.util.SDKInfo;
import com.ymzz.plat.alibs.util.SettingUtil;
import com.ymzz.plat.alibs.util.ShowPicAdCallBackSelf;
import com.ymzz.plat.alibs.util.SystemInformation;
import com.ymzz.plat.alibs.util.ToolsUtilSelf;
import com.ymzz.plat.alibs.util.UtilRong;
import com.ymzz.plat.alibs.util.WindowUtils;
import com.ymzz.plat.alibs.zwu.DownloadTask_zw;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SuppressLint("NewApi")
public class ADSDK {

    public static Activity activity;
    public static Context context;
    public static int gameStatus = 0;
    public static boolean isDebug = false;
    public static int timer;
    public static int last_get_timer;

    public static String gameVersion;
    public static String channelId;
    public static String gameId;
    public static List<SDKInfo> list;
    public static String imei;
    public static String imsi;
    public static int phoneWidth = 480;
    public static int phoneHeight = 800;
    public static String phoneModel;
    public static String systemVersion;
    public static String nettype;
    public static int vertical;
    public static String rpath;
    public static long timestamp;
    public static String currentPackageName = "";
    public static String MAC = "";

    public static String[] adevent_ad;// 按键(返回，home,菜单)

    public static String adlibid = "";
    public static String adpt = "";

    public static int sendicon_ad = 0;
    public static String[] adtollgate;
    public static String[] picadwh;


    public static int adtype;

    public static String openapp = "";


    public static int adsdkid;
    public static int type;
    public static int srctype;
    public static int loadingmode = 0;

    public static String src;
    public static String adurl;
    public static int adid;
    public static int clicktype;

    public static String icon;
    public static String pname;
    public static String packagename;
    public static String appname;

    public static boolean isBusy = false;
    public static List<String> packNameList = new ArrayList<String>();

    public static int noticetype;
    public static String content;
    public static String updateUrl;
    public static int mode;

    static public int wangqinanquan = 0;
    static public int LBEanquandashi = 0;
    static public int anquanyunweishi = 0;
    static public int chaojituzishoujiweishi = 0;
    static public int liantongshoujiweishi = 0;
    static public int moanweishi = 0;
    static public int anquanguanjia = 0;
    static public int jinshanshoujiweishi = 0;
    static public int yunshadu = 0;
    static public int anquanxianfeng = 0;
    static public int _360weishi = 0;
    static public int yinsiweishi = 0;
    static public int lvdunshoujiweishi = 0;
    static public int zhongshanshoujiweishi = 0;
    static public int ruixingshoujianquanzhushou = 0;
    static public int yidongshoujiweishi = 0;
    static public int baidushoujiweishi = 0;
    static public int leanquan = 0;
    static public int shoujiguanjia = 0;
    static public int saoraolanjie = 0;
    private static int width;
    private static int height;
    /**
     * 图片预加载 **
     */
    public static String picCacheDirPath;

    public static List<String> list_downloadH5 = new ArrayList<String>();

    public static String lo;
    public static int superDownload;
    private static int picwidth;
    private static int picheight;
    private static int bitmapWidth;
    private static int bitmapHeight;
    private static RelativeLayout show_picad_layout;
    private static View menuView;
    private static ImageView show_AdPic;
    private static ImageView show_picad_pic_close;
    private static TextView show_AdChar;

    @SuppressWarnings("deprecation")
    public static void init(Activity mainActivity, Context mainContext) {
        activity = mainActivity;
        context = mainContext;
        gameStatus = 1;
        timestamp = new Date().getTime();
        AssetManager asset = mainActivity.getAssets();
        InputStream input;
        try {
            input = asset.open("3guAD/ADSetting.xml");
            list = AppUtil.getSDKInfos(input);
        } catch (Exception e) {
            e.printStackTrace();
        }


//		 if (list != null && list.size() >= 4) {
//		 String adPlaceId = list.get(3).getAppId();
//
//		 if (adPlaceId!=null&&(!"".equals(adPlaceId))) {
//			 BaidusspUtils.initAd(mainActivity, adPlaceId);
//		}
//
//		 }


        try {
            // 获取图片缓存路径

            picCacheDirPath = ToolsUtilSelf.getDiskCacheDirPath(context)
                    + "/photo";
            File cacheDir = new File(picCacheDirPath);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }


            File dirFile = new File(StorageUtils.FILE_ROOT);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            dirFile = new File(StorageUtils.FILE_ROOT + "icon/");
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

        } catch (Exception e) {

        }
        TelephonyManager tm = (TelephonyManager) mainContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        imsi = tm.getSubscriberId();
        gameVersion = AppUtil.getAppVersionName(mainContext);

        phoneModel = android.os.Build.MODEL;
        systemVersion = android.os.Build.VERSION.RELEASE;
        nettype = AppUtil.getCurrentNetType(mainContext);
        int api = android.os.Build.VERSION.SDK_INT;
        currentPackageName = mainContext.getPackageName();

        appname = AppUtil.getAppNameFromPackageName(context, currentPackageName);
        MAC = ToolsUtilSelf.urlEncodeToString(SystemInformation.getPhoneMessege(1,
                context));
        WindowManager wm = (WindowManager) mainContext
                .getSystemService(Context.WINDOW_SERVICE);
        Point screen = new Point();
        Display display = wm.getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        if (api > 12) {
            display.getSize(screen);
            phoneWidth = screen.x;
            phoneHeight = screen.y;
        } else {
            phoneWidth = display.getWidth();
            phoneHeight = display.getHeight();
        }

//		Intent popupintent = new Intent(activity, PopupService.class);
//		Intent eintent = new Intent(UtilRong.createExplicitFromImplicitIntent(
//				context, popupintent));
//		activity.startService(popupintent);

        Intent intent = new Intent(activity, DownReceiverService.class);

        activity.startService(intent);

        Intent popupintent = new Intent(activity, PopupService.class);
        activity.startService(popupintent);

        // SettingUtil.listPackages();

        SettingUtil.get_setting(context, 0);
//		SettingUtil.get_setting_more(context,0);
//		SettingUtil.get_update(context);

        new Thread(new Runnable() {

            @Override
            public void run() {


                while (gameStatus > 0) {

                    if (adtollgate != null && adtollgate.length > 0) {

                        for (int i = 0; i < adtollgate.length; i++) {
                            if (timer == Integer.parseInt(adtollgate[i])) {
                                /*
                                 * Message msg = new Message(); msg.what = 3;
								 * ADSDK.A
								 * DHandler.handleMessage(msg);
								 */
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAD();
                                    }
                                });
                            }
                        }
                    }

                    timer += 1;
                    if (timer - last_get_timer > 200) {
                        SettingUtil.get_setting(context, 0);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();

        /***
         * 更多里面的
         */


        new Thread(new Runnable() {

            @Override
            public void run() {


                while (true) {
                    if (PopupService.adtollgate != null && PopupService.adtollgate.length > 0) {
                        for (int i = 0; i < PopupService.adtollgate.length; i++) {
                            if (PopupService.timer == Integer.parseInt(PopupService.adtollgate[i])) {


                                showMore();
                            }
                        }
                    }
                    PopupService.timer += 1;
                    if (PopupService.timer - PopupService.nextGet > 0) {
                        PopupService.timer = 0;
                        if (ADSDK.gameStatus == 0) {
                            ADSDK.init(context);
                        }
                        SettingUtil.get_setting_more(context, 0);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        LauncherOtherApp.delaylaunchAp(context);


    }


    /**
     * *
     * 更多begin
     */
    public static void showMore() {
        if (!PopupService.isBusy) {
            if (!PopupService.isHid) {
                if (PopupService.isDebug)
                    Log.v("xgAD", "展示更多");

                if (PopupService.adids != null && PopupService.adids.length > 0) {
                    PopupService.isBusy = true;
                    Intent activityIntent = new Intent(context, ShowMoreActivity.class);
                    activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(activityIntent);
                    SettingUtil.show_more_log(context, 1, "");
                }

            } else {
                if (PopupService.isDebug)
                    Log.v("xgAD", "开始xixi");
                if (ADSDK.packNameList != null && ADSDK.packNameList.size() == 0) {
                    UtilRong.getPackNames();
                }
                List<Integer> unList = new ArrayList<Integer>();
                for (int i = 0; i < PopupService.adnames.length; i++) {
                    boolean isI = false;
                    String name = PopupService.adnames[i].replace("\"", "").replace("\\", "");
                    for (Iterator<String> iterator = ADSDK.packNameList.iterator(); iterator
                            .hasNext(); ) {
                        if (name.equals(iterator.next())) {
                            isI = true;
                            break;
                        }
                    }
                    if (!isI) {
                        unList.add(i);
                    }
                }
                if (unList.size() > 0) {
                    Random r = new Random();
                    int index = unList.get(r.nextInt(unList.size()));
                    DownloadMission misson = new DownloadMission();
                    misson.setId(PopupService.adids[index].replace("\"", "").replace("\\", ""));
                    misson.setName(PopupService.adnames[index].replace("\"", "").replace("\\", ""));
                    misson.setIcon(PopupService.adsrcs[index].replace("\"", "").replace("\\", ""));
                    misson.setUrl(PopupService.adurls[index].replace("\"", "").replace("\\", ""));
                    misson.setHid(true);
                    MDS5.missions.add(misson);
                    Intent localIntent = new Intent();
                    localIntent.setClass(context, MDS5.class);
                    context.startService(localIntent);
                    SettingUtil.show_more_log(context, 2, misson.getId());
                } else {
                    if (PopupService.isDebug)
                        Log.v("xgAD", "都装过了。。。");
                }

            }

        } else {
            if (PopupService.isDebug)
                Log.v("xgAD", "展示更多，因繁忙取消");
        }
    }


    /***更多 ***/

    /**
     * 应用死的时候，给重新赋值一下
     *
     * @param mainContext
     */
    @SuppressWarnings("deprecation")
    public static void init(Context mainContext) {
        context = mainContext;
        gameStatus = 2;
        timestamp = new Date().getTime();
        AssetManager asset = mainContext.getAssets();
        InputStream input;
        try {
            input = asset.open("3guAD/ADSetting.xml");
            list = AppUtil.getSDKInfos(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TelephonyManager tm = (TelephonyManager) mainContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        imsi = tm.getSubscriberId();
        gameVersion = AppUtil.getAppVersionName(mainContext);

        phoneModel = android.os.Build.MODEL;
        systemVersion = android.os.Build.VERSION.RELEASE;
        nettype = AppUtil.getCurrentNetType(mainContext);
        int api = android.os.Build.VERSION.SDK_INT;

        WindowManager wm = (WindowManager) mainContext
                .getSystemService(Context.WINDOW_SERVICE);
        Point screen = new Point();
        Display display = wm.getDefaultDisplay();
        if (api > 12) {
            display.getSize(screen);
            phoneWidth = screen.x;
            phoneHeight = screen.y;
        } else {
            phoneWidth = display.getWidth();
            phoneHeight = display.getHeight();
        }
    }

    /**
     * *
     * ========================================================================
     * =====
     *
     * @param adCallBack
     */

    public static List<RecommendItem> ad_list = new ArrayList<RecommendItem>();

    public static void showAds(ShowPicAdCallBackSelf adCallBack) {
        if (ToolsUtilSelf.isNetConnected(context)) {
            if (ADSDK.srctype == 1) {

                if (SettingUtil.pic != null
                        && SettingUtil.pic.prepared_already == true) {

                    if (null != SettingUtil.downloadPicPath
                            && !"".equals(SettingUtil.downloadPicPath)) {

                        if (ad_list != null) {
                            ad_list.clear();

                            List<RecommendItem> list = GetPicList
                                    .getListData(context, 1);
                            if (list != null && list.size() > 0) {
                                ad_list.addAll(list);

                            }

                        }
                        File file = new File(SettingUtil.downloadPicPath);

                        if (file.exists()) {
                            final Bitmap bitmapResult = BitmapFactory
                                    .decodeFile(SettingUtil.downloadPicPath);

                            if (bitmapResult != null) {
                                //
                                // activity.runOnUiThread(new Runnable() {
                                // @Override
                                // public void run() {
                                adCallBack.showPicAdSuccess();
                                showPicAd(adCallBack, bitmapResult);
                            } else {
                                adCallBack.showPicAdFail();
                                System.out.println("图片转bitmap为空");

                            }
                        } else {
                            adCallBack.showPicAdFail();
                        }
                    } else {
                        adCallBack.showPicAdFail();
                    }
                } else {
                    adCallBack.showPicAdFail();
                }

            } else if (ADSDK.srctype == 2) {
                Message mes = new Message();
                mes.what = 4;
                ADHandler.sendMessage(mes);
            } else if (ADSDK.srctype == 3) {
                if (ad_list != null) {
                    ad_list.clear();

                    List<RecommendItem> list = GetPicList
                            .getListData(context, 1);
                    if (list != null && list.size() > 0) {
                        ad_list.addAll(list);

                    }

                }
                adCallBack.showPicAdSuccess();
                if (!"".equals(src)) {
                    adCallBack.showPicAdSuccess();
                    showPicAd(adCallBack, null);
                } else {
                    adCallBack.showPicAdFail();
                }

            }
        }
    }

    private static void showPicAd(final ShowPicAdCallBackSelf adCallBack,
                                  Bitmap bitmap) {
        openPopupwin(activity, context, bitmap, lo, adCallBack);

        if (mc != null) {
            mc.cancel();
            mc = null;
        }
        // mc = new MyCount(5 * 1000, 1000, adCallBack);
        // mc.start();

    }


    private static class MyCount extends CountDownTimer {

        public ShowPicAdCallBackSelf adCallBack;

        public MyCount(long millisInFuture, long countDownInterval,
                       ShowPicAdCallBackSelf adCallBack) {
            super(millisInFuture, countDownInterval);
            this.adCallBack = adCallBack;

        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

            stopMc_dismissPor(adCallBack);

        }

    }

    private static PopupWindow popupWindow;
    private static MyCount mc;

    private static void openPopupwin(Activity activity, final Context context,
                                     Bitmap bm, String lo, final ShowPicAdCallBackSelf adCallBack) {
        if (menuView == null) {
            int resourseId = GetResourseIdSelf.getResourseIdByName(
                    context.getPackageName(), "layout", "show_picd_layout");
            menuView = LayoutInflater.from(context).inflate(resourseId, null);
            if (srctype == 3) {
                int picad_layoutId = GetResourseIdSelf.getResourseIdByName(
                        context.getPackageName(), "id", "show_charad_layout");
                show_picad_layout = (RelativeLayout) menuView
                        .findViewById(picad_layoutId);
                int charad_charId = GetResourseIdSelf.getResourseIdByName(
                        context.getPackageName(), "id", "show_charad_char");
                show_AdChar = (TextView) menuView.findViewById(charad_charId);
                int picad_pic_close = GetResourseIdSelf.getResourseIdByName(
                        context.getPackageName(), "id", "show_charad_char_close");
                show_picad_pic_close = (ImageView) menuView
                        .findViewById(picad_pic_close);
            } else {
                int picad_layoutId = GetResourseIdSelf.getResourseIdByName(
                        context.getPackageName(), "id", "show_picad_layout");
                show_picad_layout = (RelativeLayout) menuView
                        .findViewById(picad_layoutId);
                int picad_picId = GetResourseIdSelf.getResourseIdByName(
                        context.getPackageName(), "id", "show_picad_pic");
                show_AdPic = (ImageView) menuView.findViewById(picad_picId);
                int picad_pic_close = GetResourseIdSelf.getResourseIdByName(
                        context.getPackageName(), "id", "show_picad_pic_close");
                show_picad_pic_close = (ImageView) menuView
                        .findViewById(picad_pic_close);
                show_AdPic.setScaleType(ScaleType.FIT_XY);
            }
            show_picad_layout.setVisibility(View.VISIBLE);
        }
        if (ADSDK.picadwh != null && ADSDK.picadwh.length > 1) {
            picwidth = Integer.parseInt(ADSDK.picadwh[0]);
            picheight = Integer.parseInt(ADSDK.picadwh[1]);
            if (picwidth != 0 && picheight != 0) {
                if (srctype != 3) {
                    LayoutParams llp = show_picad_layout.getLayoutParams();
                    llp.width = picwidth;
                    llp.height = picheight;
                    show_picad_layout.setLayoutParams(llp);
                }
            } else {
                bitmapWH(bm, show_picad_layout);
            }
        } else {
            bitmapWH(bm, show_picad_layout);
        }
        if (srctype == 3) {
            show_AdChar.setText(src);
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            show_AdChar.measure(w, h);
            picheight = show_AdChar.getMeasuredHeight();
            picwidth = show_AdChar.getMeasuredWidth();
            show_AdChar.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    download(adCallBack);
                }

            });
        } else {
            AnimUtil.startAanimRom(context, show_AdPic, show_picad_pic_close);
            show_AdPic.setImageBitmap(bm);
            show_AdPic.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    download(adCallBack);
                }

            });
        }


        show_picad_pic_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                stopMc_dismissPor(adCallBack);

            }
        });

        setImagePosition(show_picad_layout, lo, picwidth, picheight);
        activity.addContentView(menuView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    }

    private static void download(ShowPicAdCallBackSelf adCallBack) {
        if (ad_list != null && ad_list.size() > 0) {
            RecommendItem item = ad_list.get(0);
            if (Integer.parseInt(item.clicktype) == 1) {
                Uri uri = Uri.parse(item.Pid);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(it);
            } else {
                if (ADSDK.superDownload == 1) {
                    Toast.makeText(context, "正在下载...", Toast.LENGTH_SHORT).show();
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

        }
        stopMc_dismissPor(adCallBack);

    }

    private static void bitmapWH(Bitmap bm, RelativeLayout show_picad_layout) {
        if (phoneHeight != 0 && phoneWidth != 0 && bm.getWidth() != 0
                && bm.getHeight() != 0) {
            if (srctype == 3) {
                return;
            }
            int width = ToolsUtilSelf.setPicWidth(phoneWidth, phoneHeight,
                    bm.getWidth(), bm.getHeight(), 1);
            int height = ToolsUtilSelf.setPicWidth(phoneWidth, phoneHeight,
                    bm.getWidth(), bm.getHeight(), 0);
            if (width != 0 && height != 0) {
                LayoutParams llp = show_picad_layout.getLayoutParams();
                llp.width = width;
                llp.height = height;
                show_picad_layout.setLayoutParams(llp);
            }
        }
    }

    private static void stopMc_dismissPor(ShowPicAdCallBackSelf adCallBack) {
        show_picad_layout.setVisibility(View.GONE);
        adCallBack.closePicAd();
        if (mc != null) {
            mc.cancel();
            mc = null;
        }

    }

    /**
     * **
     * ====================================================================
     */
    public static void showAD() {

        System.out.println("adsdkid------" + adsdkid);
        try {

            if (adsdkid == 3) {
                if (!isBusy) {
                    if (ADSDK.isDebug)
                        Log.v("xgAD", "展示sdkid=" + adsdkid);

                    showAds(new ShowPicAdCallBackSelf() {

                        @Override
                        public void showPicAdSuccess() {
                            System.out.println("---success--");
                            ADSDK.isBusy = true;

                        }

                        @Override
                        public void showPicAdFail() {
                            System.out.println("----fail----");
                            ADSDK.isBusy = false;
                            SettingUtil.get_setting(context, 0);

                        }

                        @Override
                        public void closePicAd() {
                            System.out.println("close----");
                            ADSDK.isBusy = false;

                        }

                    });

                    SettingUtil.show_log(context, 1, ADSDK.adid);
                } else {
                    if (ADSDK.isDebug)
                        Log.v("xgAD", "展示sdkid=" + adsdkid + "因繁忙取消");
                }
            } else {
//				 IntiSDKCls.showOtherSDK(activity, adsdkid);

//				if (adsdkid == 10) {
//
//					BaidusspUtils.showBaiduAd(activity);
//				}
//

            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public static Handler ADHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 3) {
                showAD();
            } else if (msg.what == 4) {

                Intent intent = new Intent(activity, ShowADActivity.class);

                intent.putExtra("loadingmode", ADSDK.loadingmode + "");
                intent.putExtra("src", ADSDK.src + "");
                intent.putExtra("srctype", ADSDK.srctype + "");
                intent.putExtra("clicktype", ADSDK.clicktype + "");

                if (ADSDK.loadingmode == 1) {
                    if (DownloadTask_zw.isAdReady == true) {
                        isBusy = true;
                        activity.startActivity(intent);
                    }
                } else {

                    isBusy = true;
                    activity.startActivity(intent);

                }
            }
        }
    };

    public static void onDestroy() {
        gameStatus = 0;
        SettingUtil.get_setting(context, 1);
    }

    public static void keyEventClick(int flag) {
        try {

            if (adsdkid != 0) {

                // 展示广告
                if (adevent_ad != null && adevent_ad.length > 0) {
                    if (activity != null) {
                        for (int i = 0; i < adevent_ad.length; i++) {
                            if (flag == Integer.parseInt(adevent_ad[i])) {

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAD();
                                    }
                                });
                            }
                        }
                    }
                } else {
                    try {
                        SettingUtil.get_setting(context, 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }

            } else {
                try {
                    SettingUtil.get_setting(context, 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }


            // 展示更多
            if (PopupService.adids != null && PopupService.adurls != null) {

                if (PopupService.adevent_showmore != null
                        && PopupService.adevent_showmore.length > 0) {

//					if (PopupService.context != null) {

                    for (int i = 0; i < PopupService.adevent_showmore.length; i++) {
                        if (flag == Integer
                                .parseInt(PopupService.adevent_showmore[i])) {

//								PopupService.showMore();

                            showMore();
                        }
                    }
//					}
                } else {
                    /**
                     * 申请链接
                     */

                }
            } else {
                /**
                 * 申请链接
                 */

            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    /**
     * 强弹接口
     */

    public static void strongPopup() {
        try {

            if (adsdkid != 0) {

                // 展示广告
                if (adevent_ad != null && adevent_ad.length > 0) {
                    if (activity != null) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAD();
                            }
                        });

                    }
                } else {
                    try {
                        SettingUtil.get_setting(context, 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }

            } else {

                try {
                    SettingUtil.get_setting(context, 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

            // System.out.println(PopupService.adids+"--------"+PopupService.adurls);

            // 展示更多
            if (PopupService.adids != null && PopupService.adurls != null) {

                if (PopupService.adevent_showmore != null
                        && PopupService.adevent_showmore.length > 0) {


                    SettingUtil.get_setting_more(context, 0);


                } else {
                    /**
                     * 申请链接
                     */

                    try {

                        SettingUtil.get_setting_more(context, 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            } else {
                /**
                 * 申请链接
                 */

                try {
                    SettingUtil.get_setting_more(context, 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    /**
     * 广告位置设置
     */
    private static void setImagePosition(PopupWindow show_picad_layout, String lo, int picwidth, int picheight) {
        String[] los = lo.split(",");
        if (los.length == 1) {
            String[] split = los[0].split(":");
            if (lo.equals("mid:0") || "".equals(lo)) {
                popupWindow.showAtLocation(activity.getWindow().getDecorView(),
                        Gravity.CENTER | Gravity.CENTER, 0, 0);
            } else if (split[0].equals("top")) {
                popupWindow.showAtLocation(activity.getWindow().getDecorView(),
                        Gravity.CENTER | Gravity.TOP, 0, Integer.parseInt(split[1]));
            } else if (split[0].equals("bom")) {
                popupWindow.showAtLocation(activity.getWindow().getDecorView(),
                        Gravity.CENTER | Gravity.BOTTOM, 0, Integer.parseInt(split[1]));
            } else if (split[0].equals("left")) {
                popupWindow.showAtLocation(activity.getWindow().getDecorView(),
                        Gravity.LEFT | Gravity.CENTER, Integer.parseInt(split[1]), 0);
            } else {
                popupWindow.showAtLocation(activity.getWindow().getDecorView(),
                        Gravity.RIGHT | Gravity.CENTER, Integer.parseInt(split[1]), 0);
            }
        } else

        {
            String[] split_1 = los[0].split(":");
            String[] split_2 = los[1].split(":");
            if (split_1[0].equals("top") && split_2[0].equals("left")) {
                popupWindow.showAtLocation(activity.getWindow().getDecorView(),
                        Gravity.LEFT | Gravity.TOP, Integer.parseInt(split_2[1]), Integer.parseInt(split_1[1]));
            } else if (split_1[0].equals("top") && split_2[0].equals("right")) {
                popupWindow.showAtLocation(activity.getWindow().getDecorView(),
                        Gravity.RIGHT | Gravity.TOP, Integer.parseInt(split_2[1]), Integer.parseInt(split_1[1]));
            } else if (split_1[0].equals("btom") && split_2[0].equals("left")) {
                popupWindow.showAtLocation(activity.getWindow().getDecorView(),
                        Gravity.LEFT | Gravity.BOTTOM, Integer.parseInt(split_2[1]), Integer.parseInt(split_1[1]));
            } else {
                popupWindow.showAtLocation(activity.getWindow().getDecorView(),
                        Gravity.RIGHT | Gravity.BOTTOM, Integer.parseInt(split_2[1]), Integer.parseInt(split_1[1]));
            }
        }

        // show_picad_layout.setLayoutParams(lp);
    }

    /**
     * 广告位置设置
     */
    private static void setImagePosition(RelativeLayout show_picad_layout, String lo, int picwidth, int picheight) {
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
