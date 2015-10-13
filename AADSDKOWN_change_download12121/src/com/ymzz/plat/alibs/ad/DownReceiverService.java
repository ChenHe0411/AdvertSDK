package com.ymzz.plat.alibs.ad;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;

import com.feilu.download.AllDownList;
import com.feilu.download.DownloadManager;
import com.feilu.download.DownloadTask;
import com.feilu.download.MyIntents;
import com.feilu.download.StorageUtils;
import com.feilu.utilmy.ClassifyDataHelper;
import com.feilu.utilmy.Contant;
import com.feilu.utilmy.MyGet_1;
import com.feilu.utilmy.RecommendItem;
import com.feilu.utilmy.Util;
import com.guad.sdk.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.ymzz.plat.alibs.util.AddOtherAppShortcutUtil;
import com.ymzz.plat.alibs.util.ScreenOpenLoad;
import com.ymzz.plat.alibs.util.SettingUtil;
import com.ymzz.plat.alibs.util.UtilRong;

public class DownReceiverService extends Service {
	private DownReceiver receiver;
	public static final String ACTION = "com.feilu.download.DownReceiverService";
	private File cache;

	protected ClassifyDataHelper picDataHelper = null;
	public Dao<RecommendItem, Long> resolveDao;
	public AllDownList allDownList = AllDownList.getInstance();
	public DownloadManager downloadManager;
	private NotificationManager mNotificationManager;
	private NumberFormat format1 = NumberFormat.getNumberInstance();
	public HashMap<Notification, RecommendItem> builders = new HashMap<Notification, RecommendItem>();
	public HashMap<String, RemoteViews> views = new HashMap<String, RemoteViews>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 根据图片的url路径获得Bitmap对象
	 * 
	 * @return
	 */
	// private Bitmap returnBitmap(String url) {
	// URL fileUrl = null;
	// Bitmap bitmap = null;
	//
	// try {
	// fileUrl = new URL(url);
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	//
	// try {
	// HttpURLConnection conn = (HttpURLConnection) fileUrl
	// .openConnection();
	// conn.setDoInput(true);
	// conn.connect();
	// InputStream is = conn.getInputStream();
	// bitmap = BitmapFactory.decodeStream(is);
	// is.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return bitmap;
	//
	// }
	@SuppressLint("InlinedApi")
	public void shwoNotify(RecommendItem resolve) {

		String iconPath = StorageUtils.FILE_ROOT + "icon/" + resolve.PName
				+ ".png";
		File imgFile = new File(iconPath);
		Bitmap bitmap = null;
		if (imgFile.exists() && imgFile.length() > 0) {
			bitmap = BitmapFactory.decodeFile(iconPath);
		}
		RemoteViews view_custom = new RemoteViews(getPackageName(),
				UtilRong.getR(DownReceiverService.this, ".R$layout",
						"xg_pl_download_notice"));
		if (bitmap == null) {
			view_custom.setImageViewResource(UtilRong.getR(
					DownReceiverService.this, ".R$id", "flcustom_icon"),
					android.R.drawable.stat_sys_download);
		} else {
			view_custom
					.setImageViewBitmap(UtilRong.getR(DownReceiverService.this,
							".R$id", "flcustom_icon"), bitmap);
		}
		view_custom.setTextViewText(UtilRong.getR(DownReceiverService.this,
				".R$id", "fltv_custom_title"), resolve.PName);
		view_custom.setProgressBar(UtilRong.getR(DownReceiverService.this,
				".R$id", "flnotice_progreBar_id"), 100, 0, true);

		// Intent intent = new Intent(this, BoutiqueAct.class);
		// intent.putExtra("resolve", resolve);
		// intent.putExtra("xingji", resolve.XingJi);
		// PendingIntent pi =
		// PendingIntent.getActivity(DownReceiverService.this,
		// resolve.Pid.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
		NotificationCompat.Builder mBuilder = new Builder(this);
		mBuilder.setContent(view_custom).setWhen(System.currentTimeMillis())
				.setTicker(resolve.PName)
				.setPriority(Notification.PRIORITY_DEFAULT).setAutoCancel(true)
				.setTicker(resolve.PName)
				.setSmallIcon(android.R.drawable.stat_sys_download);
		// .setContentIntent(pi);
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		notify.flags = Notification.FLAG_NO_CLEAR;
		notify.contentView = view_custom;
		mNotificationManager.notify(resolve.Pid.hashCode(), notify);
		builders.put(notify, resolve);
		views.put(resolve.Pid, view_custom);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		receiver = new DownReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Contant.FIRST_RECEIVER_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, filter);

		registerListener();

		downloadManager = DownloadManager.getInstance(this);
		try {
			resolveDao = getPicHelper().getDao(RecommendItem.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		fangWenThread();
	}

	/***
	 * 
	 * @author feilu-pc
	 * 
	 */
	private ScreenBroadcastReceiver mScreenReceiver;

	public void unregisterListener() {
		DownReceiverService.this.unregisterReceiver(mScreenReceiver);
	}

	private void registerListener() {
		mScreenReceiver = new ScreenBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		DownReceiverService.this.registerReceiver(mScreenReceiver, filter);
	}

	private class ScreenBroadcastReceiver extends BroadcastReceiver {
		private String action = null;

		@Override
		public void onReceive(final Context context, Intent intent) {
			action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏

				// System.out.println("开屏");
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
			// System.out.println("锁屏");
			} else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
			// System.out.println("解锁");
				new Thread() {

					public void run() {
						ScreenOpenLoad.currentPath(context);

					};
				}.start();

			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterListener();
		unregisterReceiver(receiver);
		ArrayList<RecommendItem> downloadingItems = allDownList.downloadingItems;
		for (int i = 0; i < downloadingItems.size(); i++) {
			try {
				RecommendItem db = downloadingItems.get(i);
				if (db.status != MyIntents.Types.COMPLETE) {
					db.status = MyIntents.Types.PAUSE;
					resolveDao.update(db);
				}
			} catch (Exception e) {
			}
		}

		Intent localIntent = new Intent();
		localIntent.setClass(this, DownReceiverService.class);
		this.startService(localIntent);

	}

	// private void openFile(File file,RecommendItem item) {
	// Intent intent = new Intent();
	// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// intent.setAction(android.content.Intent.ACTION_VIEW);
	// intent.setDataAndType(Uri.fromFile(file),
	// "application/vnd.android.package-archive");
	// startActivity(intent);
	// SettingUtil.show_more_log(DownReceiverService.this,3,item.ItemId);
	// }

	private class DownReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			handleIntent(intent);
		}

		private void handleIntent(Intent intent) {
			if (intent != null
					&& Contant.FIRST_RECEIVER_ACTION.equals(intent.getAction())) {
				int type = intent.getIntExtra(MyIntents.TYPE, -1);
				final RecommendItem baseItem = intent
						.getParcelableExtra("resolve");
				switch (type) {
				case MyIntents.Types.ADD:
					shwoNotify(baseItem);
					try {
						baseItem.status = MyIntents.Types.ADD;
						resolveDao.createIfNotExists(baseItem);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					break;
				case MyIntents.Types.COMPLETE:
					for (int i = 0; i < allDownList.downloadingItems.size(); i++) {
						RecommendItem resolve = allDownList.downloadingItems
								.get(i);
						if (resolve.Pid.equals(baseItem.Pid)) {
							resolve.status = MyIntents.Types.COMPLETE;
							resolve.downloadPercent = 100;
							allDownList.complete(resolve);

						}
					}

					mNotificationManager.cancel(baseItem.Pid.hashCode());
					deleNotification(baseItem);
					break;
				case MyIntents.Types.PROCESS:
					for (int i = 0; i < allDownList.downloadingItems.size(); i++) {
						RecommendItem resolve = allDownList.downloadingItems
								.get(i);
						if ((resolve.Pid).equals(baseItem.Pid)) {
							resolve.status = MyIntents.Types.PROCESS;
							resolve.downloadPercent = baseItem.downloadPercent;
							resolve.downloadSize = baseItem.downloadSize;

							// System.out.println("resolve.downloadPercent---"+resolve.downloadPercent+"");

							if (resolve.downloadPercent > 100) {

								resolve.status = MyIntents.Types.DEFAULT;
								resolve.downloadPercent = 0;
								resolve.downloadSize = 0;

								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								File file = new File(StorageUtils.FILE_ROOT
										+ "/" + baseItem.PName + ".apk");
								if (file.exists()) {
									file.delete();
								}
							}
						}
					}
					break;
				case MyIntents.Types.PAUSE:
					for (int i = 0; i < allDownList.downloadingItems.size(); i++) {
						RecommendItem resolve = allDownList.downloadingItems
								.get(i);
						if ((resolve.Pid).equals(baseItem.Pid)) {
							resolve.status = MyIntents.Types.PAUSE;
						}
					}
					mNotificationManager.cancel(baseItem.Pid.hashCode());
					try {
						baseItem.status = MyIntents.Types.PAUSE;
						resolveDao.update(baseItem);

					} catch (Exception e) {
					}
					break;
				case MyIntents.Types.DELETE:
					for (int i = 0; i < allDownList.downloadingItems.size(); i++) {
						RecommendItem resolve = allDownList.downloadingItems
								.get(i);
						if ((resolve.Pid).equals(baseItem.Pid)) {
							resolve.status = MyIntents.Types.DEFAULT;
							resolve.downloadPercent = 0;
							resolve.downloadSize = 0;
							allDownList.deleteDownloading(resolve);
						}
					}
					try {
						for (Iterator<Notification> it = builders.keySet()
								.iterator(); it.hasNext();) {
							Notification builder = it.next();
							if ((builders.get(builder).Pid)
									.equals(baseItem.Pid)) {
								builders.remove(builder);
								views.remove(baseItem.Pid);
							}
						}
						resolveDao.delete(baseItem);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					mNotificationManager.cancel(baseItem.Pid.hashCode());
					break;
				case MyIntents.Types.ERROR:
					for (int i = 0; i < allDownList.downloadingItems.size(); i++) {
						RecommendItem resolve = allDownList.downloadingItems
								.get(i);
						if ((resolve.Pid).equals(baseItem.Pid)) {
							resolve.status = MyIntents.Types.ERROR;
						}
					}
					mNotificationManager.cancel(baseItem.Pid.hashCode());
					try {
						baseItem.status = MyIntents.Types.PAUSE;
						resolveDao.update(baseItem);
					} catch (Exception e) {
					}
					break;
				}
				Intent bro = new Intent();
				bro.setAction(Contant.SECOND_RECEIVER_ACTION);
				bro.putExtra(MyIntents.TYPE, type);
				bro.putExtra("resolve", baseItem);
				sendBroadcast(bro);
			}
		}
	}

	public void deleNotification(final RecommendItem baseItem) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					File file = new File(StorageUtils.FILE_ROOT + "/"
							+ baseItem.PName + ".apk");
					resolveDao.update(baseItem);
					AddOtherAppShortcutUtil.openFile(DownReceiverService.this,
							baseItem);

					
					if (PopupService.showmore_type.equals(baseItem.XingJi)
						) {
						 SettingUtil.show_more_log(DownReceiverService.this,3,baseItem.ItemId);
						
						
						
					}else  if (PopupService.ad_type.equals(baseItem.XingJi)
							) {
						 SettingUtil.show_log(DownReceiverService.this,3,Integer.parseInt(baseItem.ItemId));
					}else if (PopupService.h5ad_type.equals(baseItem.XingJi)
							) {
						/***  h5页面ItemId为adlidid**/
						SettingUtil.show_H5page_log(DownReceiverService.this,3,Integer.parseInt(baseItem.ItemId));
						
						
					}
					try {

//						if (PopupService.showmore_type.equals(baseItem.XingJi)
//								&& PopupService.sendicon_showmore == 1) {
//							AddOtherAppShortcutUtil.addShortCut(
//									DownReceiverService.this, baseItem.PName);
//						}
//						/***广告**/
//						if (PopupService.ad_type.equals(baseItem.XingJi)
//								&& ADSDK.sendicon_ad == 1) {
//							AddOtherAppShortcutUtil.addShortCut(
//									DownReceiverService.this, baseItem.PName);
//						}

					} catch (Exception e) {
						// TODO: handle exception
					}

					for (Iterator<Notification> it = builders.keySet()
							.iterator(); it.hasNext();) {
						Notification builder = it.next();
						if ((builders.get(builder).Pid).equals(baseItem.Pid)) {
							builders.remove(builder);
							views.remove(baseItem.Pid);
						}
					}

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}.start();
	}

	public void fangWenThread() {
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				try {
					List<DownloadTask> list = downloadManager.mDownloadingTasks;
					for (int i = 0; i < list.size(); i++) {
						RecommendItem baseItem = list.get(i).getBaseItem();
						for (Iterator<Notification> it = builders.keySet()
								.iterator(); it.hasNext();) {
							Notification builder = it.next();
							if ((builders.get(builder).Pid)
									.equals(baseItem.Pid)) {
								double ds = (double) baseItem.downloadSize
										/ (1024 * 1024);
								String s1 = format1.format(ds);
								RemoteViews remot = views.get(baseItem.Pid);

								double total_M = (double) baseItem.totalSize
										/ (1024 * 1024);
								String s1_total_M = format1.format(total_M);
								// remot.setTextViewText(R.id.lvw_custom_description,s1+"M/"+baseItem.FileSize+"M");
								// remot.setTextViewText(R.id.lvw_custom_description,s1+"M/"+s1_total_M+"M");

								if (baseItem.totalSize != 0) {
									int progress = (int) (baseItem.downloadSize
											/ (double) baseItem.totalSize * 100);

									// System.out.println("progress---"+progress);
									// remot.setTextViewText(UtilRong.getR(DownReceiverService.this,".R$id","fllvw_custom_description"),s1+"M/"+s1_total_M+"M");
									remot.setTextViewText(UtilRong.getR(
											DownReceiverService.this, ".R$id",
											"fllvw_custom_description"),
											progress + "%");
									remot.setProgressBar(UtilRong.getR(
											DownReceiverService.this, ".R$id",
											"flnotice_progreBar_id"), 100,
											progress, false);
								} else {

									remot.setTextViewText(UtilRong.getR(
											DownReceiverService.this, ".R$id",
											"fllvw_custom_description"), s1
											+ "M");

								}

								mNotificationManager.notify(
										baseItem.Pid.hashCode(), builder);
							}
						}
					}
				} catch (Exception e) {
				}
			}
		};
		timer.schedule(timerTask, 0, 1000);
	}

	public ClassifyDataHelper getPicHelper() {
		if (picDataHelper == null) {
			picDataHelper = OpenHelperManager.getHelper(this,
					ClassifyDataHelper.class);
		}
		return picDataHelper;
	}
}
