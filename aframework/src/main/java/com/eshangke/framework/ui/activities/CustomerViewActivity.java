package com.eshangke.framework.ui.activities;

import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.eshangke.framework.R;
import com.eshangke.framework.util.ToastUtil;
import com.eshangke.framework.widget.ArcMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述: 自定义view例子
 * 创建人: shims
 * 创建时间: 16/9/6 15:59
 */
public class CustomerViewActivity extends BaseActivity {
    ArcMenu arcMenu;
    ListView listView;
    private List<String> mDatas;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customerview);

        arcMenu = (ArcMenu) findViewById(R.id.arcmenu);
        listView = (ListView) findViewById(R.id.listview);

        mDatas = new ArrayList<String>();
        for (int i = 'A'; i < 'Z'; i++) {
            mDatas.add((char) i + "");
        }

        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mDatas));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (arcMenu.isOpen())
                    arcMenu.toggleMenu(600);
            }
        });


        arcMenu.setOnMenuItemClickListener((view, pos) -> ToastUtil.showToast(CustomerViewActivity.this,  pos+":"+view.getTag(), Toast.LENGTH_LONG));
    }
}
