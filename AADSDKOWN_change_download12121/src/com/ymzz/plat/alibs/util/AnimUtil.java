package com.ymzz.plat.alibs.util;


import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class AnimUtil {

	public static void startAanimRom(Context context, ImageView show_AdPic,
			final ImageView show_picad_pic_close) {

//		Animation scale_rotate_in = AnimationUtils.loadAnimation(context,
//				R.anim.scale_rotate_in);
//		Animation scale_alpha_in = AnimationUtils.loadAnimation(context,
//				R.anim.scale_alpha_in);
//		Animation scale_tra_in = AnimationUtils.loadAnimation(context,
//				R.anim.scale_tras_in);
//		Animation scale_tra_in_FROMY = AnimationUtils.loadAnimation(context,
//				R.anim.scale_tras_in_fromy);
//		Animation scale_in = AnimationUtils.loadAnimation(context,
//				R.anim.scale_in);
		
		Animation scale_rotate_in = AnimationUtils.loadAnimation(context,GetResourseIdSelf.getResourseIdByName(context.getPackageName(),"anim","scale_rotate_in"));
		Animation scale_alpha_in = AnimationUtils.loadAnimation(context,GetResourseIdSelf.getResourseIdByName(context.getPackageName(),"anim","scale_alpha_in"));
		Animation scale_tra_in = AnimationUtils.loadAnimation(context,GetResourseIdSelf.getResourseIdByName(context.getPackageName(),"anim","scale_tras_in"));
		Animation scale_tra_in_FROMY = AnimationUtils.loadAnimation(context,GetResourseIdSelf.getResourseIdByName(context.getPackageName(),"anim","scale_tras_in_fromy"));
		Animation scale_in = AnimationUtils.loadAnimation(context,GetResourseIdSelf.getResourseIdByName(context.getPackageName(),"anim","scale_in"));
		
		int randomInt = getRandom();

		if (randomInt == 0) {
			startAnim(show_AdPic, scale_alpha_in, show_picad_pic_close,
					scale_in);
		} else if (randomInt == 1) {
			startAnim(show_AdPic, scale_rotate_in, show_picad_pic_close,
					scale_in);
		} else if (randomInt == 2) {
			startAnim(show_AdPic, scale_tra_in, show_picad_pic_close, scale_in);
		} else if (randomInt == 3) {
			startAnim(show_AdPic, scale_tra_in_FROMY, show_picad_pic_close,
					scale_in);
		} else {
			startAnim(show_AdPic, scale_rotate_in, show_picad_pic_close,
					scale_in);

		}
	}

	private static void startAnim(final View show_AdPic, Animation anim,
			final View pic_close, final Animation animClose) {

		pic_close.setVisibility(View.GONE);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				pic_close.setVisibility(View.VISIBLE);
				pic_close.startAnimation(animClose);

			}
		});
		show_AdPic.startAnimation(anim);

	}

	private static int getRandom() {
		return (int) Math.round(Math.random() * 3);
	}
}
