package com.eshangke.framework.util;

import android.content.res.Resources;
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
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片压缩器.
 *
 * @author 史明松
 *         <p>
 *         压缩原理： 1.获取原始图片的长和宽 2.计算压缩比例并缩放 3.对图片质量进行压缩
 */
public class BitmapCompressUtil {
    /**
     * 计算图片的缩放值.
     *
     * @param options
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     * @return
     * @author 史明松
     * @update 2014年6月8日 下午11:47:40
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;// 1表示不缩放

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 图片按比例大小压缩.
     *
     * @param image
     * @return
     * @author 史明松
     * @update 2014年6月9日 上午12:07:01
     */
    public static Bitmap compImageFromBitmapdd(Bitmap image) {
        int x = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出

        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 10, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options options = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, options);

        // 换算合适的图片缩放值
        // options.inSampleSize = calculateInSampleSize(options, 360, 600);
        options.inSampleSize = 4;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // inPurgeable 设定为 true，可以让java系统, 在内存不足时先行回收部分的内存
        options.inPurgeable = true;
        // 减少对Aphla 通道
        // options.inPreferredConfig = Bitmap.Config.RGB_565;
        // options.inTempStorage = new byte[1000 * 1024];

        isBm = new ByteArrayInputStream(baos.toByteArray());
        if (bitmap != null && bitmap.isRecycled()) {
            bitmap.recycle();
        }
        Bitmap tempbitmap = BitmapFactory.decodeStream(isBm, null, options);
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        baos = null;

        // bitmap = Bitmap.createScaledBitmap(tempbitmap, options.outWidth,
        // options.outHeight, false);
        return tempbitmap;
    }

    /**
     * 图片质量循环压缩.
     *
     * @param bitmap
     * @param options
     * @return
     * @author 史明松
     * @update 2014年6月9日 下午3:01:45
     */
    private static Bitmap compressImage(Bitmap bitmap, BitmapFactory.Options options) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int quality = 90;

        while (baos.toByteArray().length / 1024 > 100 && quality > 0) { // 循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            quality -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        bitmap = BitmapFactory.decodeStream(isBm, null, options);// 把ByteArrayInputStream数据生成图片
        try {
            baos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        baos = null;

        return bitmap;
    }

    /**
     * 把Bitmap转换成Base64.
     *
     * @param bitmap
     * @return
     * @author 史明松
     * @update 2014年6月8日 下午11:53:05
     */
    public static String getBitmapStrBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, 0);
    }

    /**
     * 把Base64转换成Bitmap.
     *
     * @param iconBase64
     * @return
     * @author 史明松
     * @update 2014年6月8日 下午11:53:15
     */
    public static Bitmap getBitmapFromBase64(String iconBase64) {
        byte[] bitmapArray = Base64.decode(iconBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
    }

    /**
     * 这里用一句话描述这个方法的作用.
     *
     * @param bmp
     * @param name  路径
     * @param quity 压缩比例
     * @return
     * @author 史明松
     * @update 2012-8-24 下午3:25:18
     * 这个方法被saveBitmapToJPG所代替
     */
//
//    public static String  {
//
//        return saveBitmap(bmp, null, name, quity);
//    }


    /***
     * 这个方法作用：根据(oldName)的文件生成的bmp，转换生成新的文件（newName），(oldName)生成原始文件会删除，生成新文件（
     * newName）
     *
     * @param bmp     bitmap 图像
     * @param oldName 文件原始名字 原始文件会删除
     * @param newName 文件压缩后需要保存的新路径
     * @param quity   压缩质量
     * @return
     * @author 史明松
     * @update 2014-6-26 下午4:23:52
     */
    public static String saveBitmap(Bitmap bmp, String oldName, String newName, int quity) {
        if (TextUtils.isEmpty(newName)) {
            return null;
        }
        File pic = new File(newName);
        if (pic.exists()) {
            pic.delete();
        }
        try {
            pic.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(pic);
            bmp.compress(Bitmap.CompressFormat.JPEG, quity, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("saveBmp is here");
        } catch (Exception e) {

        }
        return pic.getPath();
    }

    /**
     * 将一个Bitmap存储成JPG
     *
     * @param bmp     将要存储的Bitmap
     * @param newName 文件存储路径
     * @param quity   压缩时的图片质量
     * @return
     */
    public static String saveBitmapToJPG(Bitmap bmp, String newName, int quity) {
        if (TextUtils.isEmpty(newName)) {
            return null;
        }
        File pic = new File(newName);
        if (pic.exists()) {
            pic.delete();
        }
        try {
            pic.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(pic);
            bmp.compress(Bitmap.CompressFormat.JPEG, quity, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("saveBmp is here");
        } catch (Exception e) {

        }
        return pic.getPath();
    }

//    /**
//     * 将bitmap保存至本地.
//     * 与saveBitmapToJPG用法相同，写死了存储路径
//     * @param bitmap
//     * @return
//     * @author 史明松
//     * @update 2014年11月5日 下午3:03:54
//     */
//    public static File saveBitmapToJPG(Bitmap bitmap, String savePath) {
//        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//        File cutTempFile = new File(savePath + timeStamp + "_s.jpg");
//        if (cutTempFile.exists()) {
//            cutTempFile.delete();
//        }
//        try {
//            cutTempFile.createNewFile();
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cutTempFile));
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            bos.flush();
//            bos.close();
//        } catch (Exception e) {
//            Log.e(BitmapUtil.class.getName(), e.toString());
//        }
//        String newTempPath2 = savePath + System.currentTimeMillis() + "_s.jpg";
//        String cutTempFile2 = BitmapCompressUtil.getSmallBitmapAndSave(cutTempFile.getAbsolutePath(), newTempPath2, 100, 100);
//        if (cutTempFile2 != null && new File(cutTempFile2).exists() && new File(cutTempFile2).length() > 0) {
//            cutTempFile.delete();
//            return new File(cutTempFile2);
//        }
//        return cutTempFile;
//    }

//    /**路径转换
//     * @param imgPath
//     * @return
//     */
//    public static String Big2Small(String imgPath) {
//        if (TextUtils.isEmpty(imgPath)) {
//            return "";
//        }
//        String imgBodyP = "";
//        if (imgPath.lastIndexOf(".") != -1) {
//            String imgBodyS = imgPath.substring(0, imgPath.lastIndexOf("."));
//            String imgBodyS2 = imgPath.substring(imgPath.lastIndexOf("."), imgPath.length());
//            imgBodyP = imgBodyS + "_s" + System.currentTimeMillis() + imgBodyS2;
//        }
//        return imgBodyP;
//
//    }

    /**
     * 根据路径获得图片并压缩返回bitmap用于显示
     *
     * @param filePath     图片路径
     * @param expectWidth  期望的高度
     * @param expectHeight 期望的宽度
     * @return
     */
    public static Bitmap getBitmap(String filePath, int expectWidth, int expectHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, expectWidth, expectHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /****
     * 这个方法作用：压缩剪裁后的图片吗，会删除传进来的大图
     *
     * @param filePath 原始文件路径
     * @param maxSize  保存文件最大大小
     * @param quity    默认质量 40
     * @return 返回压缩后新文件路径
     * @author 史明松
     * @update 2014-6-26 上午10:39:40
     */
    public static String getSmallBitmapAndSave(String filePath, String newPath, int maxSize, int quity) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 540, 960);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        String last = saveBitmap(bitmap, filePath, newPath, quity);
        quity -= 10;
        while (last != null && new File(last).exists() && new File(last).length() / 1024 > maxSize && quity >= 10) {
            last = saveBitmap(bitmap, last, newPath, quity);
            quity -= 10;
        }
        if (last == null || !new File(last).exists() || new File(last).length() == 0) {
            last = filePath;
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return last;
    }

//    /**
//     * 从给定路径加载图片
//     *
//     * @param imgpath
//     * @return
//     */
//    public static Bitmap loadBitmap(String imgpath) {
//        return BitmapFactory.decodeFile(imgpath);
//    }

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
        Bitmap bm = BitmapFactory.decodeFile(imgpath);
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
     * 例:
     * oriWidth=100,
     * changedWidth=50;
     * oriHeight=100;
     * changedHeight=20;
     * p1=changedWidth/oriWidth=0.5;
     * p2=changedHeight/oriHeight=0.2;
     * p2更小些,如果要求图像比例均衡,那么使用变动后较小的height比较划算
     * <p>
     * 例2:
     * oriWidth=100,
     * changedWidth=200;
     * oriHeight=100;
     * changedHeight=100;
     * p1=changedWidth/oriWidth=2;
     * p2=changedHeight/oriHeight=1;
     * p2更小一些,如果要求图像比例均衡,那么使用变动后较小的height比较划算
     *
     * @param oriWidth
     * @param oriHeight
     * @param changedWidth
     * @param changedHeight
     * @return true:使用width作为依据;false:使用height作为依据
     */
    private static boolean checkMaxDdge(int oriWidth, int oriHeight, int changedWidth, int changedHeight) {
        if (changedWidth / oriWidth < changedHeight / oriHeight) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 读取App资源以外的本地图片，这个Bitmap的宽高为尽可能的接近指定值
     *
     * @param path     路径
     * @param width    期望的宽度
     * @param height   期望的高度
     * @param hasAlphe 是否有透明度
     * @return
     */
    public static Bitmap getBitmap(String path, float width, float height, boolean hasAlphe) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inSampleSize = 1;
        BitmapFactory.decodeFile(path, opts);
        if (!hasAlphe) {
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        // 检测边长收缩
        // p1或p2的值大于1的情况说明伸展了
        float p1 = width / opts.outWidth;
        float p2 = height / opts.outHeight;
        if (p1 >= 1 && p2 >= 1) {
            // 都大于1，返回原始的图像
        } else if (p1 < 1 || p2 < 1) {
            // 使用比例更小的一边做样本，true为使用width
            boolean minDdgeIsWidth = p1 < p2 ? true : false;
            boolean isGo = true;
            int maxSize = 0;
            int minSize = 0;
            int size;
            if (minDdgeIsWidth) {
                size = (int) width;
            } else {
                size = (int) height;

            }
            while (isGo) {
                if (minDdgeIsWidth) {
                    maxSize = opts.outWidth;
                } else {
                    maxSize = opts.outHeight;
                }
                opts.inSampleSize++;
                BitmapFactory.decodeFile(path, opts);
                if (minDdgeIsWidth) {
                    minSize = opts.outWidth;
                } else {
                    minSize = opts.outHeight;
                }

                // 每次检测一个范围
                // 如果本次计算的结果等于期望的长度，即可完美的结束缩放

                if (size == minSize || size == maxSize) {
                    isGo = false;
                }
                // 并不能完美的缩放。所得为近似值
                if (size > minSize && size < maxSize) {
                    isGo = false;
                }
            }
            if (maxSize > minSize) {
                int mid = (maxSize - minSize) / 2 + minSize;
                if (size > mid) {
                    opts.inSampleSize /= 2;
                }
            }
        }
        opts.inJustDecodeBounds = false;
        // Log.v("zzzzzzzzzzz", " " + opts.inSampleSize);
        try {
            bitmap = BitmapFactory.decodeFile(path, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 读取App资源Bitmap，这个Bitmap的宽高为尽可能的接近指定值(只处理收缩)
     *
     * @param res
     * @param id       资源ID
     * @param width    期望的宽度
     * @param height   期望的高度
     * @param hasAlphe 是否有透明度
     * @return
     */
    public Bitmap getBitmap(Resources res, int id, float width, float height,
                            boolean hasAlphe) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inSampleSize = 1;
        BitmapFactory.decodeResource(res, id, opts);
        if (!hasAlphe) {
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        // 检测边长收缩
        // p1或p2的值大于1的情况说明伸展了
        float p1 = width / opts.outWidth;
        float p2 = height / opts.outHeight;
        if (p1 >= 1 && p2 >= 1) {
            // 都大于1，返回原始的图像
        } else if (p1 < 1 || p2 < 1) {
            // 使用比例更小的一边做样本，true为使用width
            boolean minDdgeIsWidth = p1 < p2 ? true : false;
            boolean isGo = true;
            int maxSize = 0;
            int minSize = 0;
            int size;
            if (minDdgeIsWidth) {
                size = (int) width;
            } else {
                size = (int) height;

            }
            while (isGo) {
                if (minDdgeIsWidth) {
                    maxSize = opts.outWidth;
                } else {
                    maxSize = opts.outHeight;
                }
                opts.inSampleSize++;
                BitmapFactory.decodeResource(res, id, opts);
                if (minDdgeIsWidth) {
                    minSize = opts.outWidth;
                } else {
                    minSize = opts.outHeight;
                }

                // 每次检测一个范围
                // 如果本次计算的结果等于期望的长度，即可完美的结束缩放

                if (size == minSize || size == maxSize) {
                    isGo = false;
                }
                // 并不能完美的缩放。所得为近似值
                if (size > minSize && size < maxSize) {
                    isGo = false;
                }
            }
            if (maxSize > minSize) {
                int mid = (maxSize - minSize) / 2 + minSize;
                if (size > mid) {
                    opts.inSampleSize /= 2;
                }
            }
        }
        opts.inJustDecodeBounds = false;
        // Log.v("zzzzzzzzzzz", " " + opts.inSampleSize);
        try {
            bitmap = BitmapFactory.decodeResource(res, id, opts);
        } catch (Exception e) {
            e.printStackTrace();
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
