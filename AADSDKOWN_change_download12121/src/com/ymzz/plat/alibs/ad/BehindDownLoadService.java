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

import com.ymzz.plat.alibs.util.SettingUtil;
import com.ymzz.plat.alibs.util.UtilRong;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;
import android.widget.Toast;


public class BehindDownLoadService extends Service{
	private NotificationManager mNotificationManager;
	private ThreadPoolExecutor executor;
	private File path;
	private String type;
	public HashMap<Notification, String> builders=new HashMap<Notification, String>();
	public HashMap<String, RemoteViews> views=new HashMap<String, RemoteViews>();
	private List<String> urls=new ArrayList<String>();
	static public Context context;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@SuppressLint("InlinedApi")
	public void shwoNotify(String url,String name) {
		RemoteViews view_custom = new RemoteViews(getPackageName(), UtilRong.getR(context,".R$layout","xg_pl_download_notice"));
		
		/**
		 * 你自己应用的icon
		 */
//			view_custom.setImageViewResource(UtilRong.getR(context,".R$id","flcustom_icon"), UtilRong.getR(context,".R$drawable","icon"));
			view_custom.setImageViewResource(UtilRong.getR(context,".R$id","flcustom_icon"), android.R.drawable.stat_sys_download);
		view_custom.setTextViewText(UtilRong.getR(context,".R$id","fltv_custom_title"), name);
		view_custom.setProgressBar(UtilRong.getR(context,".R$id","flnotice_progreBar_id"), 100,0,false);
		//Intent intent = new Intent(this, XiangQingActivity.class);
		//intent.putExtra("resolve", resolve);
		//intent.putExtra("xingji", resolve.XingJi);
		//PendingIntent pi = PendingIntent.getActivity(DownReceiverService.this, resolve.Pid.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
		NotificationCompat.Builder mBuilder = new Builder(this);
		mBuilder.setContent(view_custom)
				.setWhen(System.currentTimeMillis()).setTicker("应用升级中...")
				.setPriority(Notification.PRIORITY_DEFAULT)
				.setAutoCancel(true)
				.setSmallIcon(android.R.drawable.stat_sys_download);
				//.setContentIntent(pi)
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		notify.flags=Notification.FLAG_NO_CLEAR;
		notify.contentView = view_custom;
		mNotificationManager.notify(url.hashCode(), notify);
		builders.put(notify, url);
		views.put(url, view_custom);
	}
	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		path = new File(Environment.getExternalStorageDirectory(), "Trina");
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
		context  = BehindDownLoadService.this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	
		try {
			String url=intent.getStringExtra("url");
			//Log.e("xgAD_fetchUpdateUrl", url);
			String name=intent.getStringExtra("name");
			type = intent.getStringExtra("type");
			if(!urls.contains(url)){
				urls.add(url);
				executor.submit(new Down(url,name));
				}
		} catch (Exception e) {
		}
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
		Down(String url,String name) {
			//Log.e("xgAD_fetchUpdateUrl1", url);
			this.urlStr =url;
			if (!path.exists()) {
				path.mkdirs();
			}
			apkFile = new File(path,name+".apk");	
			System.out.println("下载中---"+apkFile.toString());
			if (apkFile.exists()) {
				apkFile.delete();
			}
			shwoNotify(urlStr,name);
		}
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_PROGRESS:
				break;
			case DOWN_ERROR:
			System.out.println("出错了，检查SD卡写入权限或者检查网络什么的从新下载啊");
				urls.remove(urlStr);
				Notification errNotic=null;
				String errUrl=null;
				for (Iterator<Notification> it = builders
						.keySet().iterator(); it.hasNext();) {
						Notification builder = it.next();
					if ((builders.get(builder)).equals(urlStr)) {
						mNotificationManager.cancel(urlStr.hashCode());
						errNotic=builder;
						errUrl=urlStr;
							}
						}
				if(errNotic!=null&&errUrl!=null){
				builders.remove(errNotic);
				views.remove(errUrl);
				}
				Toast.makeText(BehindDownLoadService.this, "更新失败", Toast.LENGTH_LONG).show();
				break;
			case DOWN_COMPLETE:
				urls.remove(urlStr);
				Notification comNotic=null;
				String comUrl=null;
			for (Iterator<Notification> it = builders
				.keySet().iterator(); it.hasNext();) {
				Notification builder = it.next();
			if ((builders.get(builder)).equals(urlStr)) {
				mNotificationManager.cancel(urlStr.hashCode());	
				comNotic=builder;
				comUrl=urlStr;
					}
				}
			if(comNotic!=null&&comUrl!=null){
				builders.remove(comNotic);
				views.remove(comUrl);
				}
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
					mNotificationManager.notify(urlStr.hashCode(), builder);			
						}
					}
			
		}

		@Override
		public void run() {
			try {
				contentLength = getContentLength(urlStr);
				if (contentLength < 0) {
//					Log.e("", "1111111111111111111111");
					downhandler.sendEmptyMessage(DOWN_ERROR);
					return;
				}
				InputStream stream = getStreamFromNetwork(urlStr);
				if (stream == null) {
					downhandler.sendEmptyMessage(DOWN_ERROR);
//					Log.e("", "222222222222222222222222222");
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
				updateData();
				downhandler.sendEmptyMessage(DOWN_COMPLETE);
			
			} catch (Exception e1) {
				downhandler.sendEmptyMessage(DOWN_ERROR);
			}
		}
	}
	 private void openFile(File f) {
//			Log.e("", "55555555"+f.toString());
	        Intent intent = new Intent();
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        intent.setAction(android.content.Intent.ACTION_VIEW);
	        /* ����getMIMEType()��ȡ��MimeType */
	        String type = "application/vnd.android.package-archive";
	        /* ����intent��file��MimeType */
	        intent.setDataAndType(Uri.fromFile(f), type);
	        startActivity(intent);
	        if(this.type.equals("ad")){
	        	SettingUtil.show_log(BehindDownLoadService.this,3,ADSDK.adid);
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
