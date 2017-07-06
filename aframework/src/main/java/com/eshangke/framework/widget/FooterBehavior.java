package com.eshangke.framework.widget;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

/**
 * 类的说明：
 * 作者：shims
 * 创建时间：2016/10/24 0024 17:07
 */
public class FooterBehavior extends CoordinatorLayout.Behavior<View> {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();


    private int sinceDirectionChange;


    public FooterBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 判断child的布局是否依赖dependency
     * 返回false表示child不依赖dependency，ture表示依赖
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        //设定依赖的父布局为AppBarLayout
        return dependency instanceof AppBarLayout;
    }

    /**
     *  当dependency发生改变时（位置、宽高等），执行这个函数
     *  返回true表示child的位置或者是宽高要发生改变，否则就返回false
     * @param parent CoordinatorLayout
     * @param child  要执行动作的CoordinatorLayout的子View
     * @param dependency child依赖的View
     * @return return 如果找到了你依赖的那个View就返回true
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float translationY = Math.abs(dependency.getTranslationY());//取绝对值
        child.setTranslationY(translationY);
        return true;
    }

    /**
     * 判断滑动的方向  是横轴和纵轴
     * @param coordinatorLayout
     * @param child
     * @param directTargetChild
     * @param target
     * @param nestedScrollAxes
     * @return
     */
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    /**
     * 根据滑动的距离显示和隐藏footer view
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param dx
     * @param dy
     * @param consumed
     */
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        if (dy > 0 && sinceDirectionChange < 0 || dy < 0 && sinceDirectionChange > 0) {
            child.animate().cancel();
            sinceDirectionChange = 0;
        }
        sinceDirectionChange += dy;
        if (sinceDirectionChange > child.getHeight() && child.getVisibility() == View.VISIBLE) {
            hide(child);
        } else if (sinceDirectionChange < 0 && child.getVisibility() == View.GONE) {
            show(child);
        }
    }

    /**
     * 当target正尝试滑动或者已经滑动时候调用这个方法
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param dxConsumed
     * @param dyConsumed
     * @param dxUnconsumed
     * @param dyUnconsumed
     */
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    private void hide(final View view) {
        ViewPropertyAnimator animator = view.animate().translationY(view.getHeight()).setInterpolator(INTERPOLATOR).setDuration(200);
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                show(view);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }


    private void show(final View view) {
        ViewPropertyAnimator animator = view.animate().translationY(0).setInterpolator(INTERPOLATOR).setDuration(200);
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                hide(view);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();

    }
}