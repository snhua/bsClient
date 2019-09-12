package top.ilikecode.thelastone.utils;

import android.graphics.*;

public class BitmapUtils {

    //TODO 缩放位图
    public static Bitmap getSmallBitmap(Bitmap bitmap, int width, int height){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scaleW = width/w;
        float scaleH = height/h;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH); // 长和宽放大缩小的比例
        Bitmap bit = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return bit;
    }

    //TODO 裁剪圆形用户头像
    public static Bitmap createCircleBitmap(Bitmap resource) {
        //获取图片的宽度
        int width = resource.getWidth();
        Paint paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);

        //创建一个与原bitmap一样宽度的正方形bitmap
        Bitmap circleBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        //以该bitmap为低创建一块画布
        Canvas canvas = new Canvas(circleBitmap);
        //以（width/2, width/2）为圆心，width/2为半径画一个圆
        canvas.drawCircle(width/2, width/2, width/2, paint);

        //设置画笔为取交集模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //裁剪图片
        canvas.drawBitmap(resource, 0, 0, paint);

        return circleBitmap;
    }
}
