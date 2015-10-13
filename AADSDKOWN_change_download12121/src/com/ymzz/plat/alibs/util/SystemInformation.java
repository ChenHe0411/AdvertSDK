package com.ymzz.plat.alibs.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.webkit.WebView;

public class SystemInformation {

	/**
	 * 1������Ϣ============================
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentNetType(Context context) {
		String type = "";
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null) {
//			type = "null";
			type = "0";
			
			
			
		} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
//			type = "wifi";
			type = "2";
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			int subType = info.getSubtype();
			if (subType == TelephonyManager.NETWORK_TYPE_CDMA
					|| subType == TelephonyManager.NETWORK_TYPE_GPRS
					|| subType == TelephonyManager.NETWORK_TYPE_EDGE) {
//				type = "2g";
				type = "4";
			} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
					|| subType == TelephonyManager.NETWORK_TYPE_HSDPA
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
//				type = "3g";
				type = "5";
			} else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE��3g��4g�Ĺ�ɣ���3.9G��ȫ���׼
//				type = "4g";
				type = "6";
			}
		}
		return type;
	}

	// public static String getIMEI(Context context) {
	//
	// String IMEI="";
	// TelephonyManager tm = (TelephonyManager) context
	// .getSystemService(Context.TELEPHONY_SERVICE);
	//
	// IMEI=tm.getDeviceId();
	// if (IMEI==null) {
	// IMEI="";
	// }
	// return IMEI;
	// }
	public static String getPhoneMessege(int flag, Context context) {
		String imei = "";
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		switch (flag) {
		/**
		 * IMEI
		 */

		case 0:
			imei = tm.getDeviceId();
			if (imei == null) {
				imei = "";
			}
			break;

		/**
		 * //MAC��ַ
		 */
		case 1:

			WifiManager wifiMgr = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);

			WifiInfo info = (null == wifiMgr ? null : wifiMgr
					.getConnectionInfo());
			if (null != info) {
				if (!TextUtils.isEmpty(info.getMacAddress()))
				
					imei = info.getMacAddress().replace(":", "");
				else
					return imei;
			}

			break;

		/**
		 * �ֻ��Ʒ��
		 * 
		 */

		case 2:
			imei = android.os.Build.BRAND;// �ֻ�Ʒ��

			break;

		/**
		 * �ֻ���ͺ�
		 * 
		 */
		case 3:

			imei = android.os.Build.MODEL; // �ֻ��ͺ�
			break;

		/**
		 * sdk�汾��
		 */

		case 4:

			imei = String.valueOf(Build.VERSION.SDK_INT);

			break;

		case 5:
			/**
			 * ������
			 * 
			 * 
			 */

			imei = android.os.Build.MANUFACTURER;
			break;
		case 6:
			/**
			 * ϵͳ�汾
			 * 
			 * 
			 */

			imei = android.os.Build.VERSION.RELEASE;
			break;


		default:
			break;
		}

		return imei;
	}

	
	/***
	 * ��ȡ������Ӫ��
	 * @param context
	 * @return
	 */
	public static String getOperatorName(Context context) {

		String operatorName = "";
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = tm.getSimOperator();
		if (operator != null) {

			if (operator.equals("46000") || operator.equals("46002")
					|| operator.equals("46007")) {
				// �ƶ�
				 operatorName = "�й��ƶ�";
			} else if (operator.equals("46001")) {
				// ��ͨ
				 operatorName = "�й���ͨ";
			} else if (operator.equals("46003")) {
				// ����
				 operatorName = "�й����";
			}
		}else{
			 operatorName = "";
		}
		
		return operatorName;

	}
	
	/**
	 * ��ȡ User-Agent
	 * 
	 * @return
	 */
	public static String getUA(Context mContext) {
		if (mContext == null) {
			return "";
		}
		String result = "";
		try {
			result = new WebView(mContext).getSettings().getUserAgentString();
		} catch (Exception e) {
			// e.printStackTrace();
			return "";
		}
		return result;
	}
	
	

	// /**
	// * ��ȡ�ֻ�mac��ַ
	// *
	// */
	// public static String getMacAddress(Context context) {
	// // ��ȡmac��ַ��
	// String macAddress = "000000000000";
	// try {
	// WifiManager wifiMgr = (WifiManager) context
	// .getSystemService(Context.WIFI_SERVICE);
	// WifiInfo info = (null == wifiMgr ? null : wifiMgr
	// .getConnectionInfo());
	// if (null != info) {
	// if (!TextUtils.isEmpty(info.getMacAddress()))
	// macAddress = info.getMacAddress().replace(":", "");
	// else
	// return macAddress;
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return macAddress;
	// }
	// return macAddress;
	// }

	// /**
	// * �õ���Ӫ�̴���
	// *
	// * @param context
	// * @return
	// */
	// public static String getOperatorCode(Context mContext) {
	// if (mContext == null) {
	// return "";
	// }
	// // ���ֻ�������
	// TelephonyManager tm = (TelephonyManager)
	// mContext.getSystemService(Context.TELEPHONY_SERVICE);
	// // ��ȡ��Ӫ�̴���
	// return tm.getNetworkOperator();
	// }
}
