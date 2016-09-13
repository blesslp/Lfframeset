package com.liufan.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

public final class ImageUtils {
	/*
	 * 图片转换工具：
	 */



	public final static int px2dip(Context context,int px) {
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(px / scale + 0.5f); 
	}

	public final static int dip2px(Context context,int dip) {
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(dip * scale + 0.5f); 
	}



	/*
	 *************************************************************************************
	 *
	 *			接收的参数 ：  Bitmap为原图，
	 *			返回的参数:  Bitmap为转换后的圆角图
	 *
	 **************************************************************************************
	 */

	public final static Bitmap switchToRoundRectangle(Bitmap img) {
		int w = img.getWidth();
		int h = img.getHeight();
		if(h <= w) w = h;
		return switchImageToRound(img,w/6);
	}

	public final static Bitmap switchToRound(Bitmap img) {
		int w = img.getWidth();
		int h = img.getHeight();
		if(h <= w) w = h;
		return switchImageToRound(img,w/2);
	}

	public final static Bitmap switchImageToRound(Bitmap img,int arc) {
		//新建一个空白可以透明的画布
		Bitmap output = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Config.ARGB_8888);
		//得到该画布的画笔
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		//得到一个与原图同等大小的矩形 
		Rect rect = new Rect(0,0,img.getWidth(),img.getHeight());
		//得到一个圆角矩形
		RectF rectF = new RectF(rect);
		//打开抗锯齿
		paint.setAntiAlias(true);
		//将新画布画成全透明色
		canvas.drawARGB(0, 0, 0, 0);
		//设置画笔为黑色
		paint.setColor(Color.BLACK);
		//画一个黑色的圆角矩形框
		canvas.drawRoundRect(rectF, arc, arc, paint);
		//将画笔调成蒙板模式，
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		//将原图画在蒙板上，因此黑色的圆角矩形框会显示内容
		canvas.drawBitmap(img, rect, rect,paint);

		//		if(img != null && !img.isRecycled()) {
		//			img.recycle();
		img = null;
		//		}

		return output;
	}

	/*
	 *************************************************************************************
	 *
	 *			接收的参数 ：  Bitmap为原图，w，  h  宽高
	 *			
	 *			返回的参数:  Bitmap为转换后的圆角图
	 *
	 **************************************************************************************
	 */
	public final static Bitmap zoomBitmap(Bitmap bitmap,int w,int h){
		//得到原图的宽
		int width = bitmap.getWidth();
		//得到原图的高
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		//得到缩放比例
		float scaleWidth = ((float)w / width);
		float scaleHeight = ((float)h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		//得到缩放后的新图形
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//		if(!bitmap.isRecycled()) {
////			bitmap.recycle();
//			bitmap = null;
//		}
		return newbmp;
	}

	/*
	 *************************************************************************************
	 *
	 *			将Drawable转换成Bitmap
	 *
	 **************************************************************************************
	 */
	public final static Bitmap drawableToBitmap(Drawable drawable){
		//得到Drawable的宽高
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		//得到Drawable的图像模式，
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
						: Config.RGB_565);
		//得到新建的画面的画笔
		Canvas canvas = new Canvas(bitmap);

		//设置drawable的边界
		drawable.setBounds(0,0,width,height);
		//用drawable把内容画到新bitmap中
		drawable.draw(canvas);

		return bitmap;
	} 
	/*
	 *************************************************************************************
	 *
	 *			将原图转化为带倒影的图片 （反转+渐变）
	 *
	 **************************************************************************************
	 */
	public final static Bitmap createReflectionImageWithOrigin(Bitmap bitmap){
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		//这是原图一半高度的，用来做倒影
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap,
				0, height/2, width, height/2, matrix, false);
		//这是原图的高度加倒影的高度的画布，这个将作为最终效果图
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Config.ARGB_8888);

		//得到目标图的画笔
		Canvas canvas = new Canvas(bitmapWithReflection);
		//		先将原图画在目标图上
		canvas.drawBitmap(bitmap, 0, 0, null);
		//		----------------------------------------------
		//新建一个画笔模式
		Paint defaultPaint = new Paint();
		//在原图下面画一个矩形
		canvas.drawRect(0, height,width,height + reflectionGap,
				defaultPaint);
		//把倒影的图画上去
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		Paint paint = new Paint();
		//新建一个线性渐变，设置渐变的起点x,y与终点x,y,起始色与终止色
		LinearGradient shader = new LinearGradient(0,
				bitmap.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		//将渐变设置到画笔模式上
		paint.setShader(shader);
		//设置渲染的模式，
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		//将渲染画到倒影的位置，
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		//		if(bitmap != null && !bitmap.isRecycled()) {
		//			bitmap.recycle();
		bitmap = null;
		//		}

		return bitmapWithReflection;
	} 
}
