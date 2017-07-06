package com.eshangke.framework.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类的说明：Bitmap工具类
 * 作者：shims
 * 创建时间：2016/2/2 0002 11:03
 */
public class BitmapUtil {
    /**
     * 从给定路径加载图片
     *
     * @param imgpath
     * @return
     */
    public static Bitmap loadBitmap(String imgpath) {
        return BitmapFactory.decodeFile(imgpath);
    }

    /**
     * 获得载图片方位
     *
     * @param imgpath
     * @return
     */
    public static int getBitmapDigree(String imgpath) {
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgpath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 270;
                    break;
                default:
                    digree = 0;
                    break;
            }
        }
        return digree;
    }

    /**
     * 从给定的路径加载图片，并自动矫正方位
     *
     * @param imgpath
     * @param digree
     * @return
     */
    public static Bitmap loadMatrixBitmap(String imgpath, int digree) {
        Bitmap reBm = null;
        Bitmap bm = loadBitmap(imgpath);
        if (digree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(digree);
            reBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
        }
        if (null == reBm) {
            return bm;
        }
        if (null != bm && !bm.isRecycled()) {
            bm.recycle();
        }
        return reBm;
    }

    /**
     * 将bitmap保存至本地.
     *
     * @param bitmap
     * @return
     * @author 史明松
     * @update 2014年11月5日 下午3:03:54
     */
    public static File saveBitmapFile(Bitmap bitmap, String savePath) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File cutTempFile = new File(savePath + timeStamp + "_s.jpg");
        if (cutTempFile.exists()) {
            cutTempFile.delete();
        }
        try {
            cutTempFile.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cutTempFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            Log.e(BitmapUtil.class.getName(), e.toString());
        }
        String newTempPath2 = savePath + System.currentTimeMillis() + "_s.jpg";
        String cutTempFile2 = BitmapCompressUtil.getSmallBitmapAndSave(cutTempFile.getAbsolutePath(), newTempPath2, 100, 100);
        if (cutTempFile2 != null && new File(cutTempFile2).exists() && new File(cutTempFile2).length() > 0) {
            cutTempFile.delete();
            return new File(cutTempFile2);
        }
        return cutTempFile;
    }

    /**
     * 得到一个Bitmap，这个Bitmap的宽高为原图宽高的n次幂分之一 很接近但不太可能等于指定值
     *
     * @param path          路径
     * @param width         期望的宽度
     * @param height        期望的高度
     * @param hasAlphe      是否有透明度
     * @param isNearlySmall 缩放时不能正好返回N次幂的时候是否采用偏小的图片
     * @return
     */
    public static Bitmap getSavedBitmap(String path, int width, int height, boolean hasAlphe, boolean isNearlySmall) {
        Bitmap bitmap = null;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        if (!hasAlphe) {
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        if (width <= 0 && height > 0) {
            //一边为0，不能进行
            return null;
        } else if (width > 0 && height <= 0) {
            //一边为0，不能进行
            return null;
        } else if (width > 0 && height > 0) {
            int size = 0;
            //两边都大于0，普通
            size = Math.max(width, height);
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            int big = Math.max(opts.outWidth, opts.outHeight);
            int index = 1;
            boolean isGo = true;
            int maxSize = 0;
            int minSize = 0;
            // 如果图比预留图片位置小，不做处理
            if (big > size) {
                while (isGo) {
                    maxSize = big / index;
                    index *= 2;
                    minSize = big / index;

                    // 每次检测一个范围
                    // 如果本次计算的结果等于期望的长度，即可完美的结束缩放
                    if (size == minSize || size == maxSize) {
                        isGo = false;
                    }
                    // 并不能完美的缩放。所得为近似值，在n次幂与n-1次幂之间
                    if (size > minSize && size < maxSize) {
                        isGo = false;
                    }
                }
            }
            if (!isNearlySmall) {
                index /= 2;
                if (index <= 0) {
                    index = 1;
                }
            }
            opts.inSampleSize = index;
            opts.inJustDecodeBounds = false;

            try {
                bitmap = BitmapFactory.decodeFile(path, opts);
            } catch (Exception e) {
            }
        } else if (width == 0 && height == 0) {
            //两边都为0，取值时为自增，直接取原始图片
            try {
                bitmap = BitmapFactory.decodeFile(path, opts);
            } catch (Exception e) {
            }
        }

        return bitmap;
    }


    /**
     * 获取图片文件的信息，是否旋转了90度，如果是则反转
     *
     * @param bitmap 需要旋转的图片
     * @param path   图片的路径
     */
    public static Bitmap reviewPicRotate(Bitmap bitmap, String path) {
        int degree = getPicRotate(path);
        if (degree != 0) {
            Matrix m = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            m.setRotate(degree); // 旋转angle度
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片
        }
        return bitmap;
    }

    /**
     * 读取图片文件旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片旋转的角度
     */
    public static int getPicRotate(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param iv     图片控件
     * @param angle  旋转的角度
     * @param bitmap 传入需要旋转的图片
     * @return Bitmap 旋转之后的图片
     */
    public static void rotaingImageView(ImageView iv, int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        iv.setImageBitmap(resizedBitmap);
    }

    /**
     * 把传入的矩形Bitmap处理成圆角，并添加自定义颜色的边框
     *
     * @param bmp              需要修改的Bitmap
     * @param roundAngleWidth  圆角宽度
     * @param roundAngleHeight 圆角高度
     * @param ringColor        十六进制颜色代码
     * @param ringHeight       外环的颜色
     * @return
     */
    //图片圆角处理
    public static Bitmap getRoundedBitmap(Bitmap bmp, int roundAngleWidth, int roundAngleHeight, int ringColor, int ringHeight) {
        //创建用来处理的位图
        Bitmap tempBmp = getRoundedBitmap(bmp, roundAngleWidth, roundAngleHeight, ringHeight);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(tempBmp);
        paint.setColor(ringColor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        Rect rect = new Rect(0, 0, tempBmp.getWidth(), tempBmp.getHeight());
        RectF rectf = new RectF(rect);
        canvas.drawRoundRect(rectf, roundAngleWidth, roundAngleHeight, paint);
        return tempBmp;
    }

    /**
     * 把传入的矩形Bitmap处理成圆角
     *
     * @param bmp
     * @param roundAngleWidth
     * @param roundAngleHeight
     * @return
     */
    public static Bitmap getRoundedBitmap(Bitmap bmp, int roundAngleWidth, int roundAngleHeight) {
        return getRoundedBitmap(bmp, roundAngleWidth, roundAngleHeight, 0);
    }

    /**
     * @param bmp
     * @param roundAngleWidth
     * @param roundAngleHeight
     * @param ringWH
     * @return
     */
    //图片圆角处理
    private static Bitmap getRoundedBitmap(Bitmap bmp, int roundAngleWidth, int roundAngleHeight, int ringWH) {
        //创建用来处理的位图
        Bitmap tempBmp = Bitmap.createBitmap(bmp.getWidth() + ringWH * 2, bmp.getHeight() + ringWH * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tempBmp);
        Paint paint = new Paint();
        Rect srcRect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        Rect roundRect = new Rect(ringWH, ringWH, tempBmp.getWidth() - ringWH, tempBmp.getHeight() - ringWH);

//        dstRect.offset(ringWH, ringWH);
        paint.setAntiAlias(true);
        //先绘制圆角矩形
        canvas.drawRoundRect(new RectF(roundRect), roundAngleWidth, roundAngleHeight, paint);

        //设置图像的叠加模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //绘制图像
        canvas.drawBitmap(bmp, srcRect, roundRect, paint);
        return tempBmp;
    }

    /**
     * 把传入的矩形Bitmap处理成圆形，并添加边框
     *
     * @param bmp
     * @param ringWH
     * @param ringColor
     * @return
     */
    public static Bitmap getCircularBitmap(Bitmap bmp, int ringWH, int ringColor) {
        Bitmap tempBmp = getCircularBitmap(bmp, ringWH);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(tempBmp);
        paint.setColor(ringColor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        int r = tempBmp.getWidth() / 2;
        canvas.drawCircle(r, r, r, paint);
        return tempBmp;
    }

    /**
     * 把传入的矩形Bitmap处理成圆形
     *
     * @param bmp
     * @return
     */
    public static Bitmap getCircularBitmap(Bitmap bmp) {
        return getCircularBitmap(bmp, 0);
    }

    /**
     * 把传入的矩形Bitmap处理成圆形
     *
     * @param bmp
     * @param ringWH
     * @return
     */
    private static Bitmap getCircularBitmap(Bitmap bmp, int ringWH) {
        //判断宽和高哪个短一些
        int minLength = Math.min(bmp.getWidth(), bmp.getHeight());
        //取得半径
        int r = minLength / 2;

        //创建用来处理的位图，矩形
        Bitmap tempBmp = Bitmap.createBitmap(minLength + ringWH * 2, minLength + ringWH * 2, Bitmap.Config.ARGB_8888);
        //准备在tempBmp上绘制
        Canvas canvas = new Canvas(tempBmp);
        Paint paint = new Paint();

        //判断矩形需要位移的距离
        int offset = bmp.getWidth() - bmp.getHeight();
        int offsetX = 0;
        int offsetY = 0;
        if (offset > 0) {
            //宽度大于高度的场合，宽度位移offset/2
            offsetX = offset / 2;
        } else if (offset < 0) {
            //宽度大于高度的场合，宽度位移offset/2
            offsetY = Math.abs(offset) / 2;
        }
        //准备一个矩形，这个矩形用于在源图像指定截图的区域
        Rect oriRect = new Rect(offsetX, offsetY, minLength + offsetX, minLength + offsetY);
        //目标位置
        Rect targetRect = new Rect(ringWH, ringWH, minLength + ringWH, minLength + ringWH);
        paint.setAntiAlias(true);
        //先绘制圆
        canvas.drawCircle(r + ringWH, r + ringWH, r, paint);

        //设置图像的叠加模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //绘制图像
        canvas.drawBitmap(bmp, oriRect, targetRect, paint);
        return tempBmp;
    }

}
