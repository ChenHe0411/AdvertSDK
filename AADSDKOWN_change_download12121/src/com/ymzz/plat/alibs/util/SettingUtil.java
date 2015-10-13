package com.ymzz.plat.alibs.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.feilu.download.StorageUtils;
import com.ymzz.plat.alibs.ad.ADSDK;
import com.ymzz.plat.alibs.ad.PopupService;
import com.ymzz.plat.alibs.ad.UpdateActivity;
import com.ymzz.plat.alibs.zwu.DownloadTask_zw;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SettingUtil {
    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 4, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    public static String getDomainName(Context context) {
        String domainName = "www.baopiqi.com";

        if (StoreLocalDataUtil.showAppNameStoreData(context).length() > 1) {
            domainName = StoreLocalDataUtil.showAppNameStoreData(context);
        } else {

            domainName = "www.baopiqi.com";
        }
        return domainName;
    }

    public static PicDownloadThreadself pic;
    public static String downloadPicPath;

    // +"86600205167403"
    public static void get_setting(final Context context, final int out) {
        ADSDK.last_get_timer = ADSDK.timer;
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = "http://"
                            + getDomainName(context)
                            + "/api/ad.php?gameid="
                            + 1000
                            + "&qudao="
                            + ADSDK.channelId
                            + "&ver="
                            + ADSDK.gameVersion
                            + "&uid="
                            + ADSDK.imei
                            + "&imsi="
                            + ADSDK.imsi
                            + "&phoneModel="
                            + URLEncoder
                            .encode(ADSDK.phoneModel.replace(' ', '_'),
                                    "UTF-8") + "&os=android-"
                            + ADSDK.systemVersion + "&ratio="
                            + ADSDK.phoneWidth + "x" + ADSDK.phoneHeight
                            + "&nettype=" + ADSDK.nettype + "&out=" + out
                            + "&time=" + ADSDK.timer + "&timestamp="
                            + ADSDK.timestamp + "&packagename="
                            + ADSDK.currentPackageName + "&mac=" + ADSDK.MAC
                            + "&appname="
                            + URLEncoder.encode(ADSDK.appname, "UTF-8");
//					String urlStr ="http://www.baopiqi.com/api/ad.php?gameid=108&qudao=386&ver=2.4&uid=865056029175102&imsi=null&phoneModel=MI_3&os=android-4.4.4&ratio=1920x1080&nettype=wifi&out=0&time=1&timestamp=1442369398627&packagename=com.aaaaaa.adtest&mac=14f65a905451&appname=我的世界";

                    Log.v("xgAD_getSetting", ADSDK.isDebug + "");
                    if (ADSDK.isDebug)
                        Log.v("xgAD_getSetting", urlStr);

                    System.out.println("urlStr----" + urlStr);
                    URL url = new URL(urlStr);

                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty(
                            "User-Agent",
                            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Shuame)");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        int a = threadPool.getActiveCount();
                        if (a <= 1) {
                            Log.e("a", "111");
                        } else {
                            Log.e("a", "222");
                        }
                        InputStream inputStream = conn.getInputStream();
                        String rs = UtilRong.convertStreamToString(inputStream);
                        System.out.println("rs---------" + rs);
                        JSONObject jobj = new JSONObject(rs);

                        ADSDK.adtollgate = jobj
                                .getString("adtollgate")
                                .substring(
                                        1,
                                        jobj.getString("adtollgate").length() - 1)
                                .split(",");

                        // if (jobj.getString("mz") != null) {
                        // ADSDK.mzdata = jobj.getString("mz");
                        //
                        // }
                        // if (jobj.getString("adpt") != null) {
                        // ADSDK.adpt = jobj.getString("adpt");
                        //
                        // if ("mz".equals(ADSDK.adpt)) {
                        // MiaozhenInterface.sendrequest();
                        //
                        // }
                        //
                        // }
                        if (jobj.has("adevent")) {
                            ADSDK.adevent_ad = jobj
                                    .getString("adevent")
                                    .substring(
                                            1,
                                            jobj.getString("adevent").length() - 1)
                                    .split(",");
                        }
                        if (jobj.has("super")) {
                            ADSDK.superDownload = jobj.getInt("super");
                        }
                        if (jobj.has("lo")) {
                            ADSDK.lo = jobj.getString("lo");
                        }
                        if (jobj.has("nextdomain")) {
                            String jobj_nextdomain = jobj
                                    .getString("nextdomain");

                            // System.out.println("--22222--"+jobj_nextdomain);
                            if (jobj_nextdomain != null
                                    && (!"".equals(jobj_nextdomain))) {
                                StoreLocalDataUtil.storeAppNameData(context,
                                        jobj_nextdomain);
                            }
                        }

                        if (jobj.has("adtype")) {
                            ADSDK.adtype = jobj.getInt("adtype");
                        }

                        if (jobj.has("openapp")) {
                            if (jobj.getString("openapp") != null) {
                                ADSDK.openapp = jobj.getString("openapp");
                            }
                        }
                        if (jobj.has("adwh")) {
                            if (jobj.getString("adwh") != null) {

                                ADSDK.picadwh = jobj
                                        .getString("adwh")
                                        .substring(1,
                                                jobj.getString("adwh").length() - 1)
                                        .split(",");
                            }
                        }

                        if (jobj.has("adsdkid")) {
                            ADSDK.adsdkid = jobj.getInt("adsdkid");
                        }

                        if (jobj.has("type")) {
                            ADSDK.type = jobj.getInt("type");
                        }
                        if (jobj.has("sendicon")) {
                            ADSDK.sendicon_ad = jobj.getInt("sendicon");
                        }

                        if (ADSDK.adsdkid == 3) {

                            if (jobj.has("src")) {
                                ADSDK.src = jobj.getString("src");
                            }
                            if (jobj.has("adurl")) {
                                ADSDK.adurl = jobj.getString("adurl");
                            }

                            if (jobj.has("srctype")) {
                                ADSDK.srctype = jobj.getInt("srctype");
                            }

                            if (jobj.has("loadingmode")) {
                                ADSDK.loadingmode = jobj.getInt("loadingmode");
                            }

                            if (ADSDK.loadingmode == 1 && ADSDK.srctype == 2) {
                                /**
                                 * 下载h5预加载
                                 */
                                if (ADSDK.src != null
                                        && (!"".equals(ADSDK.src))) {
                                    DownloadTask_zw doDownload = new DownloadTask_zw(
                                            ADSDK.list_downloadH5, ADSDK.src);
                                    doDownload.start();
                                }

                            }

                            if (ADSDK.src != null && (!"".equals(ADSDK.src))
                                    && ADSDK.srctype == 1) {

                                String Catalog = "fei";
                                if (ADSDK.src.contains("/")) {

                                    if (ADSDK.src.contains(".")
                                            && (ADSDK.src.lastIndexOf(".") > ADSDK.src
                                            .lastIndexOf("/"))) {
                                        Catalog = ADSDK.src.substring(
                                                ADSDK.src.lastIndexOf("/") + 1,
                                                ADSDK.src.lastIndexOf("."));
                                    } else {

                                        Catalog = ADSDK.src.substring(ADSDK.src
                                                .lastIndexOf("/") + 1);

                                    }

                                }
                                downloadPicPath = ADSDK.picCacheDirPath + "/"
                                        + Catalog;

                                File cacheDir = new File(ADSDK.picCacheDirPath);
                                if (!cacheDir.exists()) {
                                    cacheDir.mkdirs();
                                }
                                pic = new PicDownloadThreadself(ADSDK.src,
                                        downloadPicPath);

                                pic.start();

                            }

                            if (jobj.has("clicktype")) {
                                ADSDK.clicktype = jobj.getInt("clicktype");
                            }
                            if (jobj.has("adid")) {
                                ADSDK.adid = jobj.getInt("adid");
                            }
                            if (jobj.has("icon")) {
                                ADSDK.icon = jobj.getString("icon");
                            }

                            if (jobj.has("pname")) {
                                if (jobj.getString("pname") != null) {
                                    ADSDK.pname = jobj.getString("pname");
                                    String AppNameStore = AddOtherAppShortcutUtil
                                            .showAppNameStoreData(context);
                                    if (!AppNameStore.contains(ADSDK.pname)) {

                                        AddOtherAppShortcutUtil
                                                .storeAppNameData(context,
                                                        AppNameStore + ""
                                                                + ADSDK.pname);

                                    }
                                } else {
                                    ADSDK.pname = "spotgame";
                                }

                            } else {
                                ADSDK.pname = "spotgame";

                            }

                            if (jobj.has("packagename")) {
                                ADSDK.packagename = jobj
                                        .getString("packagename");
                            }
                            if (ADSDK.icon != null && (!"".equals(ADSDK.icon))) {
                                downloadAdIcon(context, ADSDK.icon, ADSDK.pname);
                            }

                        }

                        if (jobj.has("adlibid")) {
                            ADSDK.adlibid = jobj.getString("adlibid");
                        }

                        if (ADSDK.isDebug)
                            Log.e("xgAD", "当前sdkid=" + ADSDK.adsdkid);

                    }
                } catch (Exception e) {
                    if (ADSDK.isDebug)
                        Log.e("xgAD", "请求广告配置失败");


                    e.printStackTrace();
                }
            }
        });

    }

    public static void get_update(final Context context) {
        new Thread() {
            public void run() {
                try {
                    // URL url = new URL(
                    // "http://www.baopiqi.com/api/notice.php?gameid=1&qudao="
                    // + qudao + "&ver=1&uid=" + imei);
                    URL url = new URL(
                            "http://"
                                    + getDomainName(context)
                                    + "/api/noticev1.php?gameid="
                                    + ADSDK.gameId
                                    + "&qudao="
                                    + ADSDK.channelId
                                    + "&ver="
                                    + ADSDK.gameVersion
                                    + "&uid="
                                    + ADSDK.imei
                                    + "&phoneModel="
                                    + URLEncoder.encode(
                                    ADSDK.phoneModel.replace(' ', '_'),
                                    "UTF-8") + "&os=android-"
                                    + ADSDK.systemVersion + "&ratio="
                                    + ADSDK.phoneWidth + "x"
                                    + ADSDK.phoneHeight + "&nettype="
                                    + ADSDK.nettype);
                    Log.e("xgAD_getUpdate", "url " + url);
                    // URL url = new URL(
                    // "http://www.baopiqi.com/api/noticev1.php?gameid=4&qudao=21&ver=1.1");
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty(
                            "User-Agent",
                            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Shuame)");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String rs = UtilRong.convertStreamToString(inputStream);
                        JSONObject jobj = new JSONObject(rs);
                        if (jobj.has("noticetype")) {
                            ADSDK.noticetype = jobj.getInt("noticetype");
                            ADSDK.mode = jobj.getInt("mode");
                            // count = jobj.getInt("count");
                            if (ADSDK.noticetype == 3 || ADSDK.noticetype == 4) {
                                // 更新
                                ADSDK.updateUrl = jobj.getString("url") + "&a="
                                        + new Date().getTime();
                                ADSDK.content = jobj.getString("content");// 150608

                                if (ADSDK.updateUrl != null && (!"".equals(ADSDK.updateUrl))) {
                                    try {
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(context, UpdateActivity.class);
                                                context.startActivity(intent);
                                            }
                                        });
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }

                                }

                            }
                        } else {

                        }
                    }
                } catch (Exception e) {
                    if (ADSDK.isDebug)
                        Log.e("xgAD", "请求更新失败");
                }
            }
        }.start();

    }

    public static void listPackages() {
        ArrayList<PInfo> apps = getInstalledApps(false); /*
                                                         *
														 * 
														 * false = no system
														 * 
														 * 
														 * packages
														 */
        final int max = apps.size();
        for (int i = 0; i < max; i++) {
            apps.get(i).prettyPrint();
            Log.e("xingxing", apps.get(i).pname);
            if ("com.nqmobile.antivirus20".equals(apps.get(i).pname)) {
                // 网秦安全
                ADSDK.wangqinanquan = 1;
            }
            if ("com.lbe.security".equals(apps.get(i).pname)) {
                // LBE安全大师
                ADSDK.LBEanquandashi = 1;
            }
            if ("com.ml.cloudguard".equals(apps.get(i).pname)) {
                // 安全云卫士
                ADSDK.anquanyunweishi = 1;
            }
            if ("com.antutu.safe".equals(apps.get(i).pname)) {
                // 超级兔子手机卫士
                ADSDK.chaojituzishoujiweishi = 1;
            }
            if ("com.chainanicom.mobileguard".equals(apps.get(i).pname)) {
                // 联通手机卫士
                ADSDK.liantongshoujiweishi = 1;
            }
            if ("com.mobileann.MobileAnn".equals(apps.get(i).pname))

            {
                // 摩安卫士
                ADSDK.moanweishi = 1;
            }
            if ("com.anguanjia.safe".equals(apps.get(i).pname)) {
                // 安全管家
                ADSDK.anquanguanjia = 1;
            }
            if ("com.ijinshan.mguard".equals(apps.get(i).pname)) {
                // 金山手机卫士
                ADSDK.jinshanshoujiweishi = 1;
            }
            if ("com.vulnhunt.cloudscan".equals(apps.get(i).pname)) {
                // 云杀毒
                ADSDK.yunshadu = 1;
            }
            if ("kvpioneer.cmcc".equals(apps.get(i).pname)) {
                // 安全先锋
                ADSDK.anquanxianfeng = 1;
            }
            if ("com.qihoo360.mobilesafe".equals(apps.get(i).pname)) {
                // 360卫士
                ADSDK._360weishi = 1;
            }
            if ("cn.ys007.secret".equals(apps.get(i).pname)) {
                // 隐私卫士
                ADSDK.yinsiweishi = 1;
            }
            if ("com.alexchen.mobilesafeexercise".equals(apps.get(i).pname)) {
                // 绿盾手机卫士
                ADSDK.lvdunshoujiweishi = 1;
            }
            if ("com.blucelee.zsmobilesafe".equals(apps.get(i).pname)) {
                // 中山手机卫士
                ADSDK.zhongshanshoujiweishi = 1;
            }
            if ("project.rising".equals(apps.get(i).pname)) {
                // 瑞星手机安全助手
                ADSDK.ruixingshoujianquanzhushou = 1;
            }
            if ("com.ydsjws.mobileguard".equals(apps.get(i).pname)) {
                // 移动手机卫士
                ADSDK.yidongshoujiweishi = 1;
            }
            if ("cn.opda.a.phonoalbumshoushou".equals(apps.get(i).pname)) {
                // 百度手机卫士
                ADSDK.baidushoujiweishi = 1;
            }
            if ("com.lenovo.safecenter".equals(apps.get(i).pname)) {
                // 乐安全
                ADSDK.leanquan = 1;
            }
            if ("com.tencent.qqpimsecure".equals(apps.get(i).pname)) {
                // 手机管家
                ADSDK.shoujiguanjia = 1;
            }
            if ("com.mediatek.mtklogger".equals(apps.get(i).pname)) {
                // 骚扰拦截
                ADSDK.saoraolanjie = 1;
            }

        }
    }

    static class PInfo {
        private String appname = "";
        private String pname = "";
        private String versionName = "";
        private int versionCode = 0;
        @SuppressWarnings("unused")
        private Drawable icon;

        private void prettyPrint() {
            Log.e("taskmanger", appname + "\t" + pname + "\t" +

                    versionName + "\t" + versionCode + "\t");
        }
    }

    public static ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = ADSDK.activity.getPackageManager()
                .getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            PInfo newInfo = new PInfo();
            newInfo.appname = p.applicationInfo.loadLabel(
                    ADSDK.activity.getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(ADSDK.activity
                    .getPackageManager());
            // 判断是否是系统程序（非系统程序 显示到列表）
            if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                res.add(newInfo);
            }
        }
        return res;
    }

    public static void show_log(final Context context, final int ok,
                                final int adid) {
        new Thread() {
            public void run() {
                try {
                    String urlStr = "http://"
                            + getDomainName(context)
                            + "/api/adlog.php?gameid="
                            + ADSDK.gameId
                            + "&qudao="
                            + ADSDK.channelId
                            + "&uid="
                            + ADSDK.imei
                            + "&ver="
                            + ADSDK.gameVersion
                            + "&type="
                            + ADSDK.type
                            + "&adtype="
                            + ADSDK.adtype
                            + "&adsdkid="
                            + ADSDK.adsdkid
                            + "&key="
                            + MD5.getMD5((ADSDK.imei + ADSDK.gameId + ADSDK.imei)
                            .getBytes()) + "&adid=" + adid

                            + "&packagename=" + ADSDK.currentPackageName
                            + "&adlibid=" + ADSDK.adlibid + "&adpt="
                            + ADSDK.adpt

                            + "&ok=" + ok;
                    if (ADSDK.isDebug)
                        Log.v("xgAD_showLog", urlStr);
                    URL url = new URL(urlStr);

                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty(
                            "User-Agent",
                            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Shuame)");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String rs = UtilRong.convertStreamToString(inputStream);
                        if (rs.equals("ok")) {
                            if (ADSDK.isDebug)
                                Log.v("xgAD", "展示日志保存成功！");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * h5页面的统计
     * adid写0；
     * <p/>
     * adlibid由H5传过来
     *
     * @param context
     * @param ok
     * @param adlibid
     */
    public static void show_H5page_log(final Context context, final int ok,
                                       final int adlibid) {
        new Thread() {
            public void run() {
                try {
                    String urlStr = "http://"
                            + getDomainName(context)
                            + "/api/adlog.php?gameid="
                            + ADSDK.gameId
                            + "&qudao="
                            + ADSDK.channelId
                            + "&uid="
                            + ADSDK.imei
                            + "&ver="
                            + ADSDK.gameVersion
                            + "&type="
                            + ADSDK.type
                            + "&adtype="
                            + ADSDK.adtype
                            + "&adsdkid="
                            + ADSDK.adsdkid
                            + "&key="
                            + MD5.getMD5((ADSDK.imei + ADSDK.gameId + ADSDK.imei)
                            .getBytes()) + "&adid=0"

                            + "&packagename=" + ADSDK.currentPackageName
                            + "&adlibid=" + adlibid + "&adpt="
                            + ADSDK.adpt

                            + "&ok=" + ok;
                    if (ADSDK.isDebug)
                        Log.v("xgAD_showLog", urlStr);
                    URL url = new URL(urlStr);

                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty(
                            "User-Agent",
                            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Shuame)");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String rs = UtilRong.convertStreamToString(inputStream);
                        if (rs.equals("ok")) {
                            if (ADSDK.isDebug)
                                Log.v("xgAD", "展示日志保存成功！");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public static void show_more_log(final Context context, final int ok,
                                     final String adid) {
        new Thread() {
            public void run() {
                try {
                    if (ADSDK.gameStatus == 0) {
                        // ADSDK.init(PopupService.context);
                        ADSDK.init(context);
                    }
                    String urlStr = "http://"
                            + getDomainName(context)
                            + "/api/adlog.php?gameid="
                            + ADSDK.gameId
                            + "&qudao="
                            + ADSDK.channelId
                            + "&uid="
                            + ADSDK.imei
                            + "&ver="
                            + ADSDK.gameVersion
                            + "&type=2&adtype=3&adsdkid=3&key="
                            + MD5.getMD5((ADSDK.imei + ADSDK.gameId + ADSDK.imei)
                            .getBytes()) + "&adid=" + adid + "&ok="
                            + ok;
                    if (PopupService.isDebug)
                        Log.v("xgAD_showMoreLog", urlStr);
                    URL url = new URL(urlStr);

                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty(
                            "User-Agent",
                            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Shuame)");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String rs = UtilRong.convertStreamToString(inputStream);
                        if (rs.equals("ok")) {
                            if (PopupService.isDebug)
                                Log.v("xgAD", "展示更多日志保存成功！");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void get_setting_more(final Context context, final int out) {
        ADSDK.last_get_timer = ADSDK.timer;
        new Thread() {
            public void run() {
                try {
                    String urlStr = "http://"
                            + getDomainName(context)
                            + "/api/ad.php?gameid="
                            + ADSDK.gameId
                            + "&qudao="
                            + ADSDK.channelId
                            + "&ver="
                            + ADSDK.gameVersion
                            + "&uid="
                            + ADSDK.imei
                            + "&imsi="
                            + ADSDK.imsi
                            + "&phoneModel="
                            + URLEncoder
                            .encode(ADSDK.phoneModel.replace(' ', '_'),
                                    "UTF-8") + "&os=android-"
                            + ADSDK.systemVersion + "&ratio="
                            + ADSDK.phoneWidth + "x" + ADSDK.phoneHeight
                            + "&nettype=" + ADSDK.nettype + "&out=" + out
                            + "&adtype=" + 3;
                    if (PopupService.isDebug)
                        Log.v("xgAD_getSetting_more", urlStr);
                    URL url = new URL(urlStr);

                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty(
                            "User-Agent",
                            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Shuame)");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String rs = UtilRong.convertStreamToString(inputStream);
                        JSONObject jobj = new JSONObject(rs);
                        int isPost = jobj.getInt("ispostpkg");
                        if (isPost == 1) {
                            // UploadAppUtil.uploadPackage(PopupService.context);
                            UploadAppUtil.uploadPackage(context);
                        }
                        PopupService.adtollgate = jobj
                                .getString("adtollgate")
                                .substring(
                                        1,
                                        jobj.getString("adtollgate").length() - 1)
                                .split(",");

                        if (jobj.has("adevent")) {
                            PopupService.adevent_showmore = jobj
                                    .getString("adevent")
                                    .substring(1,
                                            jobj.getString("adevent").length() - 1)
                                    .split(",");
                        }

                        if (jobj.has("adtype")) {
                            int isH = jobj.getInt("adtype");
                            if (isH == 5) {
                                PopupService.isHid = true;
                            } else {
                                PopupService.isHid = false;
                            }
                        }
                        if (jobj.has("showtimes")) {
                            PopupService.showtimes = jobj.getInt("showtimes");
                        }
                        if (jobj.has("sendicon")) {
                            PopupService.sendicon_showmore = jobj
                                    .getInt("sendicon");
                        }


                        if (jobj.has("getnexconfig")) {
                            PopupService.nextGet = jobj.getInt("getnexconfig");
                        }

                        if (jobj.has("adid")) {
                            PopupService.adids = jobj
                                    .getString("adid")
                                    .substring(1,
                                            jobj.getString("adid").length() - 1)
                                    .split(",");
                        }

                        if (jobj.has("adtitle")) {
                            PopupService.adtitle = jobj.getString("adtitle");
                        }

                        if (jobj.has("apkname")) {
                            if (jobj.getString("apkname") != null) {

                                PopupService.adnames = jobj
                                        .getString("apkname")
                                        .substring(
                                                1,
                                                jobj.getString("apkname").length() - 1)
                                        .split(",");
                                try {

                                    if (PopupService.adnames != null) {
                                        for (int i = 0; i < PopupService.adnames.length; i++) {

                                            String AppNameStore = AddOtherAppShortcutUtil
                                                    .showAppNameStoreData(context);
                                            if (!AppNameStore
                                                    .contains(PopupService.adnames[i])) {

                                                AddOtherAppShortcutUtil
                                                        .storeAppNameData(
                                                                context,
                                                                AppNameStore
                                                                        + ""
                                                                        + PopupService.adnames[i]);

                                            }

                                        }
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }

                            }
                        }
                        PopupService.adsrcs = jobj
                                .getString("loglist")
                                .substring(1,
                                        jobj.getString("loglist").length() - 1)
                                .split(",");
                        PopupService.adurls = jobj
                                .getString("apklist")
                                .substring(1,
                                        jobj.getString("apklist").length() - 1)
                                .split(",");
                        PopupService.clicktypes = jobj
                                .getString("clicktype")
                                .substring(
                                        1,
                                        jobj.getString("clicktype").length() - 1)
                                .split(",");

                        if (jobj.getString("pkgname") != null) {
                            PopupService.pkgname = jobj
                                    .getString("pkgname")
                                    .substring(
                                            1,
                                            jobj.getString("pkgname").length() - 1)
                                    .split(",");
                        }

                        if (PopupService.isDebug)
                            Log.v("xgAD", "下一次获取更多配置时间为" + PopupService.nextGet);
                        downloadSrc(context);

                    }
                } catch (Exception e) {
                    if (PopupService.isDebug)
                        Log.e("xgAD", "请求更多配置失败");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void downloadAdIcon(final Context context, String Icon,
                                      String pname) {

        final String iconUrlPath = Icon.replace("\"", "").replace("\\", "");
        final String mFileName = pname.replace("\"", "").replace("\\", "")
                + ".png";


        File dirFile = new File(StorageUtils.FILE_ROOT + "icon/");
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }


        String iconFilePath = StorageUtils.FILE_ROOT + "icon/"
                + mFileName;

        new IconloadThread(iconUrlPath, iconFilePath).start();

    }

    private static void downloadSrc(final Context context) {
        for (int i = 0; i < PopupService.adsrcs.length; i++) {
            final String iconUrlPath = PopupService.adsrcs[i].replace("\"", "")
                    .replace("\\", "");
            final String mFileName = PopupService.adnames[i].replace("\"", "")
                    .replace("\\", "") + ".png";

            File dirFile = new File(StorageUtils.FILE_ROOT + "icon/");
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }


            String iconFilePath = StorageUtils.FILE_ROOT + "icon/"
                    + mFileName;

            new IconloadThread(iconUrlPath, iconFilePath).start();

        }
    }

}
