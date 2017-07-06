package com.eshangke.framework.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.eshangke.framework.ui.activities.event.TouchEventChilds;
import com.eshangke.framework.ui.activities.event.TouchEventFather;
import com.eshangke.framework.ui.activities.event.TouchEventUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 类的说明：
 * cd D:\android\AndroidStudioProjects\FrameWork\framework\src\main>
 * javah -d jni -classpath D:\android\android-sdk\platforms\android-23\android.jar;D:\android\android-sdk\extras\android\support\v7\appcompat\libs\android-support-v7-appcompat.jar;D:\android\android-sdk\extras\android\support\v4\android-support-v4.jar;..\..\build\intermediates\classes\debug com.eshangke.framework.ui.activities.NDKActivity
 * 作者：shims
 * 创建时间：2016/8/3 0003 11:52
 */
public class NDKActivity extends BaseActivity{
    @BindView(R.id.father)
    TouchEventFather father;

    @BindView(R.id.childs)
    TouchEventChilds childs;

    @BindView(R.id.jni)
    Button jni;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    static {
        System.loadLibrary("myjni");//导入生成的链接库文件
    }


    public native String getStringFromNative();//本地方法


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jni);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @OnClick({R.id.father, R.id.childs, R.id.jni})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.father:
                Toast.makeText(this, "father", Toast.LENGTH_SHORT).show();
                break;
            case R.id.childs:
                Toast.makeText(this, "childs", Toast.LENGTH_SHORT).show();
                break;
            case R.id.jni:
                Toast.makeText(this, getStringFromNative(), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    /**
     * 事件的分发
     *
     * 先捕获然后冒泡
     * 在捕获阶段，事件先由外部的View接收，然后传递给其内层的View，依次传递到更够接收此事件的最小View单元，完成事件捕获过程
     * 在冒泡阶段，事件则从事件源的最小View单元开始，依次向外冒泡，将事件对层传递。
     * 事件操作主要就是发生在View和ViewGroup之间
     *
     *
     * @param ev 所有的事件都由如下三个部分作为基础：  按下（ACTION_DOWN）  移动（ACTION_MOVE）  抬起（ACTION_UP）
     * @return
     * 返回true/false都只执行当前的onTouchEvent
     * 返回super.dispatchTouchEvent(ev)才传递给下一层
     *
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.w("eventTest", "Activity | dispatchTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 事件的处理
     * @param event
     * @return 返回true表示消费处理当前事件，返回false则不处理，交给子控件进行继续分发。
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.w("eventTest", "Activity | onTouchEvent --> " + TouchEventUtil.getTouchAction(event.getAction()));
        return super.onTouchEvent(event);
    }
}
