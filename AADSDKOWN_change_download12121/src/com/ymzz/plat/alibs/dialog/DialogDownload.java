package com.ymzz.plat.alibs.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feilu.download.StorageUtils;
import com.ymzz.plat.alibs.download.AsyncDownLoadManager;
import com.ymzz.plat.alibs.download.WebResource;
import com.ymzz.plat.alibs.util.GetResourseIdSelf;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

/**
 * 广告下载
 */
public class DialogDownload extends Dialog {
    /**
     * 上下文
     */
    private Activity activity;
    /**
     * 进度条
     */
    private ProgressBar dialog_download_progressbar;

    /**
     * 下载进度描述
     */
    private TextView textview_dialog_progress;
    /**
     * 下载进度0~100
     */
    int currentProgress = 0;
    /**
     * 下载管理器
     */
    private AsyncDownLoadManager downLoadManager;
    /**
     * 下载的apk文件
     */
    private File appFile;
    /**
     * 文件类
     */
    private WebResource mResource;

    public DialogDownload(String apkPath, Activity activity) {
        super(activity, GetResourseIdSelf.getResourseIdByName(
                activity.getPackageName(), "style", "dialog_normal"));
        this.activity = activity;
        if (apkPath != null || "".equals(apkPath)) {
            initDate(apkPath);
        }
    }

    private void initDate(final String apkPath) {
        downLoadManager = AsyncDownLoadManager.getAsyncManager(activity);
        //路径设置
        mResource = new WebResource();
        File file = creatSDDir();
        mResource.filePath = file.getAbsolutePath() + "/";
        mResource.url = apkPath;
        mResource.fileName = apkPath.substring(apkPath.lastIndexOf("/") + 1);
        appFile = new File(mResource.filePath + mResource.fileName);
        if (appFile.exists()) {
            //判断是否存在apk
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL u = new URL(apkPath);
                        URLConnection urlcon = u.openConnection();
                        int appLength = urlcon.getContentLength();
                        long fileLength = appFile.length();
                        if (appLength == fileLength) {
                            close();
                            //打开
                            openFile(appFile);
                        } else {
                            //下载
                            handler.sendEmptyMessage(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int resourseId = GetResourseIdSelf.getResourseIdByName(
                    activity.getPackageName(), "layout", "dialog_download");
            View menuView = LayoutInflater.from(activity).inflate(resourseId, null);
            setContentView(menuView);
            int dialog_downloadId = GetResourseIdSelf.getResourseIdByName(
                    activity.getPackageName(), "id", "dialog_download");
            RelativeLayout dialog_download = (RelativeLayout) menuView
                    .findViewById(dialog_downloadId);
            int dialog_download_progressbarId = GetResourseIdSelf.getResourseIdByName(
                    activity.getPackageName(), "id", "dialog_download_progressbar");
            dialog_download_progressbar = (ProgressBar) menuView.findViewById(dialog_download_progressbarId);
            int textview_dialog_progressId = GetResourseIdSelf.getResourseIdByName(
                    activity.getPackageName(), "id", "textview_dialog_progress");
            textview_dialog_progress = (TextView) menuView
                    .findViewById(textview_dialog_progressId);
            downLoadManager.addDownTask(mResource, onDownLoadListener);
        }
    };

    @Override
    public void show() {
        if (activity != null && !activity.isFinishing()) {
            super.show();
        }

    }

    public void close() {
        if (downLoadManager != null) {
            downLoadManager.cancelAll();
            downLoadManager = null;
        }
        dismiss();
    }


    /**
     * 在sd卡上创建文件夹（目录）
     *
     */
    private File creatSDDir() {
        File dir = new File(StorageUtils.FILE_ROOT);
        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            if (!dir.isDirectory()) {
                dir.delete();
                dir.mkdirs();
            }
        }
        return dir;
    }

    /**
     * 下载监听
     */
    AsyncDownLoadManager.OnDownLoadListener onDownLoadListener = new AsyncDownLoadManager.OnDownLoadListener() {

        @Override
        public void onUpdataDownLoadProgross(WebResource Resource, int progross) {
            if (progross > currentProgress) {
                if (textview_dialog_progress != null || dialog_download_progressbar != null) {
                    textview_dialog_progress.setText("已下载" + progross + "%");
                    dialog_download_progressbar.setProgress(progross);
                    currentProgress = progross;
                }

            }
        }

        public void onFinshDownLoad(WebResource mResource) {
            close();
            openFile(appFile);
        }

        @Override
        public void onError(String error) {
            close();
            Toast.makeText(activity.getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        }

    };

    private void openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        activity.startActivity(intent);
    }

    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg")
                || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg")
                || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            type = "*";
        }
        if (end.equals("apk")) {
        } else {
            type += "/*";
        }
        return type;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return false;

    }

}
