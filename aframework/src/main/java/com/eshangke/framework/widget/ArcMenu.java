package com.eshangke.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.eshangke.framework.R;

/**
 * 1.自定义属性
 * attr.xml
 * 在布局文件中使用
 * 自定义控件中进行读取
 * 2.onMeasure
 * 3.onLayout
 * 4.设置主按钮的旋转动画
 * 为menuItem添加平移动画和旋转动画
 * 实现menuItem动画
 *
 * 类描述: 卫星菜单
 * 创建人: shims
 * 创建时间: 16/9/7 15:09
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {

    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;

    //菜单位置
    private Position mPosition = Position.LEFT_BOTTOM;

    //菜单半径
    private int mRadius;

    //默认半径
    private int defaultRadius;

    //菜单状态
    private Status mCurrentStatus = Status.CLOSE;

    //菜单的主按钮
    private View mCButton;

    private OnMenuItemClickListener mOnMenuItemClickListener;

    public void setOnMenuItemClickListener(OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }


    public enum Status {
        OPEN, CLOSE
    }

    public enum Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //把对应的值转化为实际屏幕上的点值，也就是像素值 COMPLEX_UNIT_DIP是单位，100是数值，也就是100dip。
        defaultRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        //获得自定义属性的值
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcMenu, defStyleAttr, 0);
        int pos = typedArray.getInt(R.styleable.ArcMenu_position, POS_RIGHT_BOTTOM);
        switch (pos) {
            case POS_LEFT_TOP:
                mPosition = Position.LEFT_TOP;
                break;
            case POS_LEFT_BOTTOM:
                mPosition = Position.LEFT_BOTTOM;
                break;
            case POS_RIGHT_TOP:
                mPosition = Position.RIGHT_TOP;
                break;
            case POS_RIGHT_BOTTOM:
                mPosition = Position.RIGHT_BOTTOM;
                break;
        }
        /**
         * 基于当前DisplayMetrics进行转换，获取指定资源id对应的尺寸
         * getDimensionPixelSize() 是四舍五入
         * getDimensionPixelOffset()是取整
         */
        mRadius = (int) typedArray.getDimension(R.styleable.ArcMenu_radius,defaultRadius);
        Log.e("TAG", "positon = " + mPosition + ",radius= " + mRadius);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            //测量child
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutCButton();

            int count = getChildCount();
            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i + 1);
                child.setVisibility(View.GONE);
                /**
                 * 已知圆心坐标和半径,求各个菜单坐标
                 * menu1: 0,radius
                 * menu1: radius*sina,radius*cosa
                 * menu2: radius*sin2a,radius*cos2a
                 * a=90/(菜单个数-1)
                 */
                int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
                int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();
                // 如果菜单位置在底部 左下，右下  ct=屏幕高度-菜单控件本身高度-菜单控件的高度
                if (mPosition == Position.LEFT_BOTTOM || mPosition == Position.RIGHT_BOTTOM) {
                    ct = getMeasuredHeight() - cHeight - ct;
                }

                //如果右上 右下
                if (mPosition == Position.RIGHT_TOP || mPosition == Position.RIGHT_BOTTOM) {
                    cl = getWidth() - cWidth - cl;
                }

                child.layout(cl, ct, cl + cWidth, ct + cHeight);
            }
        }
    }

    private void layoutCButton() {
        mCButton = getChildAt(0);
        mCButton.setOnClickListener(this);

        int l = 0;
        int t = 0;
        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();
        switch (mPosition) {
            case LEFT_TOP:
                l = 0;
                t = 0;
                break;
            case LEFT_BOTTOM:
                l = 0;
                t = getMeasuredHeight() - height;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width;
                t = 0;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width;
                t = getMeasuredHeight() - height;
                break;
        }

        mCButton.layout(l, t, l + width, t + width);
    }

    @Override
    public void onClick(View v) {
        rotateCButton(v, 0f, 360f, 300);

        toggleMenu(300);
    }

    //切换menu
    public void toggleMenu(int duration) {
        //为menuItem添加平移动画
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            final View childView = getChildAt(i + 1);
            childView.setVisibility(View.VISIBLE);

            //end 0,0
            //start
            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

            int xflag = 1;
            int yflag = 1;
            if (mPosition == Position.LEFT_TOP || mPosition == Position.LEFT_BOTTOM) {
                xflag = -1;
            }
            if (mPosition == Position.LEFT_TOP || mPosition == Position.RIGHT_TOP) {
                yflag = -1;
            }

            AnimationSet animset = new AnimationSet(true);
            Animation tranAnim = null;

            if (mCurrentStatus == Status.CLOSE) {
                tranAnim = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
                childView.setClickable(false);
                childView.setFocusable(false);
            } else {
                tranAnim = new TranslateAnimation(0, xflag * cl, 0, yflag * ct);
                childView.setClickable(true);
                childView.setFocusable(true);
            }

            tranAnim.setFillAfter(true);
            tranAnim.setDuration(duration);
            tranAnim.setStartOffset((i * 100) / count);

            tranAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE) {
                        childView.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            RotateAnimation rotateAnim = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(duration);
            rotateAnim.setFillAfter(true);
            animset.addAnimation(rotateAnim);
            animset.addAnimation(tranAnim);
            childView.startAnimation(animset);

            final int pos = i + 1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.OnClick(childView, pos);
                        menuItemAnim(pos - 1);
                        changeStatus();
                    }
                }
            });
        }
        changeStatus();
    }

    /**
     * 添加menuItem的点击动画
     *
     * @param pos
     */
    private void menuItemAnim(int pos) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View childView = getChildAt(i + 1);
            if (i == pos) {
                childView.startAnimation(scaleBigAnim(300));
            } else {
                childView.startAnimation(scaleSmallAnim(300));
            }
        }
    }

    private Animation scaleSmallAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0.0f);
        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    /**
     * 为当前点击的Item设置变大和透明度降低的动画
     *
     * @param duration
     * @return
     */
    private Animation scaleBigAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0.0f);
        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    //切换菜单状态
    private void changeStatus() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
    }

    public boolean isOpen() {
        return mCurrentStatus == Status.OPEN;
    }

    //设置主按钮的旋转动画
    private void rotateCButton(View v, float start, float end, int duration) {
        RotateAnimation anim = new RotateAnimation(start, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(duration);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    //点击子菜单项的回调接口
    public interface OnMenuItemClickListener {
        void OnClick(View view, int pos);
    }
}
