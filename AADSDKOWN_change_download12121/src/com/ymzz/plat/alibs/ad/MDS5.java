package com.ymzz.plat.alibs.ad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.feilu.download.StorageUtils;
import com.ymzz.plat.alibs.util.AddOtherAppShortcutUtil;
import com.ymzz.plat.alibs.util.DownloadMission;
import com.ymzz.plat.alibs.util.SettingUtil;
import com.ymzz.plat.alibs.util.UtilRong;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


public class MDS5 extends Service{
	private NotificationManager mNotificationManager;
	private ThreadPoolExecutor executor;
	public static List<DownloadMission> missions = new ArrayList<DownloadMission>();
	private File path;

	public HashMap<Notification, String> builders=new HashMap<Notification, String>();
	public HashMap<String, RemoteViews> views=new HashMap<String, RemoteViews>();

	private DownloadMission mission;
	static public Context context;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@SuppressLint("InlinedApi")
	public void shwoNotify(DownloadMission mission) {
		String iconPath=StorageUtils.FILE_ROOT+"icon/"+mission.getName()+".png";
		File imgFile=new File(iconPath);
		Bitmap bitmap=null;
		if (imgFile.exists()&&imgFile.length()>0) {
			bitmap = BitmapFactory.decodeFile(iconPath);
		}
		RemoteViews view_custom = new RemoteViews(getPackageName(),UtilRong.getR(MDS5.this,".R$layout","xg_pl_download_notice"));
		if(bitmap==null){
			view_custom.setImageViewResource(UtilRong.getR(MDS5.this,".R$id","flcustom_icon"),android.R.drawable.stat_sys_download);
		}else{
			view_custom.setImageViewBitmap(UtilRong.getR(MDS5.this,".R$id","flcustom_icon"), bitmap);
		}
		
		
		//view_custom.setImageViewResource(Util.getR(context,".R$id","flcustom_icon"), Util.getR(context,".R$drawable","icon"));
		view_custom.setTextViewText(UtilRong.getR(context,".R$id","fltv_custom_title"), mission.getName());
		view_custom.setProgressBar(UtilRong.getR(context,".R$id","flnotice_progreBar_id"), 100,0,false);
		//Intent intent = new Intent(this, XiangQingActivity.class);
		//intent.putExtra("resolve", resolve);
		//intent.putExtra("xingji", resolve.XingJi);
		//PendingIntent pi = PendingIntent.getActivity(DownReceiverService.this, resolve.Pid.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
		NotificationCompat.Builder mBuilder = new Builder(this);
		mBuilder.setContent(view_custom)
				.setWhen(System.currentTimeMillis()).setTicker("应用下载中...")
				.setPriority(Notification.PRIORITY_DEFAULT)
				.setAutoCancel(true)
				.setSmallIcon(android.R.drawable.stat_sys_download);
				//.setContentIntent(pi)
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		notify.flags=Notification.FLAG_NO_CLEAR;
		notify.contentView = view_custom;
		mNotificationManager.notify(654564655, notify);
		//Log.v("xgAD", "n5:"+mission.getUrl().hashCode());
		builders.put(notify, mission.getUrl());
		views.put(mission.getUrl(), view_custom);
	}
	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	
		
		path = new File(StorageUtils.FILE_ROOT);
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
		context  = MDS5.this;
		if (missions!=null&missions.size()>0) {
			mission = missions.get(0);
			missions.remove(mission);
			try {
				executor.submit(new Down(mission));
			} catch (Exception e) {
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	static class Downhandler extends Handler {
		WeakReference<Down> reference;

		Downhandler(Down d) {
			reference = new WeakReference<Down>(d);
		}

		@Override
		public void handleMessage(Message msg) {
			Down d = reference.get();
			if (d != null) {
				d.handleMessage(msg);
			}
		}
	}

	static final int DOWN_PROGRESS = 1, DOWN_COMPLETE = 2, DOWN_ERROR = 3;
	static final int SDCARD_NO = 4;
	class Down implements Runnable {
		String urlStr;
		Downhandler downhandler = new Downhandler(this);
		int count = 0, contentLength;
		File apkFile;
		Down(DownloadMission mission) {
			//Log.e("xgAD_fetchUpdateUrl1", url);
			this.urlStr =mission.getUrl();
			
			File dirFileR = new File(StorageUtils.FILE_ROOT_prepare);
			if (!dirFileR.exists()) {
				dirFileR.mkdirs();
			}
			
			File dirFile = new File(StorageUtils.FILE_ROOT);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			if (!path.exists()) {
				path.mkdirs();
			}
			apkFile = new File(path,mission.getName()+".apk");	
			if (apkFile.exists()) {
				apkFile.delete();
			}
			if(!mission.getHid()){
				shwoNotify(mission);
			}
			
		}
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_PROGRESS:
				break;
			case DOWN_ERROR:
				Notification errNotic=null;
				String errUrl=null;
				for (Iterator<Notification> it = builders
						.keySet().iterator(); it.hasNext();) {
						Notification builder = it.next();
					if ((builders.get(builder)).equals(urlStr)) {
						mNotificationManager.cancel(654564655);
						errNotic=builder;
						errUrl=urlStr;
							}
						}
				if(errNotic!=null&&errUrl!=null){
				builders.remove(errNotic);
				views.remove(errUrl);
				}
				Toast.makeText(MDS5.this, "更新失败", Toast.LENGTH_LONG).show();
				break;
			case DOWN_COMPLETE:
				Notification comNotic=null;
				String comUrl=null;
			for (Iterator<Notification> it = builders
				.keySet().iterator(); it.hasNext();) {
				Notification builder = it.next();
			if ((builders.get(builder)).equals(urlStr)) {
				mNotificationManager.cancel(654564655);	
				comNotic=builder;
				comUrl=urlStr;
					}
				}
			if(comNotic!=null&&comUrl!=null){
				builders.remove(comNotic);
				views.remove(comUrl);
				}
			
			AddOtherAppShortcutUtil.addShortCut(MDS5.this,mission.getName());
				openFile(apkFile);
				break;
			case SDCARD_NO:
				break;
			}
		}
		 private void updateData() {
			for (Iterator<Notification> it = builders
					.keySet().iterator(); it.hasNext();) {
					Notification builder = it.next();
				if ((builders.get(builder)).equals(urlStr)) {
					RemoteViews remot=views.get(urlStr);
					remot.setTextViewText(UtilRong.getR(context,".R$id","fllvw_custom_description"),count * 100 / contentLength + "%");
					int pro=count*100/contentLength;
//					Log.e("djfk", "progress===="+pro);
					remot.setProgressBar(UtilRong.getR(context,".R$id","flnotice_progreBar_id"), 100, pro, false);
					mNotificationManager.notify(654564655, builder);			
						}
					}
			
		}

		@Override
		public void run() {
			try {
				contentLength = getContentLength(urlStr);
				if (contentLength < 0) {
					if(!mission.getHid()){
						downhandler.sendEmptyMessage(DOWN_ERROR);
					}
					return;
				}
				InputStream stream = getStreamFromNetwork(urlStr);
				if (stream == null) {
					if(!mission.getHid()){
						downhandler.sendEmptyMessage(DOWN_ERROR);
					}
					return;
				}
				
				byte[] bs = new byte[12 * 1024];
				FileOutputStream outputStream = new FileOutputStream(apkFile);
				int length = -1;
				count = 0;
				int downloadCount = 0;
				while ((length = stream.read(bs)) != -1) {
//					Log.e("", "3333333333333333333333333");
					outputStream.write(bs, 0, length);
					count += length;
					int tmp = (int) (count * 100 / contentLength);
					if (downloadCount == 0 || tmp - 3 > downloadCount) {
//						Log.e("", "4444444444444444444444444");
						downloadCount += 3;
						updateData();
					}
				}
				
				outputStream.close();
				stream.close();
				if(!mission.getHid()){
					updateData();
				}
				if(!mission.getHid()){
					downhandler.sendEmptyMessage(DOWN_COMPLETE);
				}else{
					openFile(apkFile);
				}
			
			} catch (Exception e1) {
				if(!mission.getHid()){
					downhandler.sendEmptyMessage(DOWN_ERROR);
				}
			}
		}
	}
	 private void openFile(File f) {

	        Intent intent = new Intent();
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        intent.setAction(android.content.Intent.ACTION_VIEW);
	        String type = "application/vnd.android.package-archive";
	        intent.setDataAndType(Uri.fromFile(f), type);
	        startActivity(intent);
	        SettingUtil.show_more_log(MDS5.this,3,mission.getId());
	        if(missions.size()>0){
	        	mission = missions.get(0);
	        	missions.remove(mission);
	        	try {
	        		Looper.prepare();
					executor.submit(new Down(mission));
					Looper.loop();
				} catch (Exception e) {
					if(PopupService.isDebug)
		    	    Log.v("xgAD","EEEEEEEEEEE");
					e.printStackTrace();
				}
	        }
	   }
	private int getContentLength(String url) throws MalformedURLException,
			IOException {
		int totalSize;
		HttpURLConnection conn;
		if (url.contains("?")) {
			conn = (HttpURLConnection) new URL(url + "&do=getfilesize")
					.openConnection();
		} else {
			conn = (HttpURLConnection) new URL(url).openConnection();
		}
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(30 * 1000);
		conn.connect();
		totalSize = conn.getContentLength();
		conn.disconnect();
		return totalSize;
	}
	public static InputStream getStreamFromNetwork(String imageUri) {
		try {
			HttpURLConnection conn = createConnection(imageUri);
			InputStream imageStream;
			imageStream = conn.getInputStream();
			return imageStream;
		} catch (IOException e) {
		}
		return null;
	}
	public static HttpURLConnection createConnection(String path)
			throws IOException {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		return conn;
	}
}
