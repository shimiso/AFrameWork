package com.eshangke.framework.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eshangke.framework.R;

import java.util.ArrayList;


/**
 * 类的说明：底部导航
 * 作者：shims
 * 创建时间：2016/2/17 0017 11:26
 */
public class BottomTabActivity extends BaseActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    // 页面列表
    private ArrayList<Fragment> fragmentList;
    SampleFragmentPagerAdapter pagerAdapter;

    private int flag=0;
    private int indexCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(PageFragment.newInstance(1));
        fragmentList.add(PageFragment.newInstance(2));
        fragmentList.add(PageFragment.newInstance(3));
        pagerAdapter =
                new SampleFragmentPagerAdapter(getSupportFragmentManager(), this);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(pagerAdapter.getTabView(i));
            }
        }
        viewPager.setCurrentItem(1);

    }


    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[]{"TAB1", "TAB2", "TAB3"};
        private Context context;
        private FragmentManager fm;

        public View getTabView(final int position) {
            View v = LayoutInflater.from(context).inflate(R.layout.bottom_tab_view, null);
            TextView tv = (TextView) v.findViewById(R.id.textView);
            tv.setText(tabTitles[position]);
            ImageView img = (ImageView) v.findViewById(R.id.imageView);
            v.setOnClickListener(v1 -> {
                viewPager.setCurrentItem(position);
                if(position==1){
                    pagerAdapter.notifyDataSetChanged();
                }

            });

            //img.setImageResource(imageResId[position]);
            return v;
        }

        public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
            this.fm = fm;
        }


        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            //得到tag，这点很重要
            String fragmentTag = fragment.getTag();
            if(position==1){
                if (flag%2 == 0) {
                    FragmentTransaction ft = fm.beginTransaction();
                    //移除旧的fragment
                    ft.remove(fragment);
                    //换成新的fragment
                    fragment = PageFragment.newInstance(4);
                    //添加新fragment时必须用前面获得的tag，这点很重要
                    ft.add(container.getId(), fragment, fragmentTag);
                    ft.attach(fragment);
                    ft.commit();
                }else{
                    FragmentTransaction ft = fm.beginTransaction();
                    //移除旧的fragment
                    ft.remove(fragment);
                    //换成新的fragment
                    fragment = PageFragment.newInstance(2);
                    //添加新fragment时必须用前面获得的tag，这点很重要
                    ft.add(container.getId(), fragment, fragmentTag);
                    ft.attach(fragment);
                    ft.commit();
                }
                flag=flag+1;
            }


            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    public static class PageFragment extends Fragment {
        public static final String ARG_PAGE = "ARG_PAGE";

        private int mPage;

        public static PageFragment newInstance(int page) {
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, page);
            PageFragment fragment = new PageFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPage = getArguments().getInt(ARG_PAGE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.book_detail, container, false);
            TextView textView = (TextView) view.findViewById(R.id.tvInfo);
            textView.setText("Fragment #" + mPage);
            return view;
        }
    }
}