package com.ymzz.plat.alibs.zwu;
import android.util.Log;

/**
 * 日志�?
 * @author zouwei
 *
 */
public class MyLogcat {
//	static boolean islog = true; //发布时改为false 屏蔽log打印�?
	static boolean islog = false; //发布时改为false 屏蔽log打印�?
	static public void log(String s){
		if(islog){
			Log.d("dth", s);
		}
	}
}
